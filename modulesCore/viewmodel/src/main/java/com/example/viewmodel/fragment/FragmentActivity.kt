package com.example.viewmodel.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.viewmodel.MyViewModel
import com.example.viewmodel.R
import kotlinx.android.synthetic.main.activity_fragment.*

class FragmentActivity : AppCompatActivity() {

    // fragment之间传递数据时，不能使用此方式创建viewmodel。否则无法传递数据
    // 使用此方式，每个fragment创建时的viewmodel都是一个新的实例，这样就无法做到fragment之间传递数据
//    private val myViewModel by lazy {
//        ViewModelProvider(this).get(MyViewModel::class.java)
//    }

    // 此处的viewModel和LeftFragment/RightFragment的viewModel实例一致，这样才能做到activity和fragment之间保持通信
    private val viewModel: BlankViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
    }

    fun addOneClick(view: View) {
        // 使用livedata发送数据，在fragment中通过livedata接收数据
        viewModel.addOne()
    }
}