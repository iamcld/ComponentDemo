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

};

#endif //COMPONENTDEMO_PLAYERFFMPEG_H
