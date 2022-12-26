//
// Created by A11222 on 2022/11/9.
//
#include "VideoChannel.h"

/**
 *丢AVPacket,丢非I帧
 * @param q
 */
void dropPacket(queue<AVPacket *> &q) {
    LOGD("丢弃视频Packet");
    while (!q.empty()) {
        AVPacket *pk = q.front();
        if (pk->flags != AV_PKT_FLAG_KEY) {
            q.pop();
            BaseChannel::releaseAvPacket(pk);
        } else {
            break;
        }
    }
}

/**
 * 丢掉frame帧. 清空frame队列.
 * @param q
 */
void dropFrame(queue<AVFrame *> &q) {
    LOGE("丢弃视频Frame.....");
    while (!q.empty()) {
        AVFrame *frame = q.front();
        q.pop();
        BaseChannel::releaseAvFrame(frame);
    }
}

VideoChannel::VideoChannel(int id, JavaCallHelper *javaCallHelper, AVCodecContext *avCodecContext,
                           AVRational time_base, AVFormatContext *formatContext)
        : BaseChannel(id, javaCallHelper, avCodecContext, time_base) {
    LOGE("构造函数VideoChannel.....");
    //指针成员需要初始化，指向地址0处。否则会指向一个未知地址。
    // 导致未初始化的指针变量if（audioChannel）为true(audioChannel=0x12df41a812e98010，非0返回true)
    this->javaCallHelper = javaCallHelper;
    this->avCodecContext = avCodecContext;
    this->avFormatContext = formatContext;

    pkt_queue.setReleaseCallback(releaseAvPacket);
    pkt_queue.setSyncHandle(dropPacket);
    frame_queue.setReleaseCallback(releaseAvFrame);
    frame_queue.setSyncHandle(dropFrame);
}

/**
 * 解码线程.
 * @param args
 * @return
 */
void *decode(void *args) {
    VideoChannel *videoChannel = static_cast<VideoChannel *>(args);
    videoChannel->decodePacket();
    return 0;
}

/**
 * 播放线程.
 * @param args
 * @return
 */
void *synchronize(void *args) {
    VideoChannel *videoChannel = static_cast<VideoChannel *>(args);
    videoChannel->synchronizeFrame();
    return 0;
}

/**
 *开启视频解码packet线程+视频frame解码渲染线程
 */
void VideoChannel::play() {
    LOGE("视频play().....");
    pkt_queue.setWork(1);
    frame_queue.setWork(1);
    isPlaying = true;
    //创建一个线程
    //解码线程packet->frame.
    pthread_create(&pid_viedo_play, NULL, decode, this);
    //解码线程frame->yuv
    pthread_create(&pid_synchronize, NULL, synchronize, this);
}

void VideoChannel::stop() {
    isPlaying = false;
    pthread_join(pid_viedo_play, NULL);
    pthread_join(pid_synchronize, NULL);
    pkt_queue.clear();
    frame_queue.clear();
}


/**
 * 解码出packet队列数据 .
 */
void VideoChannel::decodePacket() {
    AVPacket *packet = 0;
    while (isPlaying) {
        //流 --packet --音频 可以 单一  。
        int ret = pkt_queue.deQueue(packet);
        LOGE("VideoChannel::decoder() #dequeue success# !pkt_queue.size %d", pkt_queue.size());
        if (!isPlaying) {
            break;
        }
        if (!ret) {
            continue;
        }

        if (!avCodecContext) {
            LOGE("avCodecContext is NULL!");
            return;
        }
        ret = avcodec_send_packet(avCodecContext, packet);
        if (ret == AVERROR(EAGAIN)) {
            LOGE("avcodec_send_packet EAGAIN 等待数据包！");
            //需要更多数据
            continue;
        } else if (ret < 0) {
            LOGE("avcodec_send_packet FAilure ret < 0 %d", ret);
            //失败
            break;
        }

        AVFrame *avFrame = av_frame_alloc();
        //获取AVFrame
        ret = avcodec_receive_frame(avCodecContext, avFrame);

        //延缓队列缓存速度，大于100帧等待10ms，否则一直读
        while (isPlaying && frame_queue.size() > 100) {
            av_usleep(1000 * 10);
            LOGE("frame queue is full！frame_queue size: %d", frame_queue.size());
            continue;
        }
        //压缩数据要 解压 yuv->rgb888
        //放入缓存队列
        frame_queue.enQueue(avFrame);
        LOGE("video frame_queue enQueue success ! :%d", frame_queue.size());
    }

    releaseAvPacket(packet);
}

