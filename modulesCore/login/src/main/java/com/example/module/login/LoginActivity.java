package com.example.module.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.libbase.LineLog;

@Route(path = "/login/LoginActivity")
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity--->";
    private Button bg_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LineLog.INSTANCE.d(TAG, "onCreate()");
        bg_login = findViewById(R.id.tv_login);
        bg_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/main/MainActivity").navigation();
            }
        });
    }
}