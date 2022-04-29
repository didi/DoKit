package com.didichuxing.doraemonkit.kit.mc

import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.kit.connect.ConnectAddress
import com.didichuxing.doraemonkit.kit.connect.data.PackageType
import com.didichuxing.doraemonkit.kit.connect.data.TextPackage
import com.didichuxing.doraemonkit.kit.connect.parser.JsonParser
import com.didichuxing.doraemonkit.kit.connect.ws.*
import com.didichuxing.doraemonkit.kit.mc.ui.connect.MultiControlDoKitView
import com.didichuxing.doraemonkit.kit.test.TestMode
import com.didichuxing.doraemonkit.kit.test.mock.MockManager
import com.didichuxing.doraemonkit.kit.test.mock.ProxyMockCallback
import com.didichuxing.doraemonkit.kit.test.report.AutoTestMessage
import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.util.ToastUtils
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus


/**
 * didi Create on 2022/4/22 .
 *
 * Copyright (c) 2022/4/22 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/22 12:25 下午
 * @Description 一机多控管理
 */

object MultiControlManager {


    private val mainScope = MainScope() + CoroutineName(this.toString())

    private val webSocketClient: WebSocketClient = WebSocketClient()

    private var connectAddress: ConnectAddress? = null

    private val hostMultiController = HostMultiController(webSocketClient)

    private val clientMultiController = ClientMultiController(webSocketClient)

    private var webSocketClientCreate = false

    private var mode: TestMode = TestMode.UNKNOWN

    private val onModeChangeListeners: MutableSet<OnMultiControlModeChangeListener> = mutableSetOf()

    fun getMode(): TestMode {
        return mode
    }

    fun startHostMode(connectAddress: ConnectAddress) {
        this.connectAddress = connectAddress
        mode = TestMode.HOST
        connect()
        clientMultiController.close()
        hostMultiController.start()
        onNotifyMultiControlModeChange(mode)
    }

    private fun startHostMode() {
        mode = TestMode.HOST
        clientMultiController.close()
        hostMultiController.start()
        sendChangeHostMode()
        onNotifyMultiControlModeChange(mode)
        ToastUtils.showShort("主机模式")
    }

    fun startClientMode(connectAddress: ConnectAddress) {
        this.connectAddress = connectAddress
        mode = TestMode.CLIENT
        connect()
        hostMultiController.close()
        clientMultiController.start()
        onNotifyMultiControlModeChange(mode)
    }

    private fun startClientMode() {
        mode = TestMode.CLIENT
        hostMultiController.close()
        clientMultiController.start()
        onNotifyMultiControlModeChange(mode)
        ToastUtils.showShort("从机模式")
    }

    fun changeMode(testMode: TestMode) {
        if (testMode == TestMode.HOST) {
            startHostMode()
        } else if (testMode == TestMode.CLIENT) {
            startClientMode()
        }
        MultiControlDoKitView.updateConnectMode()
    }

    fun changeMode() {
        if (mode == TestMode.HOST) {
            startClientMode()
        } else if (mode == TestMode.CLIENT) {
            startHostMode()
        }
        MultiControlDoKitView.updateConnectMode()
    }

    fun closeWorkMode() {
        mode = TestMode.UNKNOWN
        clientMultiController.close()
        hostMultiController.close()
        stopConnect()

        onNotifyMultiControlModeChange(mode)
    }

    private fun sendChangeHostMode() {
        webSocketClient.let {
            it.send(JsonParser.toJson(PackageType.BROADCAST, "", "mc_host"))
        }
    }

    private fun onReceiveHostModeChange() {
        mainScope.launch {
            if (mode == TestMode.HOST) {
                changeMode(TestMode.CLIENT)
            }
        }
    }

    fun addOnMultiControlModeChangeListener(listener: OnMultiControlModeChangeListener) {
        onModeChangeListeners.add(listener)
    }

    fun removeOnMultiControlModeChangeListener(listener: OnMultiControlModeChangeListener) {
        onModeChangeListeners.remove(listener)
    }

    private fun onNotifyMultiControlModeChange(mode: TestMode) {
        onModeChangeListeners.forEach {
            it.onMultiControlModeChanged(mode)
        }
    }

    private fun onReceiveControl(textPackage: TextPackage) {
        val text = textPackage.data
        val autoTestMessage = GsonUtils.fromJson<AutoTestMessage>(text, AutoTestMessage::class.java)

        when (autoTestMessage.command) {
            "startRecord" -> {
                val msg = AutoTestMessage(command = "control_response", message = "success")
                onResponseAutoTestMessage(msg)
            }
        }
    }

    /**
     * 控制消息响应
     */
    private fun onResponseAutoTestMessage(autoTestMessage: AutoTestMessage) {
        webSocketClient?.let {
            it.send(JsonParser.toJson(PackageType.AUTOTEST, autoTestMessage, "auto_test_control"))
        }
    }

    /**
     * 接收到自动化测试事件
     */
    private fun onReceiveAction(textPackage: TextPackage) {
        clientMultiController.onReceiveAction(textPackage)
    }


    private fun stopConnect() {
        webSocketClient.close()
    }

    private fun connect() {
        if (connectAddress == null) {
            return
        }
        if (!webSocketClientCreate) {
            webSocketClientCreate = true
            webSocketClient.let {
                it.addOnWebSocketLoginSuccessListener(object : OnWebSocketLoginSuccessListener {
                    override fun onWebSocketLoginSuccess() {
                        mainScope.launch {
                            DoKit.launchFloating(MultiControlDoKitView::class.java)
                        }
                    }
                })
                it.addOnWebSocketTextPackageListener(object : OnWebSocketTextPackageListener {
                    override fun onReceiveTextPackage(webSocket: OkHttpWebSocketSession, textPackage: TextPackage) {
                        if (textPackage.type == PackageType.AUTOTEST || textPackage.type == PackageType.BROADCAST) {
                            when (textPackage.contentType) {
                                "mc_host" -> {
                                    onReceiveHostModeChange()
                                }
                                "auto_test_control" -> {
                                    onReceiveControl(textPackage)
                                }
                                "action" -> {
                                    onReceiveAction(textPackage)
                                }
                                else -> {

                                }
                            }
                        } else if (textPackage.type == PackageType.DATA) {
                            MockManager.receiveQueryResponse(textPackage)
                        }
                    }
                })
                it.addOnWebSocketCloseListener(object : OnWebSocketCloseListener {
                    override fun onWebSocketClose() {
                        mainScope.launch {
                            DoKit.removeFloating(MultiControlDoKitView::class.java)
                        }
                    }
                })

                it.addOnWebSocketStatusChangeListener(object : OnWebSocketStatusChangeListener {
                    override fun onClosed(webSocket: OkHttpWebSocketSession, code: Int, reason: String) {
                    }

                    override fun onOpen(webSocket: OkHttpWebSocketSession, response: String) {
                    }

                    override fun onFailure(webSocket: OkHttpWebSocketSession, t: Throwable, response: String?) {
                        ToastUtils.showShort("链接失败:" + t.message)
                    }

                    override fun onClosing(webSocket: OkHttpWebSocketSession, code: Int, reason: String) {
                    }
                })

                MockManager.proxyMockCallback = object : ProxyMockCallback {
                    override fun send(data: String) {
                        webSocketClient.send(data)
                    }
                }

                it.startAutoConnect()
                it.connect(connectAddress!!.url)
            }
        } else {
            webSocketClient.startAutoConnect()
            webSocketClient.reConnect(connectAddress!!.url)
        }

    }

}
