package com.example.componentdemo;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.libbase.LineLog;

public class ComponenApplication extends Application {
    private static final String TAG = "ComponenApp--->";

    @Override
    public void onCreate() {
        super.onCreate();
        LineLog.INSTANCE.d(TAG, "onCreate()");
        ARouter.init(this);
    }
}
