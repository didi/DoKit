package com.didichuxing.doraemonkit.kit.connect

import com.didichuxing.doraemonkit.kit.connect.data.TextPackage
import com.didichuxing.doraemonkit.kit.connect.ws.OkHttpWebSocketSession
import com.didichuxing.doraemonkit.kit.connect.ws.OnWebSocketLoginSuccessListener
import com.didichuxing.doraemonkit.kit.connect.ws.OnWebSocketTextPackageListener
import com.didichuxing.doraemonkit.kit.connect.ws.WebSocketClient

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
object DoKitConnectManager {


    private var currentConnectAddress: ConnectAddress? = null
    private var webSocketClient: WebSocketClient? = null

    fun getConnectSerial(): String {
        return ConnectConfig.getConnectSerial()
    }

    fun saveConnectSerial(text: String) {
        ConnectConfig.saveConnectSerial(text)
    }

    fun getCurrentConnectAddress(): ConnectAddress? {
        return currentConnectAddress
    }

    fun setCurrentConnectAddress(address: ConnectAddress?) {
        currentConnectAddress = address
    }

    fun getWebSocketClient(): WebSocketClient? {
        return webSocketClient
    }

    fun startConnect(address: ConnectAddress) {
        currentConnectAddress = address
        if (webSocketClient == null) {
            webSocketClient = WebSocketClient()

            webSocketClient?.let {
                it.addOnWebSocketLoginSuccessListener(object : OnWebSocketLoginSuccessListener {
                    override fun onWebSocketLoginSuccess() {
                    }
                })
                it.addOnWebSocketTextPackageListener(object : OnWebSocketTextPackageListener {
                    override fun onReceiveTextPackage(webSocket: OkHttpWebSocketSession, textPackage: TextPackage) {
                    }
                })
                it.startAutoConnect()
                it.connect(address.url)
            }
        } else {
            webSocketClient?.let {
                it.reConnect(address.url)
            }
        }

    }

    fun stopConnect() {
        currentConnectAddress = null
        webSocketClient?.let {
            it.close()
        }
    }
}
