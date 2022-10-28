package com.example.viewmodel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_view_model.*

class ViewModelActivity : AppCompatActivity() {

    private val myViewModel by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_model)
        myViewModel.getTestNum().observe(this) {
            tv_test.text = it.toString();
        }
    }

    // 通过viewModel和liveData结合，更新ui数据。屏幕旋转时，界面数据不变
    fun addOneClick(view: View) {
        myViewModel.addOne()
    }
}


