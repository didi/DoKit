package com.didichuxing.doraemonkit.kit.autotest

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.kit.autotest.ui.RecordingCaseDoKitView
import com.didichuxing.doraemonkit.kit.connect.ConnectAddress
import com.didichuxing.doraemonkit.kit.connect.data.PackageType
import com.didichuxing.doraemonkit.kit.connect.data.TextPackage
import com.didichuxing.doraemonkit.kit.connect.parser.ByteParser
import com.didichuxing.doraemonkit.kit.connect.parser.JsonParser
import com.didichuxing.doraemonkit.kit.connect.ws.*
import com.didichuxing.doraemonkit.kit.core.DoKitFrameLayout
import com.didichuxing.doraemonkit.kit.test.DoKitTestManager
import com.didichuxing.doraemonkit.kit.test.TestMode
import com.didichuxing.doraemonkit.kit.test.event.*
import com.didichuxing.doraemonkit.kit.test.mock.MockManager
import com.didichuxing.doraemonkit.kit.test.mock.ProxyMockCallback
import com.didichuxing.doraemonkit.kit.test.report.ScreenShotManager
import com.didichuxing.doraemonkit.util.*
import kotlinx.coroutines.*
import okio.ByteString
import java.io.ByteArrayOutputStream
import java.lang.Runnable

/**
 * didi Create on 2022/4/6 .
 *
 * Copyright (c) 2022/4/6 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/6 5:14 下午
 * @Description 自动化测试管理
 */

object AutoTestManager {

    private val mainScope = MainScope() + CoroutineName(this.toString())

    private val uploadScope = CoroutineScope(SupervisorJob() + Dispatchers.IO) + CoroutineName("upload")

    private val delayHandler: DelayHandler = DelayHandler()

    private val autoTestStateSet: MutableMap<String, AutoTestState> = mutableMapOf()

    private var connectAddress: ConnectAddress? = null

    private var webSocketClient: WebSocketClient = WebSocketClient()

    private var mode: TestMode = TestMode.UNKNOWN

    private var screenShotManager: ScreenShotManager = ScreenShotManager("doKit/autotest/screen")

    private var diffEventTask: EventScreenShotTask? = null

    private var webSocketClientCreate = false


    class EventScreenShotTask(private val event: ControlEvent) : Runnable {
        override fun run() {
            val state = autoTestStateSet.remove(event.eventId)
            state?.let {
                val message = state.message
                val bitmap = screenShotManager.screenshotBitmap()
                if (bitmap != null) {
                    val name = screenShotManager.createNextFileName()
                    message.params["imageName"] = name
                    message.params["type"] = "webp"
                } else {
                    message.params["imageName"] = ""
                    message.params["type"] = ""
                }
                if (event.eventType == EventType.WSE_TCP_EVENT && event.diffTime < 1000) {
                    onResponseAutoTestAction(message, bitmap)
                } else {
                    onResponseAutoTestAction(message, bitmap)
                }
            } ?: run {
                val message = AutoTestMessage(command = "action_response", message = "failed")
                message.params["eventId"] = event.eventId
                message.params["imageName"] = ""
                message.params["type"] = ""
                onResponseAutoTestAction(message)
            }
        }
    }

    fun getMode(): TestMode {
        return mode
    }

    fun startRecord() {
        ControlEventManager.resetLastEventDateTime()
        ControlEventManager.addOnControlEventInterceptor(eventActionInterceptor)
        ControlEventManager.addOnControlEventActionListener(eventActionListener)
        MockManager.proxyMockCallback = proxyMockCallback
        MockManager.startTest(TestMode.HOST)
        DoKitTestManager.startTest(TestMode.HOST)
        changeToRecordView()

        ToastUtils.showShort("已开始录制")
    }


    fun stopRecord() {
        MockManager.closeTest()
        DoKitTestManager.closeTest()
        MockManager.proxyMockCallback = null
        ControlEventManager.removeOnControlEventActionListener(eventActionListener)
        ControlEventManager.removeOnControlEventInterceptor(eventActionInterceptor)

        changeToConnectView()
        ToastUtils.showShort("已停止录制")
    }

    fun startAutoTest() {

        MockManager.proxyMockCallback = proxyMockCallback
        ControlEventManager.addOnControlEventActionProcessListener(actionProcessListener)
        MockManager.startTest(TestMode.CLIENT)
        DoKitTestManager.startTest(TestMode.CLIENT)
        changeToTestView()
        ToastUtils.showShort("已开始测试")
    }

