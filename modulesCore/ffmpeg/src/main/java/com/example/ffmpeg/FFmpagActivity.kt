package com.example.ffmpeg

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ffmpeg.R.*
import com.example.libbase.LineLog

class FFmpagActivity : AppCompatActivity() {
    private val TAG = "FfmpagActivity--->"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_ffmpag)
        var tmp = NativeLib().stringFromJNI()
        LineLog.d(TAG, "get c++ string : $tmp")
    }

}