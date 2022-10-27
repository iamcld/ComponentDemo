package com.example.lifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.libbase.LineLog

private val TAG = "LifeCycleActivity--->"

class LifeCycleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LineLog.d(TAG, "onCreate")

        // 2.监听单个activity生命周期
        lifecycle.addObserver(MyDefaultLifecycleObserver())
    }
}