package com.example.livedata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.android.synthetic.main.activity_live_data_for_switch_map.*

class LiveDataForSwitchMapActivity : AppCompatActivity() {

    private val switchLiveData = MutableLiveData<Boolean>()
    private val liveData1 = MutableLiveData<String>()
    private val liveData2 = MutableLiveData<String>()
    private var switchMap = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_data_for_switch_map)

        // 通过switchLiveData.value为true或者false，来决定使用liveData1或者liveData2
        val switchMapLiveData =
            Transformations.switchMap(switchLiveData, object : Function<Boolean, LiveData<String>> {
                override fun apply(input: Boolean): LiveData<String> {
                    if (input) {
                        return liveData1
                    }
                    return liveData2
                }
            })

        switchMapLiveData.observe(this) {
            tv_test_content.text = it
        }
    }

    // 通过不断点击按钮，使得switchLiveData.value在false之间切换
    // 从而switchMapLiveData的值也不断在liveData1和liveData2之间切换
    fun changeClick(view: View) {
        liveData2.value = "false的值"
        liveData1.value = "true的值"
        switchLiveData.value = switchMap
        switchMap = !switchMap

    }
}