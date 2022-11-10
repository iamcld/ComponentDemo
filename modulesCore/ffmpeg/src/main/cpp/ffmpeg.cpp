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

//子线程想要回调java层就必须要先绑定到jvm.
JavaVM *javaVm = NULL;
PlayerFFmpeg* playerFFmpeg = NULL;

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_ffmpeg_NativeLib_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    LOGD("%s", hello.c_str());
    Test::test();
    return env->NewStringUTF(hello.c_str());
}

//这个类似android的生命周期，加载jni的时候会自己调用
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    javaVm = vm;
    return JNI_VERSION_1_4;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ffmpeg_FFmpegPlayer_nativePrepare(JNIEnv *env, jobject thiz, jstring url) {
    // 转化播放源地址
    const char *input = env->GetStringUTFChars(url, 0);
    //实现一个控制类
    javaCallHelper = new JavaCallHelper(javaVm, env, thiz);
    playerFFmpeg = new PlayerFFmpeg(javaCallHelper, input);

}