//
// Created by A11222 on 2022/11/9.
//
//#include <string.h>
#include "PlayerFFmpeg.h"
#include "logutil.h"

PlayerFFmpeg::PlayerFFmpeg(JavaCallHelper *_javaCallHelper, const char *dataSource) {
    LOGD("构造函数 PlayerFFmpeg---->");
    url = new char[strlen(dataSource) + 1];
    this->javaCallHelper = _javaCallHelper;
    stpcpy(url, dataSource);
}

PlayerFFmpeg::~PlayerFFmpeg() {
    LOGD("析构函数 PlayerFFmpeg---->");
}

void *prepareFFmpeg_(void *args) {
    LOGD("prepareFFmpeg_()---> start pthread");
    //this强制转换成PoeFFmeg对象
    PlayerFFmpeg *playerFFmpeg = static_cast<PlayerFFmpeg *>(args);
    playerFFmpeg->prepareFFmpeg();
    // 线程函数体需要返回0，不然线程退不出
    return 0;
}

/**
 * 准备操作，初始化比较耗时，故放在线程中执行
 */
void PlayerFFmpeg::prepare() {
    LOGD("prepare()---> create pthread");
    pthread_create(&pid_prepare, NULL, prepareFFmpeg_, this);
}

/***
 * 子线程中的准备.
 * 开启子线程准备.
 * 实例化VideoChannel
 */
void PlayerFFmpeg::prepareFFmpeg() {
    //avformat既可以解码本地文件，也可以解码直播文件
    int ret = avformat_network_init();
    if (ret < 0) {
        LOGD("Couldn’t init network %d", ret);
    }
    // 总上下文，用来解压视频为 视频流+音频流
    avFormatContext = avformat_alloc_context();

    //参数配置
    AVDictionary *opts = NULL;
    //设置超时时间3s（3000ms->3000000mms)
    //以udp方式打开，如果以tcp方式打开将udp替换为tcp
    av_dict_set(&opts, "rtsp_transport", "tcp", 0);
//    av_dict_set(&opts , "timeout" , "3000000",0);
//    av_dict_set(&opts, "buffer_size", "1024000", 0);
//    av_dict_set(&opts, "max_delay", "500000", 0);
//    av_dict_set(&opts, "stimeout", "20000000", 0);  //设置超时断开连接时间
    char buf[1024];
    //开始打开视频文件 .
    ret = avformat_open_input(&avFormatContext, url, NULL, &opts);
    av_strerror(ret, buf, 1024);

    if (ret < 0) {
        LOGD("* * * * * * video open failure! * * * * * * * * *n %d", ret);
        LOGE("Couldn’t open file %s: %d(%s)", url, ret, buf);
        //播放失败，通知java层播放失败了.
        if (javaCallHelper) {
            javaCallHelper->onError(THREAD_CHILD, FFMPEG_CAN_NOT_OPEN_URL);
        }
        return;
    }

//解析视频流，放到avFormatContext中
    ret = avformat_find_stream_info(avFormatContext, NULL);
    if (ret < 0) {
        LOGD("* * * * * * video find stream failure! * * * * * * * * * %d", ret);
        //播放失败，通知java层播放失败了.
        if (javaCallHelper) {
            javaCallHelper->onError(THREAD_CHILD, FFMPEG_CAN_NOT_FIND_STREAMS);
        }
    }

    // 遍历文件流类型：音频流和视频流
    for (int i = 0; i < avFormatContext->nb_streams; ++i) {
        AVStream *stream = avFormatContext->streams[i];
        //1.找解码参数
        AVCodecParameters *codecParameters = stream->codecpar;
        //2.找解码器
        AVCodec *codec = avcodec_find_decoder(codecParameters->codec_id);
        if (!codec) {
            if (javaCallHelper) {
                javaCallHelper->onError(THREAD_CHILD, FFMPEG_FIND_DECODER_FAIL);
            }
            return;
        }
        //3 创建解码器上下文
        AVCodecContext *codecContext = avcodec_alloc_context3(codec);
        if (!codecContext) {
            if (javaCallHelper) {
                javaCallHelper->onError(THREAD_CHILD, FFMPEG_ALLOC_CODEC_CONTEXT_FAIL);
            }
            return;
        }
        //4.解码器参数附加到解码器上下文
        ret = avcodec_parameters_to_context(codecContext, codecParameters);
        if (ret < 0) {
            if (javaCallHelper) {
                javaCallHelper->onError(THREAD_CHILD, FFMPEG_CODEC_PARAMETERS_FAIL);
            }
            return;
        }

        //5.打开解码器
        if (avcodec_open2(codecContext, codec, NULL) < 0) {
            if (javaCallHelper) {
                javaCallHelper->onError(THREAD_CHILD, FFMPEG_OPEN_DECODER_FAIL);
            }
            return;
        }

        if (AVMEDIA_TYPE_AUDIO == codecParameters->codec_type) {
            // 音频流
            audioChannel = new AudioChannel(i, javaCallHelper, codecContext, stream->time_base,
                                            avFormatContext);
            // 音频流解码帧时间戳 timebase = 分子/分母
            LOGD("* * * * * * Audio stream timebase is! * * * * * * * * * %d/%d",
                 stream->time_base.num, stream->time_base.den);
        } else if (AVMEDIA_TYPE_VIDEO == codecParameters->codec_type){
            // 视频流解码帧时间戳 timebase = 分子/分母
            LOGE("* * * * * * Video stream timebase is! * * * * * * * * * %d/%d",
                 stream->time_base.num, stream->time_base.den);
            //视频帧率
            AVRational avFrameRate = stream->avg_frame_rate;
            int fps = av_q2d(avFrameRate);

            // 视频流
            videoChannel = new VideoChannel(i, javaCallHelper, codecContext, stream->time_base,
                                            avFormatContext);
            videoChannel->setRenderFrame(renderFrame);
            videoChannel->setFps(fps);
        }

    }

    // 音视频都没有则抛出错误。没有满足规则的流
    if (!audioChannel && !videoChannel) {
        if (javaCallHelper) {
            javaCallHelper->onError(THREAD_CHILD, FFMPEG_NO_MEDIA);
        }
        return;
    }

    //  获取音视对象，音视频同步用
    videoChannel->audioChannel = audioChannel;
    // 回调，准备完成工作
    if (javaCallHelper) {
        javaCallHelper->onPrepare(THREAD_CHILD);
    }
}

