package com.didichuxing.doraemonkit.kit.connect

import com.didichuxing.doraemonkit.kit.connect.data.TextPackage
import com.didichuxing.doraemonkit.kit.connect.ws.OkHttpWebSocketSession
import com.didichuxing.doraemonkit.kit.connect.ws.OnWebSocketLoginSuccessListener
import com.didichuxing.doraemonkit.kit.connect.ws.OnWebSocketTextPackageListener
import com.didichuxing.doraemonkit.kit.connect.ws.WebSocketClient

object DoKitSConnectManager {


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


    fun startConnect(address: ConnectAddress) {
        currentConnectAddress = address
        if (webSocketClient == null) {
            webSocketClient = WebSocketClient()
        }
        webSocketClient?.let {
            it.startAutoConnect()
            it.connect(address.url)
            it.onWebSocketLoginSuccessListenerSet.add(object : OnWebSocketLoginSuccessListener {
                override fun onWebSocketLoginSuccess() {
                }
            })
            it.onWebSocketTextPackageListenerSet.add(object : OnWebSocketTextPackageListener {
                override fun onReceiveTextPackage(webSocket: OkHttpWebSocketSession, textPackage: TextPackage) {
                }
            })
        }


    }
}
