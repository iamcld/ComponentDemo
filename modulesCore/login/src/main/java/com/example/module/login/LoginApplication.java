package com.example.module.login;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.libbase.LineLog;

/**
 * @Author: chenlida
 * @Date: 2022/10/25 15:42
 * @Description:
 */
public class LoginApplication extends Application {
    private static final String TAG = "LoginApplication--->";

    @Override
    public void onCreate() {
        super.onCreate();
        LineLog.INSTANCE.d(TAG, "onCreate()");
        ARouter.init(this);
    }
}
