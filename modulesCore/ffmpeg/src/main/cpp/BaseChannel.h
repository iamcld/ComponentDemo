//
// Created by A11222 on 2022/11/8.
//

#ifndef COMPONENTDEMO_BASECHANNEL_H
#define COMPONENTDEMO_BASECHANNEL_H

#include <android/log.h>
#include "safe_queue.h"
#include "JavaCallHelper.h"
#include "logutil.h"

extern "C" {
#include <libavutil/rational.h>
//封装格式，总上下文
#include "libavformat/avformat.h"
//解码器
#include "libavcodec/avcodec.h"
//缩放
#include "libswscale/swscale.h"
// 重采样
#include "libswresample/swresample.h"
//时间工具
#include "libavutil/time.h"
//编码转换工具yuv->rgb888
#include "libavutil/imgutils.h"
};

class BaseChannel {
public:
    BaseChannel(int id, JavaCallHelper *javaCallHelper, AVCodecContext *codecContext,
                AVRational time_base): channelId(id),
              javaCallHelper(javaCallHelper),
              avCodecContext(codecContext),
              time_base(time_base){
        LOGD("构造函数BaseChannel()---->");
    }

    ~BaseChannel() {
        LOGD("析构函数~BaseChannel()---->");

        //销毁解码器上下文
        if (avCodecContext) {
            avcodec_close(avCodecContext);
            avcodec_free_context(&avCodecContext);
            avCodecContext = 0;
        }
        //销毁队列 ,此处有问题safe_queue.clear()，SafeQueue结构体未明确.
        pkt_queue.clear();
        frame_queue.clear();
        LOGE("释放channel:%d %d" , pkt_queue.size(), frame_queue.size());
    }

    static void releaseAvPacket(AVPacket *&packet) {
        if (packet) {
            av_packet_free(&packet);
            packet = 0;
        }
    }

    static void releaseAvFrame(AVFrame *&frame) {
        if (frame) {
            av_frame_free(&frame);
            frame = 0;
        }
    }
    /**
     * 播放音频或视频.
     */
    virtual void play() = 0;
    /**
     * 停止播放音频或视频.
     */
    virtual void stop() = 0;
    /**
     * seek video.
     * @param ms
     */
    virtual void seek(long ms) = 0;

public:
    SafeQueue<AVPacket *> pkt_queue;//未解码帧队列
    SafeQueue<AVFrame *> frame_queue;// 解码帧队列
    volatile int channelId;
    volatile bool isPlaying;
    AVCodecContext *avCodecContext = NULL;
    AVFormatContext *avFormatContext = NULL;
    JavaCallHelper *javaCallHelper = NULL;
    AVRational time_base;
};

#endif //COMPONENTDEMO_BASECHANNEL_H
