package com.didichuxing.doraemonkit.kit.connect.ws

import okio.ByteString


/**
 * didi Create on 2022/4/12 .
 *
 * Copyright (c) 2022/4/12 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/12 2:44 下午
 * @Description 自定义的WebSocket 链接接口定义
 */

interface WebSocketSession {

    var onWebSocketStatusChangeListener: OnWebSocketStatusChangeListener?
    var onWebSocketMessageListener: OnWebSocketMessageListener?
    var onWebSocketBytesMessageListener: OnWebSocketBytesMessageListener?
    var onWebSocketQueueSizeOutListener:OnWebSocketQueueSizeOutListener?
    var connectStatus: ConnectStatus

    fun connect(url: String)
    fun reConnect()
    fun send(text: String): Boolean
    fun send(bytes: ByteString): Boolean
    fun close(code: Int, reason: String): Boolean
    fun queueSize(): Long
    fun cancel()


}
