package com.didichuxing.doraemonkit.kit.autotest

import android.app.Activity
import android.view.View
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.autotest.R
import com.didichuxing.doraemonkit.kit.autotest.ui.RecordingCaseDoKitView
import com.didichuxing.doraemonkit.kit.connect.ConnectAddress
import com.didichuxing.doraemonkit.kit.connect.data.PackageType
import com.didichuxing.doraemonkit.kit.connect.data.TextPackage
import com.didichuxing.doraemonkit.kit.connect.parser.JsonParser
import com.didichuxing.doraemonkit.kit.connect.ws.*
import com.didichuxing.doraemonkit.kit.core.DokitFrameLayout
import com.didichuxing.doraemonkit.kit.test.DoKitTestManager
import com.didichuxing.doraemonkit.kit.test.TestMode
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.kit.test.event.ControlEventManager
import com.didichuxing.doraemonkit.kit.test.event.OnControlEventActionListener
import com.didichuxing.doraemonkit.kit.test.mock.MockManager
import com.didichuxing.doraemonkit.kit.test.mock.ProxyMockCallback
import com.didichuxing.doraemonkit.util.LogHelper
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

/**
 * didi Create on 2022/4/6 .
 *
 * Copyright (c) 2022/4/6 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/6 5:14 下午
 * @Description 用一句话说明文件功能
 */

object AutoTestManager {

    private val mainScope = MainScope() + CoroutineName(this.toString())
    private var connectAddress: ConnectAddress? = null
    private var webSocketClient: WebSocketClient? = null

    private val recordingCaseDoKitViewList: MutableList<RecordingCaseDoKitView> = mutableListOf()

    private var mode: TestMode = TestMode.UNKNOWN

    private val eventActionListener = object : OnControlEventActionListener {
        override fun onControlEventAction(activity: Activity?, view: View?, event: ControlEvent) {
            if (view is DokitFrameLayout) {
                return
            }
            webSocketClient?.let {
                it.send(JsonParser.toJson(PackageType.BROADCAST, event, "action"))
            } ?: run {
                LogHelper.e("Autotest", "webSocketClient is null by send. ACTION")
            }
        }
    }

    private val proxyMockCallback = object : ProxyMockCallback {
        override fun send(data: String) {
            webSocketClient?.let {
                it.send(data)
            } ?: run {
                LogHelper.e("Autotest", "webSocketClient is null by send. MOCK")
            }
        }
    }


    fun getMode(): TestMode {
        return mode
    }

    fun startRecord() {
        ControlEventManager.addOnControlEventActionListener(eventActionListener)
        MockManager.proxyMockCallback = proxyMockCallback
        DoKitTestManager.startTest(TestMode.HOST)
        changeToRecordView()
    }

    fun addRecordingCaseDoKitView(view: RecordingCaseDoKitView) {

        when (mode) {
            TestMode.UNKNOWN -> {
                mainScope.launch {
                    view?.let {
                        it.changeText("已链接")
                        it.changeDotColor(R.drawable.dk_autotest_flash_red_bg)
                    }
                }
            }
            TestMode.HOST -> {
                mainScope.launch {
                    view?.let {
                        it.changeText("录制中")
                        it.changeDotColor(R.drawable.dk_autotest_flash_green_bg)
                    }
                }
            }
            TestMode.CLIENT -> {
                mainScope.launch {
                    view?.let {
                        it.changeText("测试中")
                        it.changeDotColor(R.drawable.dk_autotest_flash_blue_bg)
                    }
                }
            }
        }
        recordingCaseDoKitViewList.add(view)
    }

    fun removeRecordingCaseDoKitView(view: RecordingCaseDoKitView) {
        recordingCaseDoKitViewList.remove(view)
    }

    private fun changeToRecordView() {
        mode = TestMode.HOST
        mainScope.launch {
            recordingCaseDoKitViewList.forEach { recordingCaseDoKitView ->
                recordingCaseDoKitView?.let {
                    it.changeText("录制中")
                    it.changeDotColor(R.drawable.dk_autotest_flash_green_bg)
                }
            }
        }
    }

    private fun changeToConnectView() {
        mode = TestMode.UNKNOWN
        mainScope.launch {
            recordingCaseDoKitViewList.forEach { recordingCaseDoKitView ->
                recordingCaseDoKitView?.let {
                    it.changeText("已链接")
                    it.changeDotColor(R.drawable.dk_autotest_flash_red_bg)
                }
            }
        }
    }

    private fun changeToTestView() {
        mode = TestMode.CLIENT
        mainScope.launch {
            recordingCaseDoKitViewList.forEach { recordingCaseDoKitView ->
                recordingCaseDoKitView?.let {
                    it.changeText("测试中")
                    it.changeDotColor(R.drawable.dk_autotest_flash_blue_bg)
                }
            }
        }
    }


    fun stopRecord() {

        DoKitTestManager.closeTest()
        MockManager.proxyMockCallback = null
        ControlEventManager.removeOnControlEventActionListener(eventActionListener)

        changeToConnectView()
    }

    fun startAutoTest() {
        MockManager.proxyMockCallback = proxyMockCallback
        DoKitTestManager.startTest(TestMode.CLIENT)
        changeToTestView()
    }

    fun stopAutoTest() {

        DoKitTestManager.closeTest()
        MockManager.proxyMockCallback = null
        changeToConnectView()
    }

    fun startConnect(address: ConnectAddress) {
        connectAddress = address
        connect()
    }

    fun stopConnect() {
        webSocketClient?.close()
        recordingCaseDoKitViewList.clear()
    }

    private fun connect() {
        if (connectAddress == null) {
            return
        }
        if (webSocketClient == null) {
            webSocketClient = WebSocketClient()

            webSocketClient?.let {
                it.addOnWebSocketLoginSuccessListener(object : OnWebSocketLoginSuccessListener {
                    override fun onWebSocketLoginSuccess() {
                        mainScope.launch {
                            DoKit.launchFloating(RecordingCaseDoKitView::class.java)
                        }
                    }
                })
                it.addOnWebSocketTextPackageListener(object : OnWebSocketTextPackageListener {
                    override fun onReceiveTextPackage(webSocket: OkHttpWebSocketSession, textPackage: TextPackage) {

                    }
                })
                it.addOnWebSocketCloseListener(object : OnWebSocketCloseListener {
                    override fun onWebSocketClose() {
                        mainScope.launch {
                            DoKit.removeFloating(RecordingCaseDoKitView::class.java)
                        }
                    }
                })

                it.startAutoConnect()
                it.connect(connectAddress!!.url)
            }
        } else {
            webSocketClient?.let {
                it.reConnect(connectAddress!!.url)
            }
        }
    }
}
