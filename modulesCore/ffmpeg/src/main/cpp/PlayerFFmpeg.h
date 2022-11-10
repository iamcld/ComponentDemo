//
// Created by A11222 on 2022/11/8.
//

#ifndef COMPONENTDEMO_PLAYERFFMPEG_H
#define COMPONENTDEMO_PLAYERFFMPEG_H

#include <pthread.h>
#include <android/log.h>
#include "android/native_window_jni.h"
#include "JavaCallHelper.h"
#include "AudioChannel.h"
#include "VideoChannel.h"

extern "C" {
//封装格式，总上下文
#include "libavformat/avformat.h"
//解码器.
#include "libavcodec/avcodec.h"
//#缩放
#include "libswscale/swscale.h"
// 重采样
#include "libswresample/swresample.h"
//时间工具
#include "libavutil/time.h"
};


// 错误代码
//视频流打开失败
#define FFMPEG_CAN_NOT_OPEN_URL     -1
//找不到流媒体
#define FFMPEG_CAN_NOT_FIND_STREAMS  -2
//找不到解码器
#define FFMPEG_FIND_DECODER_FAIL -3
//无法根据解码器创建上下文
#define FFMPEG_ALLOC_CODEC_CONTEXT_FAIL -4
//根据流信息 配置上下文失败
#define FFMPEG_CODEC_PARAMETERS_FAIL -5
//打开解码器失败
#define FFMPEG_OPEN_DECODER_FAIL -6
//没有音视频
#define FFMPEG_NO_MEDIA -7


class PlayerFFmpeg {
public:
    PlayerFFmpeg(JavaCallHelper *_javaCallHelper, const char *dataSource);

    ~PlayerFFmpeg();

    void prepare();

    void prepareFFmpeg();

    void start();

    void play();

    void pause();

    void close();

    void setRenderCallBack(RenderFrame renderFrame);

    void seek(long ms);

private:
    bool isPlaying;
    pthread_t pid_prepare;// 准备完成后销毁
    pthread_t pid_play;// 解码线程，一直存在知道播放完成
    char *url;
    AVFormatContext *avFormatContext;
    JavaCallHelper *javaCallHelper;
    VideoChannel *videoChannel;
    AudioChannel *audioChannel;
    RenderFrame renderFrame;

};

#endif //COMPONENTDEMO_PLAYERFFMPEG_H
