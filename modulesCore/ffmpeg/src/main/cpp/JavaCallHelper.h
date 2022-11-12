//
// Created by A11222 on 2022/11/8.
//

#ifndef COMPONENTDEMO_JAVACALLHELPER_H
#define COMPONENTDEMO_JAVACALLHELPER_H

#include <jni.h>

#define THREAD_MAIN 1
#define THREAD_CHILD 2

class JavaCallHelper {
public:
    JavaCallHelper(JavaVM *_javaVM, JNIEnv *_env, jobject &_obj);

    ~JavaCallHelper();

    void onPrepare(int thread);

    void onProgress(int thread, int progress);

    void onError(int thread, int code);

private:
    JavaVM *javaVm = NULL;
    JNIEnv *env = NULL;
    jobject jobj;
    jmethodID jmethodId_prepare;
    jmethodID jmethodId_error;
    jmethodID jmethodId_progress;
};

#endif //COMPONENTDEMO_JAVACALLHELPER_H


