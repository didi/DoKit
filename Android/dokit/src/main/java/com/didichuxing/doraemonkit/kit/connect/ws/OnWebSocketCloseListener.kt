package com.didichuxing.doraemonkit.kit.connect.ws


/**
 * didi Create on 2022/4/15 .
 *
 * Copyright (c) 2022/4/15 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/15 11:14 上午
 * @Description 链接关闭监听，主动的链接关闭，因网络或其他原因导致的关闭不触发通知
 */

interface OnWebSocketCloseListener {
    fun onWebSocketClose()
}
