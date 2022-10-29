#include <jni.h>
#include <string>
#include "./include/logutil.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_ffmpeg_NativeLib_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    LOGD("%s", hello.c_str());
    return env->NewStringUTF(hello.c_str());
}