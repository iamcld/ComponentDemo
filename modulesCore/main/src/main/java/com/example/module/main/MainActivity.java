package com.example.module.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.libbase.LineLog;

@Route(path = "/main/MainActivity")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity--->";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LineLog.INSTANCE.d(TAG, "onCreate()");
        setContentView(R.layout.activity_main);
    }
}