package com.example.ffmpeg

import FFmpegPlayer
import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import com.example.ffmpeg.R.layout
import com.example.libbase.LineLog
import kotlinx.android.synthetic.main.activity_ffmpag.*

class FFmpegActivity : AppCompatActivity() {
    private val TAG = "FfmpagActivity--->"
    private val requestPermissionCode = 10086
    private val requestPermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE)

    private var fFmpegPlayer: FFmpegPlayer? = null
    private var mUrl: String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_ffmpag)
        var tmp = NativeLib().stringFromJNI()
        LineLog.d(TAG, "get c++ string : $tmp")


        // Example of a call to a native method
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (PermissionChecker.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED
            ) {
                requestPermissions(requestPermission, requestPermissionCode)
            }
        }

        initData()
        initView()
        initListener()
        initFFmpegPlayer()
    }



    private fun initData() {
        mUrl = "rtsp://106.75.254.198:5555/rtsp/c20ddaff-b2e5-4c10-80b7-938891409d35" //O3P
//        mUrl="http://106.75.254.198:5581/rtsp/68448ebb-d34b-4af7-9cf9-ffe650ced784.flv";//O3P
        //        mUrl="http://106.75.254.198:5581/rtsp/68448ebb-d34b-4af7-9cf9-ffe650ced784.flv";//O3P
        mUrl = "http://106.75.254.198:5581/rtsp/4127aeff-4d1e-411b-9d21-23205f117e75.flv" //4x倍速播放.
        mUrl = "/sdcard/DCIM/lpds/video/1662433156411.mp4" //只有视频流，没有音频流时，程序会奔溃
        mUrl = "/sdcard/DCIM/Camera/20220705_122608.mp4" //有视频流和音频流时，程序不会奔溃
    }

    private fun initView() {
        edt_url.setText(mUrl)
    }

    private fun initListener() {
        btn_play.setOnClickListener(){
            playLocal()
        }

        seek_bar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // 结束seek后进行seek操作.
                fFmpegPlayer?.nativeSeek(25*1000)
            }

        })
    }

    private fun initFFmpegPlayer() {
        fFmpegPlayer?.setSurfaceView(surface_view)
    }

    private fun playLocal() {
        fFmpegPlayer?.setDataSource(edt_url?.text.toString())
        fFmpegPlayer?.setPlayStatusListener(object : FFmpegPlayer.PlayStatusListener{
            override fun onPrepare() {
                LineLog.d(TAG, "onPrepare--->")
                fFmpegPlayer?.start()

            }

            override fun onProgress(progress: Int) {
                LineLog.d(TAG, "onProgress---> progress = ${progress}")

            }

            override fun onError(errorCode: Int) {
                LineLog.d(TAG, "onError---> errorCode = ${errorCode}")
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        fFmpegPlayer?.close()
    }

}