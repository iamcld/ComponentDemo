package com.example.lifecycle

import android.app.Application
import com.example.libbase.LineLog

/**
 * @Author: chenlida
 * @Date: 2022/10/25 15:45
 * @Description:
 */
class LifeCycleApplication : Application() {
    private val TAG = "LifeCycleApplication--->"

    override fun onCreate() {
        super.onCreate()
        LineLog.d(TAG, "onCreate")
    }
}