package com.example.livedata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.libbase.LineLog
import kotlinx.android.synthetic.main.activity_live_data_for_map.*

class LiveDataForMapActivity : AppCompatActivity() {
    private val TAG = "LiveDataForMapActivity--->"

    private val testLiveData = MutableLiveData<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_data_for_map)

        // 1.具名函数方式。使用map操作符，将MutableLiveData<Int>类型转为MutableLiveData<String>类型
        var liveMap1 = Transformations.map(testLiveData, ::livedataMapFun)
        liveMap1.observe(this) {
            // 通过Transformations.map方法，将testLiveData发送过来的Int类型数据1000，转为String类型”livedata发送过来的数据是-1000“
            LineLog.d(TAG, "接收到数据: $it")
            tv_livedata_map_content.text = it
        }


        // 2.匿名函数方式。使用map操作符，将MutableLiveData<Int>类型转为MutableLiveData<String>类型
        var liveMap = Transformations.map(testLiveData) { input ->
            "匿名函数方式：livedata发送过来的数据是->$input"
        }
        liveMap.observe(this) {
            // 通过Transformations.map方法，将testLiveData发送过来的Int类型数据1000，转为String类型”livedata发送过来的数据是-1000“
            LineLog.d(TAG, "接收到数据: $it")
            tv_livedata_map_content2.text = it
        }
    }

    fun mapChangeClick(view: View) {
        LineLog.d(TAG, "发送数据")
        testLiveData.value = 1000;
    }

    fun livedataMapFun(i: Int?): String {
        return "具名函数方式：livedata发送过来的数据是->$i"
    }
}


