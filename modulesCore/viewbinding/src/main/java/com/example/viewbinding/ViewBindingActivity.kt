package com.example.viewbinding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import com.example.viewbinding.databinding.ActivityViewBindingBinding

class ViewBindingActivity : AppCompatActivity() {

    // 此处的viewModel和LeftFragment/RightFragment的viewModel实例一致，这样才能做到activity和fragment之间保持通信
    private val viewModel: BlankViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivityViewBindingBinding.inflate(LayoutInflater.from(this), null, false)
        setContentView(viewBinding.root)
        viewBinding.btnAddOne.text = "viewbinding +1"
    }

    fun addOneClick(view: View) {
        // 使用livedata发送数据，在fragment中通过livedata接收数据
        viewModel.addOne()
    }
}