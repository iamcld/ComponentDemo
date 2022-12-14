package com.example.ffmpeg

class NativeLib {

    /**
     * A native method that is implemented by the 'ffmpeg' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'ffmpegLibDemo' library on application startup.
        init {
            System.loadLibrary("ffmpegLibDemo")
        }
    }
}