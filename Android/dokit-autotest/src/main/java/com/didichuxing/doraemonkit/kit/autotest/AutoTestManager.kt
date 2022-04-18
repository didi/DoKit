package com.didichuxing.doraemonkit.kit.autotest

import android.app.Activity
import android.view.View
import android.view.accessibility.AccessibilityEvent
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
import com.didichuxing.doraemonkit.kit.test.event.*
import com.didichuxing.doraemonkit.kit.test.mock.MockManager
import com.didichuxing.doraemonkit.kit.test.mock.ProxyMockCallback
import com.didichuxing.doraemonkit.kit.test.report.ScreenShotManager
import com.didichuxing.doraemonkit.util.*
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

    private val delayHandler: DelayHandler = DelayHandler()

    private val eventActionInterceptor = object : OnControlEventInterceptor {
        override fun onControlEventAction(activity: Activity?, view: View?, controlEvent: ControlEvent): Boolean {
            if (view is DokitFrameLayout) {
                return true
            }
            return false
        }
    }

    private val eventActionListener = object : OnControlEventActionListener {
        override fun onControlEventAction(activity: Activity?, view: View?, event: ControlEvent) {
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

    private var screenShotManager: ScreenShotManager = ScreenShotManager("/doKit/autotest/screen")
    private var fileUploadManager: FileUploadManager = FileUploadManager(screenShotManager)

    private val autoTestStateSet: MutableMap<String, AutoTestState> = mutableMapOf()


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
            val msg = AutoTestMessage(command = "action_response", message = "failed")
            msg.params["eventId"] = controlEvent.eventId
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

    private var diffEventTask: EventTask? = null

    class EventTask(private val event: ControlEvent) : Runnable {
        override fun run() {
            val state = autoTestStateSet[event.eventId]
            state?.let {
                val message = state.message
                val activity = ActivityUtils.getTopActivity()
                val bitmap = screenShotManager.screenshotBitmap(activity)
                if (bitmap != null) {
                    val name = screenShotManager.createNextFileName()
                    message.params["imageName"] = name
                    fileUploadManager.uploadBitmap(bitmap, name)
                } else {
                    message.params["imageName"] = ""
                }
                onResponseAutoTestAction(message)
            } ?: run {
                val msg = AutoTestMessage(command = "action_response", message = "failed")
                msg.params["eventId"] = event.eventId
                onResponseAutoTestAction(msg)
            }
        }
    }

    fun getMode(): TestMode {
        return mode
    }

    fun startRecord() {
        ControlEventManager.addOnControlEventInterceptor(eventActionInterceptor)
        ControlEventManager.addOnControlEventActionListener(eventActionListener)
        MockManager.proxyMockCallback = proxyMockCallback
        DoKitTestManager.startTest(TestMode.HOST)
        changeToRecordView()

        ToastUtils.showShort("已开始录制")
    }


    fun stopRecord() {
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
        DoKitTestManager.startTest(TestMode.CLIENT)
        changeToTestView()
        ToastUtils.showShort("已开始测试")
    }

    fun stopAutoTest() {
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
        recordingCaseDoKitViewList.clear()
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

    private fun onReceiveControl(textPackage: TextPackage) {
        val text = textPackage.data
        val autoTestMessage = GsonUtils.fromJson<AutoTestMessage>(text, AutoTestMessage::class.java)

        when (autoTestMessage.command) {
            "startRecord" -> {
                startRecord()
                val msg = AutoTestMessage(command = "control_response", message = "success")
                onResponseAutoTestMessage(msg)
            }
            "stopRecord" -> {
                stopRecord()
                val msg = AutoTestMessage(command = "control_response", message = "success")
                onResponseAutoTestMessage(msg)
            }
            "startAutoTest" -> {
                startAutoTest()
                val msg = AutoTestMessage(command = "control_response", message = "success")
                onResponseAutoTestMessage(msg)
            }
            "stopAutoTest" -> {
                stopAutoTest()
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
     * 自动化测试行为事件响应
     */
    private fun onResponseAutoTestAction(autoTestMessage: AutoTestMessage) {
        webSocketClient?.let {
            it.send(JsonParser.toJson(PackageType.AUTOTEST, autoTestMessage, "action"))
        }
    }

    private fun isDiffTimeEvent(event: ControlEvent): Boolean {
        when (event.eventType) {
            EventType.WSE_CUSTOM_EVENT,
            EventType.APP_ON_FOREGROUND,
            EventType.APP_ON_BACKGROUND,
            EventType.ACTIVITY_BACK_PRESSED -> {
                return true
            }
            EventType.WSE_COMMON_EVENT -> {
                event.viewC12c?.let {
                    when (it.accEventType) {
                        //点击
                        AccessibilityEvent.TYPE_VIEW_CLICKED,
                            //长按
                        AccessibilityEvent.TYPE_VIEW_LONG_CLICKED,
                            //滚动
                        AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                            return true
                        }
                        else -> {

                        }
                    }
                    when (it.actionType) {
                        ActionType.ON_LONG_CLICK,
                        ActionType.ON_SCROLL,
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

    /**
     * 接收到自动化测试事件
     */
    private fun onReceiveAction(textPackage: TextPackage) {
        val text = textPackage.data
        val event = GsonUtils.fromJson<ControlEvent>(text, ControlEvent::class.java)

        if (isDiffTimeEvent(event)) {
            val diff: Long = if (event.diffTime < 1000) {
                1000
            } else {
                event.diffTime
            }
            val eventTask = EventTask(event)
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
                it.reConnect(connectAddress!!.url)
            }
        }
    }
}
