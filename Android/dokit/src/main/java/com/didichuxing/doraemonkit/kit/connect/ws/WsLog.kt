package com.didichuxing.doraemonkit.kit.connect.ws

import com.didichuxing.doraemonkit.util.LogHelper
/**
 * didi Create on 2022/4/12 .
 *
 * Copyright (c) 2022/4/12 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/12 6:07 下午
 * @Description 用一句话说明文件功能
 */
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
