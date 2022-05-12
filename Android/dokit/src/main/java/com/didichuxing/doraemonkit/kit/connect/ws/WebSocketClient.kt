package com.didichuxing.doraemonkit.kit.connect.ws

import android.text.TextUtils
import com.didichuxing.doraemonkit.kit.connect.ConnectConfig
import com.didichuxing.doraemonkit.kit.connect.data.LoginData
import com.didichuxing.doraemonkit.kit.connect.data.PackageType
import com.didichuxing.doraemonkit.kit.connect.data.TextPackage
import com.didichuxing.doraemonkit.kit.connect.parser.JsonParser
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.util.*
import okhttp3.OkHttpClient
import okio.ByteString
import java.text.SimpleDateFormat
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

    private val onWebSocketStatusChangeListenerSet: MutableSet<OnWebSocketStatusChangeListener> = mutableSetOf()
    private val onWebSocketMessageListenerSet: MutableSet<OnWebSocketMessageListener> = mutableSetOf()
    private val onWebSocketTextPackageListenerSet: MutableSet<OnWebSocketTextPackageListener> = mutableSetOf()
    private val onWebSocketBytesMessageListenerSet: MutableSet<OnWebSocketBytesMessageListener> = mutableSetOf()
    private val onWebSocketLoginSuccessListenerSet = mutableSetOf<OnWebSocketLoginSuccessListener>()

    private val onWebSocketCloseListenerSet = mutableSetOf<OnWebSocketCloseListener>()
    private val onWebSocketQueueSizeOutListenerSet = mutableSetOf<OnWebSocketQueueSizeOutListener>()
    private val onWebSocketReConnectListenerSet = mutableSetOf<OnWebSocketReConnectListener>()


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
    private var closed: Boolean = true

    constructor() {
        bindTask()
        bindSession()
    }

    fun connect(url: String) {
        WsLog.i("connect. ")
        closed = false
        webSocketSession.connect(url)
    }

    fun reConnect() {
        WsLog.i("reConnect. ")
        closed = false
        webSocketSession.reConnect()
    }

    fun reConnect(url: String) {
        WsLog.i("reConnect url=${url} ")
        closed = false
        loginSuccess = false
        webSocketSession.close(1001, "close")
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
        webSocketSession.close(1000, "close")
        webSocketSession.cancel()
        closed = true

        onWebSocketCloseListenerSet.forEach {
            it.onWebSocketClose()
        }
    }

    /**
     * 不在使用的时候需要销毁释放资源
     * 一旦销毁就不可继续使用
     */
    fun destroy() {
        close()
        stopTimer()
    }


    fun isLoginSuccess(): Boolean {
        return loginSuccess
    }

    /**
     * 客户端是否在关闭状态，主动关闭
     */
    fun isClosed(): Boolean {
        return closed
    }

    fun getConnectStatus(): ConnectStatus {
        return webSocketSession.connectStatus
    }

    fun send(text: String): Boolean {
        return webSocketSession.send(text)
    }

    fun send(bytes: ByteString): Boolean {
        return webSocketSession.send(bytes)
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
        timer.schedule(KeepConnectTimerTask(), 5000, 2000)
        timer.schedule(HeartTimerTask(), 5000, 30 * 1000)
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

        webSocketSession.onWebSocketQueueSizeOutListener = object : OnWebSocketQueueSizeOutListener {

            override fun onWebSocketQueueSizeOut() {
                WsLog.i("onQueueSize out. ")
                onWebSocketQueueSizeOutListenerSet.forEach {
                    try {
                        it.onWebSocketQueueSizeOut()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
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
                checkKeepConnect()
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

    fun addOnWebSocketReConnectListener(listener: OnWebSocketReConnectListener) {
        onWebSocketReConnectListenerSet.add(listener)
    }

    fun removeOnWebSocketReConnectListener(listener: OnWebSocketReConnectListener) {
        onWebSocketReConnectListenerSet.remove(listener)
    }

    fun addOnWebSocketQueueSizeOutListener(listener: OnWebSocketQueueSizeOutListener) {
        onWebSocketQueueSizeOutListenerSet.add(listener)
    }

    fun removeOnWebSocketQueueSizeOutListener(listener: OnWebSocketQueueSizeOutListener) {
        onWebSocketQueueSizeOutListenerSet.remove(listener)
    }

    fun addOnWebSocketCloseListener(listener: OnWebSocketCloseListener) {
        onWebSocketCloseListenerSet.add(listener)
    }

    fun removeOnWebSocketCloseListener(listener: OnWebSocketCloseListener) {
        onWebSocketCloseListenerSet.remove(listener)
    }

    fun addOnWebSocketLoginSuccessListener(listener: OnWebSocketLoginSuccessListener) {
        onWebSocketLoginSuccessListenerSet.add(listener)
    }

    fun removeOnWebSocketLoginSuccessListener(listener: OnWebSocketLoginSuccessListener) {
        onWebSocketLoginSuccessListenerSet.remove(listener)
    }

    fun addOnWebSocketBytesMessageListener(listener: OnWebSocketBytesMessageListener) {
        onWebSocketBytesMessageListenerSet.add(listener)
    }

    fun removeOnWebSocketBytesMessageListener(listener: OnWebSocketBytesMessageListener) {
        onWebSocketBytesMessageListenerSet.remove(listener)
    }

    fun addOnWebSocketTextPackageListener(listener: OnWebSocketTextPackageListener) {
        onWebSocketTextPackageListenerSet.add(listener)
    }

    fun removeOnWebSocketTextPackageListener(listener: OnWebSocketTextPackageListener) {
        onWebSocketTextPackageListenerSet.remove(listener)
    }

    fun addOnWebSocketMessageListener(listener: OnWebSocketMessageListener) {
        onWebSocketMessageListenerSet.add(listener)
    }

    fun removeOnWebSocketMessageListener(listener: OnWebSocketMessageListener) {
        onWebSocketMessageListenerSet.remove(listener)
    }

    fun addOnWebSocketStatusChangeListener(listener: OnWebSocketStatusChangeListener) {
        onWebSocketStatusChangeListenerSet.add(listener)
    }

    fun removeOnWebSocketStatusChangeListener(listener: OnWebSocketStatusChangeListener) {
        onWebSocketStatusChangeListenerSet.remove(listener)
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
            onWebSocketReConnectListenerSet.forEach {
                it.onWebSocketReConnect()
            }
        }
    }

    inner class HeartTimerTask : TimerTask() {
        override fun run() {
            if (!heartBeatEnable) {
                return
            }
            val dateTime = TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS"))
            val heartPackage = JsonParser.toJson(PackageType.HEART_BEAT, "{'dateTime':'${dateTime}}'")
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
