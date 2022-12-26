//
// Created by A11222 on 2022/11/8.
//

#ifndef COMPONENTDEMO_VIDEOCHANNEL_H
#define COMPONENTDEMO_VIDEOCHANNEL_H

#include "BaseChannel.h"
#include <android/native_window.h>
#include <pthread.h>
#include "AudioChannel.h"
#include "JavaCallHelper.h"

//定义一个给native层使用的回调接口.
typedef void(*RenderFrame)(uint8_t *, int, int, int);

class VideoChannel : public BaseChannel {
public:
    VideoChannel(int id, JavaCallHelper *javaCallHelper, AVCodecContext *codecContext,
                 AVRational time_base, AVFormatContext *formatContext);

    /**
     * 播放音频或视频.
     */
    virtual void play();

    /**
     * 停止播放音频或视频.
     */
    virtual void stop();

    virtual void seek(long ms);

    /**
     * 解码packet队列-》frame.
     */
    void decodePacket();


    /**
     * YUV --》RGB888 .
     */
    void synchronizeFrame();

    /**
     * 设置渲染回调接口
     * @param renderFrame
     */
    void setRenderFrame(RenderFrame renderFrame);

    void setFps(int fps);

private:
    pthread_t pid_viedo_play;
    pthread_t pid_synchronize;
    RenderFrame renderFrame;
    int fps;

public:
    AudioChannel *audioChannel = NULL;//音视频同步使用
    double clock;//音视频同步使用，视频pts
};

#endif //COMPONENTDEMO_VIDEOCHANNEL_H
