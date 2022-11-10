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
    LOGE("构造函数丢弃视频Frame.....");

    this->javaCallHelper = javaCallHelper;
    this->avCodecContext = avCodecContext;
    this->avFormatContext = formatContext;

    pkt_queue.setReleaseCallback(releaseAvPacket);
    pkt_queue.setSyncHandle(dropPacket);
    frame_queue.setReleaseCallback(releaseAvFrame);
    frame_queue.setSyncHandle(dropFrame);
}

void VideoChannel::setRenderFrame(RenderFrame renderFrame) {
    this->renderFrame = renderFrame;
}

void VideoChannel::setFps(int fps) {
    this->fps = fps;
}