    fun stopAutoTest() {
        MockManager.closeTest()
        DoKitTestManager.closeTest()
        MockManager.proxyMockCallback = null
        ControlEventManager.removeOnControlEventActionProcessListener(actionProcessListener)
        changeToConnectView()
        ToastUtils.showShort("已停止测试")
    }

    fun startConnect(address: ConnectAddress) {
        connectAddress = address
        connect()
    }

    fun stopConnect() {
        webSocketClient?.close()
    }

    fun send(bytes: ByteString): Boolean {
        webSocketClient?.let {
            it.send(bytes)
            return true
        }
        return false
    }

    private fun changeToRecordView() {
        mode = TestMode.HOST
        mainScope.launch {
            RecordingCaseDoKitView.changeMode(mode)
        }
    }

    private fun changeToConnectView() {
        mode = TestMode.UNKNOWN
        mainScope.launch {
            RecordingCaseDoKitView.changeMode(mode)
        }
    }

    private fun changeToTestView() {
        mode = TestMode.CLIENT
        mainScope.launch {
            RecordingCaseDoKitView.changeMode(mode)
        }
    }

    private fun onReceiveControl(textPackage: TextPackage) {
        val text = textPackage.data
        val autoTestMessage = GsonUtils.fromJson<AutoTestMessage>(text, AutoTestMessage::class.java)

        when (autoTestMessage.command) {
            "startRecord" -> {
                startRecord()
                val msg = AutoTestMessage(command = "control_response", message = "success")
                msg.params["command"] = autoTestMessage.command
                onResponseAutoTestMessage(msg)
            }
            "stopRecord" -> {
                stopRecord()
                val msg = AutoTestMessage(command = "control_response", message = "success")
                msg.params["command"] = autoTestMessage.command
                onResponseAutoTestMessage(msg)
            }
            "startAutoTest" -> {
                startAutoTest()
                val msg = AutoTestMessage(command = "control_response", message = "success")
                msg.params["command"] = autoTestMessage.command
                onResponseAutoTestMessage(msg)
            }
            "stopAutoTest" -> {
                onStopAutoTest(autoTestMessage)
            }
        }
    }

