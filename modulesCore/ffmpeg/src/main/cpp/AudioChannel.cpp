//
// Created by A11222 on 2022/11/10.
//

#include "AudioChannel.h"

AudioChannel::AudioChannel(int id, JavaCallHelper *javaCallHelper, AVCodecContext *avCodecContext,
                           AVRational time_base, AVFormatContext *formatContext)
        : BaseChannel(id, javaCallHelper, avCodecContext, time_base) {
    this->javaCallHelper = javaCallHelper;
    this->avCodecContext = avCodecContext;
    this->avFormatContext = formatContext;
    //初始化音频相关参数
    out_channels = av_get_channel_layout_nb_channels(AV_CH_LAYOUT_STEREO);
    out_sample_size = av_get_bytes_per_sample(AV_SAMPLE_FMT_S16);
    out_sample_rate = 44100;
    //CD音频标准
    //44100 双声道 2字节
    buffer = (uint8_t *) (malloc(out_sample_rate * out_sample_size * out_channels));

    //设置清空回调函数释放对象的回调
    pkt_queue.setReleaseCallback(releaseAvPacket);
    frame_queue.setReleaseCallback(releaseAvFrame);
}

void AudioChannel::play() {
    LOGE("音频 play().....");

}

void AudioChannel::stop() {
    LOGE("音频 stop().....");

}

void AudioChannel::seek(long ms) {
    LOGE("VideoChannel::seek has not implemeted!");
}