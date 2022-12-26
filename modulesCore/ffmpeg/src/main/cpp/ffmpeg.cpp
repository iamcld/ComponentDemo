#include <jni.h>
#include <string>
#include "logutil.h"
#include "JavaCallHelper.h"
#include "PlayerFFmpeg.h"
#include "safe_queue.h"
#include "Test.h"

extern "C"
{
#include "libavformat/avformat.h"
#include "libavformat/avformat.h"

}

JavaCallHelper *javaCallHelper;

//stdio.h中定义 #define NULL 0.即空指针指向地址0
//在C语言使用NULL表示空指针，java使用null表示空指针，Object-C使用nil表示空指针。当然，今天的主角是C++，它使用nullptr表示空指针。
//子线程想要回调java层就必须要先绑定到jvm.
JavaVM *javaVm = NULL;
PlayerFFmpeg *playerFFmpeg = NULL;
//绘图窗口.等价于 *window = NULL
ANativeWindow *window = 0;

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_ffmpeg_NativeLib_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    LOGD("%s", hello.c_str());
    Test::test();
    return env->NewStringUTF(hello.c_str());
}

//这个类似android的生命周期，当调用System.loadLibrary时，会回调这个方法
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    javaVm = vm;
    return JNI_VERSION_1_4;
}

/**
 *渲染窗口回调接口
 * 只需要不断的往Nativie Windows缓冲区进行拷贝数据，就能完成绘制
 * @param data
 * @param linesize
 * @param w
 * @param h
 */
void renderFrame(uint8_t *data, int linesize, int w, int h) {
    LOGD("renderFrame start--------->");
    //对本窗口设置缓冲区大小RGBA
    ANativeWindow_setBuffersGeometry(window, w, h, WINDOW_FORMAT_RGBA_8888);
    ANativeWindow_Buffer windowBuffer;
    if (ANativeWindow_lock(window, &windowBuffer, 0)) {
        ANativeWindow_release(window);
        window = 0;
        return;
    }

    // 拿到window的缓冲区，window_data[0]=225就代表刷新了红色
    uint8_t *window_data = static_cast<uint8_t *>(windowBuffer.bits);
    //window_data = data,r g b a每个元素占用4bit
    int window_linesize = windowBuffer.stride * 4;
    uint8_t *str_data = data;
    // 按行拷贝rgba数据到window_buffer里面
    for (int i = 0; i < windowBuffer.height; ++i) {
        //由于window的stride和帧的stride不同,因此需要逐行复制
        //以目的地为准，逐行拷贝。这样就可以进行绘制
        memcpy(window_data + i * window_linesize, str_data + i * window_linesize, window_linesize);
    }
    ANativeWindow_unlockAndPost(window);
    LOGD("func__renderFrame finish---------->%d", __LINE__);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ffmpeg_FFmpegPlayer_nativePrepare(JNIEnv *env, jobject thiz, jstring url) {
    // 转化播放源地址
    const char *input = env->GetStringUTFChars(url, 0);
    //实现一个控制类
    javaCallHelper = new JavaCallHelper(javaVm, env, thiz);
    playerFFmpeg = new PlayerFFmpeg(javaCallHelper, input);
    //设置回调监听
    playerFFmpeg->setRenderCallBack(renderFrame);
    // 进行准备
    playerFFmpeg->prepare();
    // 释放资源
    env->ReleaseStringUTFChars(url, input);

}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_ffmpeg_FFmpegPlayer_nativeSetSurface(JNIEnv *env, jobject thiz, jobject surface) {
    LOGD("set native surface invocked");
    //释放之前的window实例
    if (window) {
        ANativeWindow_release(window);
        window = 0;
    }
    //创建AWindow
    window = ANativeWindow_fromSurface(env, surface);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ffmpeg_FFmpegPlayer_nativeStart(JNIEnv *env, jobject thiz) {
    //正式进入播放界面
    if (playerFFmpeg) {
        playerFFmpeg->start();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ffmpeg_FFmpegPlayer_nativeSeek(JNIEnv *env, jobject thiz, jlong ms) {

    if (playerFFmpeg) {
        playerFFmpeg->seek(ms);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ffmpeg_FFmpegPlayer_nativePause(JNIEnv *env, jobject thiz) {

    if (playerFFmpeg) {
        playerFFmpeg->pause();
    }
}

/**
 * 关闭解码线程，释放资源
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_example_ffmpeg_FFmpegPlayer_nativeClose(JNIEnv *env, jobject thiz) {
    if (playerFFmpeg) {
        playerFFmpeg->close();
    }
}



