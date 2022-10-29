package com.example.componentdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.alibaba.android.arouter.launcher.ARouter;
//import com.example.ffmpeg.NativeLib;
import com.example.libbase.LineLog;

/**
 * 一般作为启动页activity
 */
public class StartActivity extends AppCompatActivity {

    private static final String TAG = "StartActivity--->";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LineLog.INSTANCE.d(TAG, "onCreate()");
//        new NativeLib().stringFromJNI();

        // debug模式时，不会把login和main组件添加到app模块下，故此次不会跳转。
        // 正式模式时，才会跳转
        ARouter.getInstance().build("/login/LoginActivity").navigation();
    }
}