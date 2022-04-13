package com.didichuxing.doraemonkit.kit.connect.ws

import com.didichuxing.doraemonkit.util.LogHelper

object WsLog {
    private const val TAG = "WebSocket"

    fun d(msg: String) {
        LogHelper.d(TAG, msg)
    }

    fun i(msg: String) {
        LogHelper.i(TAG, msg)
    }

    fun e(msg: String) {
        LogHelper.e(TAG, msg)
    }

}
