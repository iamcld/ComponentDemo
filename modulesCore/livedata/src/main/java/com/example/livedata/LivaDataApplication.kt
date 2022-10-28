package com.example.livedata

import android.app.Application
import com.example.libbase.LineLog

/**
 * @Author: chenlida
 * @Date: 2022/10/28 1:14
 * @Description:
 */
class LivaDataApplication : Application() {
    private val TAG = "LivaDataApplication--->"

    override fun onCreate() {
        super.onCreate()
        LineLog.d(TAG, "onCreate")
    }
}