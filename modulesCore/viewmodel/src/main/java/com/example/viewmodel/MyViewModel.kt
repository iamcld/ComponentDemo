package com.example.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @Author: chenlida
 * @Date: 2022/10/28 23:36
 * @Description:ViewModel的生命周期比activity长。发生屏幕旋转时，界面数据也不会丢失
 */
class MyViewModel : ViewModel() {
    private var i = 0
    private val testNumLd = MutableLiveData<Int>()

    fun getTestNum() : MutableLiveData<Int> {
        return testNumLd
    }

    fun addOne() {
        i++;
        testNumLd.value = i;
    }
}