package com.example.lifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.libbase.LineLog

class LifeCycleActivity : AppCompatActivity() {
    private val TAG = "LifeCycleActivity--->"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LineLog.d(TAG, "onCreate")
    }
}