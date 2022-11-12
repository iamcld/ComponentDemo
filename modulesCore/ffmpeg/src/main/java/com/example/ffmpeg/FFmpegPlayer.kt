package com.example.ffmpeg
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 * @Author: chenlida
 * @Date: 2022/11/8 13:41
 * @Description:
 */
class FFmpegPlayer : SurfaceHolder.Callback {

    private var surfaceHolder: SurfaceHolder? = null;
    private var playStatusListener: PlayStatusListener? = null
    private var mSource: String = ""

    companion object {
        init {
            System.loadLibrary("ffmpegLibDemo")
        }
    }


    fun setPlayStatusListener(playStatusListener: PlayStatusListener) {
        this.playStatusListener = playStatusListener
    }

    // NDK 播放器需要路径和SurfaceView
    fun setSurfaceView(surfaceview: SurfaceView) {
        this.surfaceHolder?.removeCallback(this)
        this.surfaceHolder = surfaceview.holder
        this.surfaceHolder?.addCallback(this)
    }

    external fun nativePrepare(string: String)
    external fun nativeSetSurface(surface: Surface)
    external fun nativeStart()
    external fun nativeSeek(ms: Long)
    external fun nativePause()
    external fun nativeClose()

    /***回调方法***/
    //c层准备完毕.
    fun onNativePrepare() {
        playStatusListener?.onPrepare()
    }
    //回调播放进度.
    fun onNativeProgress(progress: Int) {
        playStatusListener?.onProgress(progress)
    }

    //播放出错.
    fun onNativeError(errorCode: Int) {
        playStatusListener?.onError(errorCode)
    }


    override fun surfaceCreated(holder: SurfaceHolder) {
        surfaceHolder?.surface?.let {
            nativeSetSurface(it)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        surfaceHolder = holder;
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        nativePause()
    }

    interface PlayStatusListener {
        fun onPrepare()
        fun onProgress(progress: Int)
        fun onError(errorCode: Int)

    }

    /**
     * 设置播放路径
     * @param input
     */
    fun setDataSource(input: String) {
        mSource = input
    }

    //准备播放.
    fun prepare() {
        nativePrepare(mSource)
    }

    /**
     * mp4文件seek .
     * @param milliseconds
     */
    fun seek(milliseconds: Long) {
        nativeSeek(milliseconds)
    }

    /**
     * 开始播放之前调用[.prepare]
     */
    fun start() {
        nativeStart()
    }

    fun close() {
        nativeClose()
    }
}