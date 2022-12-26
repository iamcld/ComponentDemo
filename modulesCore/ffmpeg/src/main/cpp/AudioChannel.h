//
// Created by A11222 on 2022/11/8.
//

#ifndef COMPONENTDEMO_AUDIOCHANNEL_H
#define COMPONENTDEMO_AUDIOCHANNEL_H

#include "BaseChannel.h"
#include <android/native_window.h>
#include <pthread.h>
#include "JavaCallHelper.h"
#include <SLES/OpenSLES_Android.h>

class AudioChannel : public BaseChannel {
public:
    AudioChannel(int id, JavaCallHelper *javaCallHelper, AVCodecContext *codecContext,
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

    void initOpenSL();

    void decoder();

    int getPcm();

private:
    pthread_t pid_audio_play;
    pthread_t pid_audio_decode;
    SwrContext *swrContext = NULL;
    int out_channels;//通道数
    int out_sample_size;//采样率
    int out_sample_rate;//采样频率

public:
    uint8_t *buffer = NULL;
    /**
     * 	DTS：解码时间戳，告诉解码器packet的解码顺序。即I、P、B帧的解码顺序。
	PTS:显示时间戳，指示从packet中解码出来的数据显示顺序。如果播放的视频乱跳，说明这个pts时间戳错误了。
	音频中二者是相同的，但是视频由于B帧（双向预测，B帧解码出来会被缓存中传输缓冲区进行双向预测，并不会直接用于显示）的存在，
	会造成解码顺序与显示顺序并不相同，也就是视频中DTS和PTS不一定相同。I和P帧解码出来，可以直接用于显示。
	音视频同步：使用音频的pts和视频的pts进行比较。如果音频的pts比视频的pts
     */
    double clock;//音视频同步使用，音频的pts
};

#endif //COMPONENTDEMO_AUDIOCHANNEL_H
