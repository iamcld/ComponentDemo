package com.example.libbase

import android.util.Log
import java.lang.StringBuilder

/**
 * @Author: chenlida
 * @Date: 2022/10/25 15:18
 * @Description:
 */
object LineLog {
    private const val TAG = "LineLog"
    private val isDebug = BuildConfig.DEBUG



    fun v(content: String) {
        if (isDebug) {
            log(Log.VERBOSE, TAG, content)
        }
    }


    fun v(tag: String, content: String) {
        if (isDebug) {
            log(Log.VERBOSE, tag, content)
        }
    }

    fun d(content: String) {
        if (isDebug) {
            log(Log.DEBUG, TAG, content)
        }
    }
    fun d(tag: String, content: String) {
        if (isDebug) {
            log(Log.DEBUG, tag, content)
        }
    }

    fun i(content: String) {
        if (isDebug) {
            log(Log.INFO, TAG, content)
        }
    }

    fun i(tag: String, content: String) {
        if (isDebug) {
            log(Log.INFO, tag, content)
        }
    }

    fun w(content: String) {
        if (isDebug) {
            log(Log.WARN, TAG, content)
        }
    }

    fun w(tag: String, content: String) {
        if (isDebug) {
            log(Log.WARN, tag, content)
        }
    }


    fun e(content: String) {
        if (isDebug) {
            log(Log.ERROR, TAG, content)
        }
    }

    fun e(tag: String, content: String) {
        if (isDebug) {
            log(Log.ERROR, tag, content)
        }
    }

    private fun log(level: Int, tag: String, content: String) {

        val msg= getTraceInfo(content)
        when(level){
            Log.VERBOSE-> Log.v(tag, msg)
            Log.DEBUG-> Log.d(tag, msg)
            Log.INFO-> Log.i(tag, msg)
            Log.WARN-> Log.w(tag, msg)
            Log.ERROR-> Log.e(tag, msg)
            else-> Log.v(tag, msg)
        }
    }

    private fun getTraceInfo(content: String): String {
        val stes = Thread.currentThread().stackTrace ?: return content
        val result: StringBuilder? = null
        val sb = StringBuilder()
        for (i in stes.indices) {
            val ste = stes[i]
            if (ignorable(ste)) {
                continue
            }
            sb.append("(").append(ste.fileName)
                .append(":").append(ste.lineNumber)
                .append(")")
                .append(".").append(ste.methodName)
                .append("  ")
                .append("msg:")
                .append(content)
            return sb.toString()
        }
        return result.toString()
    }

    private fun ignorable(ste: StackTraceElement): Boolean {
        return ste.isNativeMethod || ste.className == Thread::class.java.name || ste.className == LineLog::class.java.name
    }
}