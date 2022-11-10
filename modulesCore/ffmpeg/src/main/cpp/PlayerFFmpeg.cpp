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
        } else {
            // 视频流解码帧时间戳 timebase = 分子/分母
            LOGE("* * * * * * Video stream timebase is! * * * * * * * * * %d/%d",
                 stream->time_base.num, stream->time_base.den);
            //视频帧率
            AVRational avFrameRate = stream->avg_frame_rate;
            int fps = av_q2d(avFrameRate);

            // 视频流
            videoChannel = new VideoChannel(i, javaCallHelper, codecContext, stream->time_base,avFormatContext);
            videoChannel->setRenderFrame(renderFrame);
            videoChannel->setFps(fps);
        }

    }


}
