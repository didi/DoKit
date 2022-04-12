package com.didichuxing.doraemonkit.connect.ws

import android.text.TextUtils
import com.didichuxing.doraemonkit.connect.ConnectConfig
import com.didichuxing.doraemonkit.connect.data.LoginData
import com.didichuxing.doraemonkit.connect.data.PackageType
import com.didichuxing.doraemonkit.connect.data.TextPackage
import com.didichuxing.doraemonkit.connect.parser.JsonParser
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.util.DeviceUtils
import com.didichuxing.doraemonkit.util.DokitDeviceUtils
import com.didichuxing.doraemonkit.util.UIUtils
import com.didichuxing.doraemonkit.util.Utils
import okhttp3.OkHttpClient
import okio.ByteString
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * didi Create on 2022/4/12 .
 *
 * Copyright (c) 2022/4/12 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/12 3:46 下午
 * @Description
 */

class WebSocketClient {

    val onWebSocketStatusChangeListenerSet: MutableSet<OnWebSocketStatusChangeListener> = mutableSetOf()
    val onWebSocketMessageListenerSet: MutableSet<OnWebSocketMessageListener> = mutableSetOf()
    val onWebSocketTextPackageListenerSet: MutableSet<OnWebSocketTextPackageListener> = mutableSetOf()
    val onWebSocketBytesMessageListenerSet: MutableSet<OnWebSocketBytesMessageListener> = mutableSetOf()
    val onWebSocketLoginSuccessListenerSet = mutableSetOf<OnWebSocketLoginSuccessListener>()

    private var okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .connectTimeout(5, TimeUnit.SECONDS)
        .build()

    private var webSocketSession: WebSocketSession = OkHttpWebSocketSession(okHttpClient)

    private var timer: Timer = Timer()

    constructor() {
        bindSession()
    }

    fun connect(url: String) {
        webSocketSession.connect(url)
    }

    fun reConnect() {
        webSocketSession.reConnect()
    }

    fun reConnect(url: String) {
        webSocketSession.close(0, "close")
        webSocketSession.cancel()
        webSocketSession = OkHttpWebSocketSession(okHttpClient)
        bindSession()
        webSocketSession.connect(url)
    }

    fun close() {
        stopHeart()
        webSocketSession.close(0, "close")
        webSocketSession.cancel()
    }

    fun send(text: String) {
        if (webSocketSession.connectStatus != ConnectStatus.CONNECT
            || webSocketSession.connectStatus != ConnectStatus.PREPARE
        ) {
            webSocketSession.reConnect()
        }
        webSocketSession.send(text)
    }

    private fun dispatchTextMessage(webSocket: OkHttpWebSocketSession, text: String) {
        onWebSocketMessageListenerSet.forEach {
            try {
                it.onMessage(webSocket, text)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun dispatchTextPackage(webSocket: OkHttpWebSocketSession, textPackage: TextPackage) {
        onWebSocketTextPackageListenerSet.forEach {
            try {
                it.onReceiveTextPackage(webSocket, textPackage)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun onLoginResponse(textPackage: TextPackage) {
        if (!TextUtils.isEmpty(textPackage.data)) {
            val loginData = JsonParser.toLoginData(textPackage.data)
            if (TextUtils.isEmpty(loginData.connectSerial)) {
                ConnectConfig.saveConnectSerial(loginData.connectSerial)
            }
        }
        onWebSocketLoginSuccessListenerSet.forEach {
            try {
                it.onWebSocketLoginSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun bindSession() {
        webSocketSession.onWebSocketBytesMessageListener = object : OnWebSocketBytesMessageListener {
            override fun onMessage(webSocket: OkHttpWebSocketSession, bytes: ByteString) {
                onWebSocketBytesMessageListenerSet.forEach {
                    it.onMessage(webSocket, bytes)
                }
            }
        }
        webSocketSession.onWebSocketMessageListener = object : OnWebSocketMessageListener {
            override fun onMessage(webSocket: OkHttpWebSocketSession, text: String) {
                try {
                    val textPackage = JsonParser.toTextPackage(text)
                    if (textPackage.type == PackageType.HEART_BEAT) {
                        WsLog.d("receive HEART_BEAT text=${text}")
                        return
                    } else if (textPackage.type == PackageType.LOGIN) {
                        WsLog.d("receive LOGIN text=${text}")
                        onLoginResponse(textPackage)
                        return
                    }
                    //登录及心跳不通知到外层
                    dispatchTextPackage(webSocket, textPackage)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                dispatchTextMessage(webSocket, text)
            }
        }

        webSocketSession.onWebSocketStatusChangeListener = object : OnWebSocketStatusChangeListener {
            override fun onClosed(webSocket: OkHttpWebSocketSession, code: Int, reason: String) {
                onWebSocketStatusChangeListenerSet.forEach {
                    try {
                        it.onClosed(webSocket, code, reason)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onOpen(webSocket: OkHttpWebSocketSession, response: String) {
                login()
                startHeart()
                onWebSocketStatusChangeListenerSet.forEach {
                    try {
                        it.onOpen(webSocket, response)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(webSocket: OkHttpWebSocketSession, t: Throwable, response: String?) {
                onWebSocketStatusChangeListenerSet.forEach {
                    try {
                        it.onFailure(webSocket, t, response)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onClosing(webSocket: OkHttpWebSocketSession, code: Int, reason: String) {
                onWebSocketStatusChangeListenerSet.forEach {
                    try {
                        it.onClosing(webSocket, code, reason)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun login() {
        val pi = DokitDeviceUtils.getPackageInfo(Utils.getApp())
        val name = "${DeviceUtils.getManufacturer()}-${DeviceUtils.getModel()}(${DeviceUtils.getSDKVersionName()})"
        val connectSerial = ConnectConfig.getConnectSerial()
        val loginData = LoginData(
            name,
            "android",
            "${DeviceUtils.getSDKVersionName()}",
            "${UIUtils.getWidthPixels()} x ${UIUtils.getRealHeightPixels()}",
            "${DeviceUtils.getManufacturer()}-${DeviceUtils.getModel()}",
            "${DoKitManager.IP_ADDRESS_BY_WIFI}",
            connectSerial,
            "${pi.packageName}",
            "${pi.versionName}"
        )

        webSocketSession.send(JsonParser.toLoginJson(loginData))
    }

    private fun startHeart() {
        timer.schedule(HeartTimerTask(webSocketSession), 0, 30 * 1000)
    }


    private fun stopHeart() {
        timer.cancel()
    }

    class HeartTimerTask(private var webSocketSession: WebSocketSession) : TimerTask() {
        override fun run() {
            val heartPackage = JsonParser.toJson(PackageType.HEART_BEAT, "")
            webSocketSession.send(heartPackage)
        }
    }

}