void PlayerFFmpeg::setRenderCallBack(RenderFrame renderFrame) {
    this->renderFrame = renderFrame;
}

void *startThread(void *args) {
    PlayerFFmpeg *playerFFmpeg = static_cast<PlayerFFmpeg *>(args);
    playerFFmpeg->play();
    return 0;
}

/**
 * 打开播放标志，开始解码
 */
void PlayerFFmpeg::start() {
    //播放成功
    isPlaying = true;
    //开启解码
    //音频解码
    LOGE("* * * * * * audioChannel * * * * * * * * * ");
    if (audioChannel) {
        LOGE("* * * * * * audioChannel->play() * * * * * * * * * ");
        /**
        * 日志崩溃在audioChannel->play()
        * 但奇怪的是，已经判断指针audioChannel不为空才走进来，为啥还会出错
        * 为啥呢？因为
        * audioChannel定义了，但是没初始化，所以和JAVA不一样的是，对C++来说，只要生成了PoeFFmpeg对象，则内部的audioChannel指针就不是为空！
        * 所以声明指针变量指需要复初始值NULL
        */
        audioChannel->play();
    }
    LOGE("* * * * * * videoChannel * * * * * * * * * ");
    // 视频解码.
    if (videoChannel) {
        LOGE("* * * * * * videoChannel->play() * * * * * * * * * ");
        //开启视频解码线程，读取packet->frame->synchronized->window_buffer
        videoChannel->play();
    }

    LOGE("* * * * * * pthread_create * * * * * * * * * ");
    //视频播放的时候开启一个解码线程.解码packet.
    pthread_create(&pid_play, NULL, startThread, this);
}

/**
 *  在子线程中执行播放解码
 */
void PlayerFFmpeg::play() {
    int ret = 0;
    while (isPlaying) {
        // 如果队列数据大于100，则延缓解码速度
        if (audioChannel && audioChannel->pkt_queue.size() > 100) {
            // 生产者的速度远远大于消费，需要延迟10ms
            av_usleep(1000 * 10);
            continue;
        }

        // 如果队列数据大于100，则延缓解码速度
        if (videoChannel && videoChannel->pkt_queue.size() > 100) {
            // 生产者的速度远远大于消费，需要延迟10ms
            av_usleep(1000 * 10);
            continue;
        }

        //读取包
        AVPacket *packet = av_packet_alloc();
        //从媒体中读取音视频的packet包
        ret = av_read_frame(avFormatContext, packet);
        if (ret == 0) {
            // 将同一id的音频或者视频流数据包加入队列
            if (audioChannel && packet->stream_index == audioChannel->channelId) {
                LOGE("audioChannel->pkt_queue.enQueue(packet):%d", audioChannel->pkt_queue.size());
                audioChannel->pkt_queue.enQueue(packet);
            } else if (videoChannel && packet->stream_index == videoChannel->channelId) {
                LOGE("videoChannel->pkt_queue.enQueue(packet):%d", videoChannel->pkt_queue.size());
                videoChannel->pkt_queue.enQueue(packet);
            }
        } else if (ret == AVERROR_EOF) {
            //读取完毕，但是不一定播放完毕
            if (videoChannel->pkt_queue.empty() && videoChannel->frame_queue.empty()
                && audioChannel->pkt_queue.empty() && audioChannel->frame_queue.empty())
                LOGD("播放完毕");
            break;
        } else {
            //因为存在seek的原因，就算读取完毕，依然要循环 去执行av_read_frame(否则seek了没用...)
            break;
        }
    }

    isPlaying = false;
    if (audioChannel) {
        audioChannel->stop();
    }

    if (videoChannel) {
        videoChannel->stop();
    }
}

void PlayerFFmpeg::pause() {
    //先关闭播放状态.
//    isPlaying = false;
}

void PlayerFFmpeg::close() {
    //先关闭播放状态.
    isPlaying = false;
    //停止prepare线程.
    pthread_join(pid_prepare, NULL);
    //停止play线程
    pthread_join(pid_play, NULL);

//    if (audioChannel) {
//        audioChannel->stop();
//    }
//    if (videoChannel) {
//        videoChannel->stop();
//    }
}

//seek the frame to dest .
void PlayerFFmpeg::seek(long ms) {
    //优先seek audio,如果没有audio则seek视频.
    if (audioChannel) {
        audioChannel->seek(ms);
    } else if (videoChannel) {
        videoChannel->seek(ms);
    }
}