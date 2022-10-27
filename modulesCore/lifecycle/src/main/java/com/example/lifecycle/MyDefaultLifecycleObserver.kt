package com.example.lifecycle

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.libbase.LineLog

/**
 * @Author: chenlida
 * @Date: 2022/10/25 17:56
 * @Description: 可在此处理具体业务
 */
class MyDefaultLifecycleObserver : DefaultLifecycleObserver {

    private  val TAG = "MyDefaultLifecycleObserver--->"

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        LineLog.d(TAG, "onCreate: ")
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        LineLog.d(TAG, "onStart: ")
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        LineLog.d(TAG, "onResume: ")
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        LineLog.d(TAG, "onPause: ")
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        LineLog.d(TAG, "onStop: ")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        LineLog.d(TAG, "onDestroy: ")
    }
}