void VideoChannel::synchronizeFrame() {
    //转码上下文：将YUV数据转为RGB。从视频流中读取数据包
    SwsContext *swsContext = sws_getContext(
            avCodecContext->width, avCodecContext->height,
            avCodecContext->pix_fmt,
            avCodecContext->width, avCodecContext->height,
            AV_PIX_FMT_RGBA, SWS_BILINEAR,
            0, 0, 0
    );

    //rgba接收的容器
    uint8_t *dst_data[4];
    //每一行的首地址
    int dst_linesize[4];
    av_image_alloc(dst_data, dst_linesize, avCodecContext->width, avCodecContext->height,
                   AV_PIX_FMT_RGBA, 1);

    //绘制界面
    //转化：一帧YUV数据转为图片---> image(dst_data存储的就是一张图片)  -----》将图片渲染到surface中
    AVFrame *frame = 0;
    while (isPlaying) {
        int ret = frame_queue.deQueue(frame);
        if (!isPlaying) {
            break;
        }
        if (!ret) {
            continue;
        }

        /**
         * 处理图像数据：视频缩放
         * 函数主要是用来做视频像素格式和分辨率的转换，其优势在于：可以在同一个函数里实现：
         * 1.图像色彩空间转换，
         * 2:分辨率缩放，
         * 3:前后图像滤波处理。
         * 不足之处在于：效率相对较低，不如libyuv或shader
         */
        LOGE("synchronizeFrame！get frame success : %d", frame_queue.size());
        sws_scale(swsContext, frame->data, frame->linesize, 0, frame->height, dst_data,
                  dst_linesize);
        //已经获取了rgb数据，则回调给ffmpeg.cpp层使用，进行画面渲染
        renderFrame(dst_data[0], dst_linesize[0], avCodecContext->width, avCodecContext->height);
        //暂时没有来做到音视频同步，所以渲染一帧，等待16ms.
        LOGE("解码一帧视频 视频总缓存size:  %d", frame_queue.size());

        clock = frame->pts * av_q2d(time_base);
        LOGW("video clock pts is %d", frame->pts);
        LOGE("video clock is %f", clock);

        //解码一帧视频延时时间
        double frame_delay = 1.0 / fps;
        //解码时间：解码一帧花费的时间. 配置差的手机 解码耗时教旧，所以需要考虑解码时间.
        double extra_delay = frame->repeat_pict / (2 * fps);
        double delay = frame_delay + extra_delay;

        // 音频pts
        double audioClock = audioChannel->clock;
        double diff = clock - audioClock;

        LOGE(" audio clock %f", audioClock);
        LOGE(" video clock %f", clock);
        LOGE(" fps %d", fps);
        LOGE(" frame_delay %f", frame_delay);
        LOGE(" extra_delay %f", extra_delay);

        /**
         * 音视频同步方案：
         * 取音频和视频的pts进行对比。
         * 1.若视频pts比音频pts大，说明视频超前，视频播放线程要短暂休眠
         * 2.若视频pts比音频pts小，说明音频超前，视频需要进行丢帧
         */
        LOGE("-----------相差----------  %f ", diff);
        if (clock > audioClock) {
            //视频超前，睡一会.
            LOGE("-----------视频超前，相差----------  %f", diff);
            if (diff > 1) {
                LOGE("-----------睡眠long----------  %f", (delay * 2));
                //差的太久了，那只能慢慢赶 不然卡好久
                av_usleep((delay * 2) * 1000000);
            } else {
                LOGE("-----------睡眠normal----------  %d", (delay + diff));
                av_usleep((delay + diff) * 1000000);
            }
        } else {
            //音频超前，需要丢帧进行处理
            LOGE("-----------音频超前，相差----------  %f", diff);
            if (abs(diff) > 1) {
                //不休眠
            } else if (abs(diff) > 0.05) {
                //视频需要追赶.丢帧(非关键帧) 同步
                releaseAvFrame(frame);
                frame_queue.sync();
            } else {
                av_usleep((delay + diff) * 1000000);
            }
        }
        //释放不需要的frame,frame已经没有利用价值了.
        releaseAvFrame(frame);
    }

    //释放资源
    if (dst_data[0]) {
        av_free(dst_data[0]);
    }
    isPlaying = false;
    if (frame) {
        releaseAvFrame(frame);
    }
    sws_freeContext(swsContext);
}

void VideoChannel::seek(long ms) {
    LOGE("VideoChannel::seek has not implemeted!");
}

void VideoChannel::setRenderFrame(RenderFrame renderFrame) {
    this->renderFrame = renderFrame;
}

void VideoChannel::setFps(int fps) {
    this->fps = fps;
}


