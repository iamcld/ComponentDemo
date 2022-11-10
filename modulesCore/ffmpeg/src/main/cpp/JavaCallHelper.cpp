//
// Created by A11222 on 2022/11/9.
//
#include "JavaCallHelper.h"
#include "logutil.h"

JavaCallHelper::JavaCallHelper(JavaVM *_javaVM, JNIEnv *_env, jobject &_obj) : javaVm(_javaVM),
                                                                               env(_env) {
    LOGD("构造函数 JavaCallHelper---->");
    //建立全局的引用对象jobj，防止方法执行结束，内存被回收.
    jobj = env->NewGlobalRef(_obj);
    //获取java层对象
    jclass jclassz = env->GetObjectClass(jobj);

    //获取java层jclassz对象的方法
    jmethodId_prepare = env->GetMethodID(jclassz, "onNativePrepare", "()V");
    jmethodId_progress = env->GetMethodID(jclassz, "onNativeProgress", "(I)V");
    jmethodId_error = env->GetMethodID(jclassz, "onNativePrepare", "(I)V");
}

JavaCallHelper::~JavaCallHelper() {
    LOGD("析构函数 JavaCallHelper---->");
}

void JavaCallHelper::onPrepare(int thread) {
    LOGD("onPrepare---->");
    if (thread == THREAD_CHILD) {
        JNIEnv *jniEnv;
        // jni中使用pthread或者std::thread创建线程时，因为线程并不是从Java环境创建的。所以这时候创建出的线程是没有JNIEnv的。
        // 如果需要使用JNIEnv,可以调用JavaVM的AttachCurrentThread将当前线程附加到虚拟机
        if (javaVm->AttachCurrentThread(&jniEnv, 0) != JNI_OK) {
            return;
        }
        // 在子线程中回调java层方法
        jniEnv->CallVoidMethod(jobj, jmethodId_prepare);
        //在线程退出之前我们必须要调用DetachCurrentThread从虚拟机分离当前线程,，不然会造成内存泄露，线程也不会退出
        javaVm->DetachCurrentThread();
    } else {
        // 在主线程中回调java层方法
        env->CallVoidMethod(jobj, jmethodId_prepare);
    }
}

void JavaCallHelper::onProgress(int thread, int progress) {
    LOGD("onProgress---->");
    if (thread == THREAD_CHILD) {
        JNIEnv *jniEnv;
        // jni中使用pthread或者std::thread创建线程时，因为线程并不是从Java环境创建的。所以这时候创建出的线程是没有JNIEnv的。
        // 如果需要使用JNIEnv,可以调用JavaVM的AttachCurrentThread将当前线程附加到虚拟机
        if (javaVm->AttachCurrentThread(&jniEnv, 0) != JNI_OK) {
            return;
        }
        // 在子线程中回调java层方法
        jniEnv->CallVoidMethod(jobj, jmethodId_progress);
        //在线程退出之前我们必须要调用DetachCurrentThread从虚拟机分离当前线程,，不然会造成内存泄露，线程也不会退出
        javaVm->DetachCurrentThread();
    } else {
        // 在主线程中回调java层方法
        env->CallVoidMethod(jobj, jmethodId_progress);
    }

}

void JavaCallHelper::onError(int thread, int code) {
    LOGD("onError---->");
    if (thread == THREAD_CHILD) {
        JNIEnv *jniEnv;
        // jni中使用pthread或者std::thread创建线程时，因为线程并不是从Java环境创建的。所以这时候创建出的线程是没有JNIEnv的。
        // 如果需要使用JNIEnv,可以调用JavaVM的AttachCurrentThread将当前线程附加到虚拟机
        if (javaVm->AttachCurrentThread(&jniEnv, 0) != JNI_OK) {
            return;
        }
        // 在子线程中回调java层方法
        jniEnv->CallVoidMethod(jobj, jmethodId_error);
        //在线程退出之前我们必须要调用DetachCurrentThread从虚拟机分离当前线程,，不然会造成内存泄露，线程也不会退出
        javaVm->DetachCurrentThread();
    } else {
        // 在主线程中回调java层方法
        env->CallVoidMethod(jobj, jmethodId_error);
    }

}