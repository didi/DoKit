package com.didichuxing.doraemonkit.kit.connect.ws



/**
 * didi Create on 2022/4/12 .
 *
 * Copyright (c) 2022/4/12 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/12 12:04 下午
 * @Description 用一句话说明文件功能
 */

interface OnWebSocketStatusChangeListener {

    fun onClosed(webSocket: OkHttpWebSocketSession, code: Int, reason: String)

    fun onOpen(webSocket: OkHttpWebSocketSession, response: String)

    fun onFailure(webSocket: OkHttpWebSocketSession, t: Throwable, response: String?)

    fun onClosing(webSocket: OkHttpWebSocketSession, code: Int, reason: String)
}