    private fun onStopAutoTest(autoTestMessage: AutoTestMessage) {
        delayHandler.postDelayed(object : Runnable {
            override fun run() {
                stopAutoTest()
                val msg = AutoTestMessage(command = "control_response", message = "success")
                msg.params["command"] = autoTestMessage.command
                onResponseAutoTestMessage(msg)
            }
        }, 3000)

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
     * 自动化测试行为事件响应
     */
    private fun onResponseAutoTestAction(autoTestMessage: AutoTestMessage) {
        webSocketClient?.let {
            it.send(JsonParser.toJson(PackageType.AUTOTEST, autoTestMessage, "action"))
        }
    }

    /**
     * 自动化测试行为事件响应
     */
    private fun onResponseAutoTestAction(autoTestMessage: AutoTestMessage, bitmap: Bitmap) {
        uploadScope.launch {
            val stream = ByteArrayOutputStream(2048)
            val ok = bitmap.compress(Bitmap.CompressFormat.WEBP, 10, stream)
            val bytes = stream.toByteArray()

            val textPackage = JsonParser.toTextPackage(PackageType.AUTOTEST, autoTestMessage, "action")
            val byteString = ByteParser.toByteString(textPackage, bytes)
            webSocketClient.send(byteString)
            stream.close()
        }
    }

    private fun isDiffTimeEvent(event: ControlEvent): Boolean {
        when (event.eventType) {
            EventType.WSE_CUSTOM_EVENT -> {
                event.params?.let {
                    var testRecording: String? = it["testRecording"]
                    if (testRecording == "false") {
                        return false
                    }
                }
                return true
            }
            EventType.WSE_TCP_EVENT,
            EventType.APP_ON_FOREGROUND,
            EventType.APP_ON_BACKGROUND,
            EventType.ACTIVITY_BACK_PRESSED -> {
                return true
            }
            EventType.WSE_COMMON_EVENT -> {
                event.viewC12c?.let {
                    when (it.actionType) {
                        ActionType.ON_LONG_CLICK,
                        ActionType.ON_SCROLL,
                        ActionType.ON_INPUT_CHANGE,
                        ActionType.ON_CLICK -> {
                            return true
                        }
                        else -> {
                        }
                    }
                }
            }

        }
        return false
    }


    private fun getDiffTimeByEvent(event: ControlEvent, diffTime: Long): Long {
        when (event.eventType) {
            EventType.WSE_TCP_EVENT -> {
                return diffTime
            }
            EventType.WSE_COMMON_EVENT -> {
                event.viewC12c?.let {
                    when (it.actionType) {
                        ActionType.ON_SCROLL,
                        ActionType.ON_INPUT_CHANGE -> {
                            return if (diffTime > 100) {
                                diffTime
                            } else {
                                100
                            }
                        }
                        else -> {
                        }
                    }
                }
            }
        }
        return 1000
    }

    /**
     * 接收到自动化测试事件
     */
    private fun onReceiveAction(textPackage: TextPackage) {
        val text = textPackage.data
        val event = GsonUtils.fromJson<ControlEvent>(text, ControlEvent::class.java)

        if (isDiffTimeEvent(event)) {
            val diff: Long = if (event.diffTime < 1000) {
                getDiffTimeByEvent(event, event.diffTime)
            } else {
                event.diffTime
            }
            val eventTask = EventScreenShotTask(event)
            diffEventTask = eventTask
            delayHandler.postDelayed(eventTask, diff)
        }

        mainScope.launch {
            ControlEventManager.onReceiveControlEventAction(event)
        }
    }


    private fun connect() {
        if (connectAddress == null) {
            return
        }
        if (!webSocketClientCreate) {
            webSocketClientCreate = true

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
                        if (textPackage.type == PackageType.AUTOTEST || textPackage.type == PackageType.BROADCAST) {
                            when (textPackage.contentType) {
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
                            DoKit.removeFloating(RecordingCaseDoKitView::class.java)
                        }
                    }
                })

                it.startAutoConnect()
                it.connect(connectAddress!!.url)
            }
        } else {
            webSocketClient?.let {
                it.startAutoConnect()
                it.reConnect(connectAddress!!.url)
            }
        }
    }

    private val eventActionInterceptor = object : OnControlEventInterceptor {
        override fun onControlEventAction(activity: Activity?, view: View?, controlEvent: ControlEvent): Boolean {
            if (view is DoKitFrameLayout) {
                return true
            }
            return false
        }
    }

    private val eventActionListener = object : OnControlEventActionListener {
        override fun onControlEventAction(activity: Activity?, view: View?, event: ControlEvent) {
            webSocketClient.send(JsonParser.toJson(PackageType.BROADCAST, event, "action"))
        }
    }

    private val proxyMockCallback = object : ProxyMockCallback {
        override fun send(data: String) {
            webSocketClient.send(data)
        }
    }


    private val actionProcessListener = object : OnControlEventActionProcessListener {
        override fun onControlEventProcessSuccess(activity: Activity?, view: View?, controlEvent: ControlEvent) {
            val msg = AutoTestMessage(command = "action_response", message = "success")
            msg.params["eventId"] = controlEvent.eventId

            if (isDiffTimeEvent(controlEvent)) {
                val state = AutoTestState(activity, view, controlEvent, msg)
                autoTestStateSet[controlEvent.eventId] = state

            } else {
                onResponseAutoTestAction(msg)
            }
        }

        override fun onControlEventProcessFailed(activity: Activity?, view: View?, controlEvent: ControlEvent, code: Int, message: String) {
            // 解决轻微滑动导致的回放滑动异常问题，当作正常响应继续流程
            if (controlEvent.eventType == EventType.WSE_COMMON_EVENT) {
                controlEvent.viewC12c?.let {
                    if (it.actionType == ActionType.ON_SCROLL) {
                        LogHelper.e("AutoTestManager","ON_SCROLL ERROR!")
                        onControlEventProcessSuccess(activity, view, controlEvent)
                        return
                    }
                }
            }

            val msg = AutoTestMessage(command = "action_response", message = "failed")
            msg.params["eventId"] = controlEvent.eventId
            msg.params["message"] = message
            msg.params["code"] = "" + code
            if (isDiffTimeEvent(controlEvent)) {
                diffEventTask?.let {
                    delayHandler.removeCallbacks(it)
                }
                onResponseAutoTestAction(msg)
            } else {
                onResponseAutoTestAction(msg)
            }
        }
    }

}
