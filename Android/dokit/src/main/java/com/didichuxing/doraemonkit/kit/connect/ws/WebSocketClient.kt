package com.didichuxing.doraemonkit.kit.connect.ws

import android.text.TextUtils
import com.didichuxing.doraemonkit.kit.connect.ConnectConfig
import com.didichuxing.doraemonkit.kit.connect.data.LoginData
import com.didichuxing.doraemonkit.kit.connect.data.PackageType
import com.didichuxing.doraemonkit.kit.connect.data.TextPackage
import com.didichuxing.doraemonkit.kit.connect.parser.JsonParser
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

    private var timer: Timer = Timer(true)
    private var loginSuccess: Boolean = false
    private var autoConnect: Boolean = false
    private var heartBeatEnable: Boolean = false
    private var keepConnectEnable: Boolean = false

    constructor() {
        bindTask()
        bindSession()
    }

    fun connect(url: String) {
        WsLog.i("connect. ")
        webSocketSession.connect(url)
    }

    fun reConnect() {
        WsLog.i("reConnect. ")
        webSocketSession.reConnect()
    }

    fun reConnect(url: String) {
        WsLog.i("reConnect url=${url} ")
        loginSuccess = false
        webSocketSession.close(0, "close")
        webSocketSession.cancel()
        webSocketSession = OkHttpWebSocketSession(okHttpClient)
        bindSession()
        webSocketSession.connect(url)
    }

    fun close() {
        WsLog.i("onClosed. ")
        loginSuccess = false
        stopHeart()
        stopAutoConnect()
        webSocketSession.close(0, "close")
        webSocketSession.cancel()
    }

    /**
     * 不在使用的时候需要销毁释放资源
     * 一旦销毁就不可继续使用
     */
    fun destroy() {
        close()
        stopTimer()
    }

    fun send(text: String) {
        if (webSocketSession.connectStatus != ConnectStatus.CONNECT
            || webSocketSession.connectStatus != ConnectStatus.PREPARE
        ) {
            webSocketSession.reConnect()
        }
        webSocketSession.send(text)
    }

    fun startAutoConnect() {
        autoConnect = true
        keepConnectEnable = true
    }

    fun stopAutoConnect() {
        autoConnect = false
        keepConnectEnable = false
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
            if (!TextUtils.isEmpty(loginData.connectSerial)) {
                ConnectConfig.saveConnectSerial(loginData.connectSerial)
            }
        }
        loginSuccess = true
        onWebSocketLoginSuccessListenerSet.forEach {
            try {
                it.onWebSocketLoginSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun bindTask() {
        timer.schedule(KeepConnectTimerTask(), 2000, 2000)
        timer.schedule(HeartTimerTask(), 3000, 5 * 1000)
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
                WsLog.i("onClosed. ")
                loginSuccess = false
                stopHeart()
                checkKeepConnect()
                onWebSocketStatusChangeListenerSet.forEach {
                    try {
                        it.onClosed(webSocket, code, reason)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onOpen(webSocket: OkHttpWebSocketSession, response: String) {
                WsLog.i("onOpen. ")
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
                WsLog.i("onFailure. ")
                loginSuccess = false
                stopHeart()
                onWebSocketStatusChangeListenerSet.forEach {
                    try {
                        it.onFailure(webSocket, t, response)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onClosing(webSocket: OkHttpWebSocketSession, code: Int, reason: String) {
                WsLog.i("onClosing. ")
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
        heartBeatEnable = true

    }


    private fun stopHeart() {
        heartBeatEnable = false
    }

    private fun stopTimer() {
        timer.cancel()
    }

    private fun checkKeepConnect() {
        keepConnectEnable = true
    }

    private fun checkReConnect() {
        if (webSocketSession.connectStatus != ConnectStatus.CONNECT
            && webSocketSession.connectStatus != ConnectStatus.PREPARE
        ) {
            webSocketSession.reConnect()
        }
    }

    inner class HeartTimerTask : TimerTask() {
        override fun run() {
            if (!heartBeatEnable) {
                return
            }
            val heartPackage = JsonParser.toJson(PackageType.HEART_BEAT, "")
            val success = webSocketSession.send(heartPackage)
            if (!success) {
                WsLog.i("HeartTimerTask.send() not success. ")
                checkReConnect()
            }
        }
    }

    inner class KeepConnectTimerTask : TimerTask() {
        override fun run() {
            if (!keepConnectEnable) {
                return
            }
            checkReConnect()
        }
    }

}
