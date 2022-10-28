package com.example.livedata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.libbase.LineLog
import kotlinx.android.synthetic.main.activity_live_data.*

class LiveDataActivity : AppCompatActivity() {

    private val TAG = "LiveDataActivity--->"

    // 定义一个livedata
    private val testLiveData = MutableLiveData<String>()
    private val testHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_data)
        LineLog.d(TAG, "onCreate")
        // 1.常规写法
        // 观察者，接收数据
        // activity处于活跃时，才会接收livedata的数据
        // 也就是说当acvity处于onpause/onstop状态时，不会接收到数据
//        testLiveData.observe(this, object : Observer<String> {
//            override fun onChanged(t: String?) {
//                LineLog.d(TAG, "接收到数据: $t")
//                tv_test_content.text = t
//            }
//        })
//
//        // 2.lambda表达式一
//        testLiveData.observe(this, {
//            LineLog.d(TAG, "接收到数据: $it")
//            tv_test_content.text = it
//        })

        // 3.lambda表达式二，推荐写法
        testLiveData.observe(this) {
            LineLog.d(TAG, "接收到数据: $it")
            tv_test_content.text = it
        }

    }

    override fun onStart() {
        super.onStart()
        LineLog.d(TAG,"onStart")
    }

    override fun onResume() {
        super.onResume()
        LineLog.d(TAG,"onResume")
    }


    // 被观察则，发送数据
    fun sendMsg(view: View) {
        LineLog.d(TAG, "发送数据")
        testLiveData.value = "这是livedata传递过来的数据"
    }

    //此处加延时是模拟acvity处于onpause/onstop状态下的场景
    // 点击按钮后，按home键退到后台，3000ms后发送数据。在activity活跃时(onstart)才接收数据
    fun delaySendMsg(view: View) {
        testHandler.postDelayed({
            // 主线程发送数据,即更新数据
            LineLog.d(TAG, "延迟发送数据")
            testLiveData.value = "延迟发送数据"
        }, 2000)
    }

    fun postValueSendMsg(view: View) {
        Thread{
            // 子线程更新数据会奔溃
//            testLiveData.value = "子线程更新数据"
            testLiveData.postValue("主线程更新数据")
        }.start()
    }
}