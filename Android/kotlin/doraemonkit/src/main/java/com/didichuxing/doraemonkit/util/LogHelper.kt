package com.didichuxing.doraemonkit.util

import com.blankj.utilcode.util.LogUtils

/**
 * Created by wanglikun on 2018/9/10.
 */
object LogHelper {
    private const val TAG = "DoraemonKit"
    private var IS_DEBUG = true
    fun d(subTag: String, msg: String) {
        if (!IS_DEBUG) {
            return
        }
        LogUtils.v("[$subTag]: $msg")
    }

    fun i(subTag: String, msg: String) {
        if (!IS_DEBUG) {
            return
        }
        LogUtils.v("[$subTag]: $msg")
    }

    fun e(subTag: String, msg: String) {
        if (!IS_DEBUG) {
            return
        }
        LogUtils.v("[$subTag]: $msg")
    }

    fun setDebug(debug: Boolean) {
        IS_DEBUG = debug
    }
}