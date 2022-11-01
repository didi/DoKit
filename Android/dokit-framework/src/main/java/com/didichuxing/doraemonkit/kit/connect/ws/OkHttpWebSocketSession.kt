package com.didichuxing.doraemonkit.kit.connect.ws

import okhttp3.*
import okio.ByteString


/**
 * didi Create on 2022/4/12 .
 *
 * Copyright (c) 2022/4/12 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/12 11:42 上午
 * @Description 自定义的WebSocket 链接辅助类实现，通过自定义的接口可以修改为其他实现方式
 */

class OkHttpWebSocketSession(val mClient: OkHttpClient) : WebSocketSession {


    override var onWebSocketStatusChangeListener: OnWebSocketStatusChangeListener? = null
    override var onWebSocketMessageListener: OnWebSocketMessageListener? = null
    override var onWebSocketBytesMessageListener: OnWebSocketBytesMessageListener? = null
    override var onWebSocketQueueSizeOutListener: OnWebSocketQueueSizeOutListener? = null
    override var connectStatus: ConnectStatus = ConnectStatus.OFF_LINE

    private var request: Request? = null
    private var webSocket: WebSocket? = null
    private val webSocketSession: OkHttpWebSocketSession = this


    override fun connect(url: String) {
        if (webSocket == null) {
            val request: Request = Request.Builder().get().url(url).build()
            webSocket = mClient.newWebSocket(request, webSocketListener)
            this.request = request
            connectStatus = ConnectStatus.PREPARE
        }
    }

    override fun reConnect() {
        if (webSocket != null) {
            webSocket?.close(1001, "close")
            webSocket = mClient.newWebSocket(request, webSocketListener)
            connectStatus = ConnectStatus.PREPARE
        }
    }

    override fun send(text: String): Boolean {
        webSocket?.let {
            if (it.queueSize() > 20) {
                onWebSocketQueueSizeOutListener?.onWebSocketQueueSizeOut()
                return it.send(text)
            }
            return it.send(text)
        }
        return false
    }

    override fun send(bytes: ByteString): Boolean {
        webSocket?.let {
            if (it.queueSize() > 20) {
                onWebSocketQueueSizeOutListener?.onWebSocketQueueSizeOut()
                return it.send(bytes)
            }
            return it.send(bytes)
        }
        return false
    }

    override fun close(code: Int, reason: String): Boolean {
        webSocket?.let {
            return it.close(code, reason)
        }
        return false
    }


    override fun queueSize(): Long {
        webSocket?.let {
            return it.queueSize()
        }
        return 0
    }

    override fun cancel() {
        onWebSocketStatusChangeListener = null
        onWebSocketMessageListener = null
        onWebSocketBytesMessageListener = null
        webSocket?.cancel()
    }


    private val webSocketListener = object : WebSocketListener() {

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            connectStatus = ConnectStatus.OFF_LINE
            onWebSocketStatusChangeListener?.onClosed(webSocketSession, code, reason)
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            connectStatus = ConnectStatus.CONNECT
            val text = response.body()?.string() ?: ""
            onWebSocketStatusChangeListener?.onOpen(webSocketSession, text)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            connectStatus = ConnectStatus.FAILED
            val text = response?.body()?.string() ?: ""
            onWebSocketStatusChangeListener?.onFailure(webSocketSession, t, text)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            onWebSocketMessageListener?.onMessage(webSocketSession, text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            onWebSocketBytesMessageListener?.onMessage(webSocketSession, bytes)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            onWebSocketStatusChangeListener?.onClosing(webSocketSession, code, reason)
        }
    }
}
