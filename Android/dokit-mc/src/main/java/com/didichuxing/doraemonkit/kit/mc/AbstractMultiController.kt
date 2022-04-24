package com.didichuxing.doraemonkit.kit.mc

import android.graphics.Bitmap
import android.view.accessibility.AccessibilityEvent
import com.didichuxing.doraemonkit.kit.connect.data.PackageType
import com.didichuxing.doraemonkit.kit.connect.parser.ByteParser
import com.didichuxing.doraemonkit.kit.connect.parser.JsonParser
import com.didichuxing.doraemonkit.kit.connect.ws.*
import com.didichuxing.doraemonkit.kit.test.event.*
import com.didichuxing.doraemonkit.kit.test.report.AutoTestMessage
import com.didichuxing.doraemonkit.kit.test.report.AutoTestState
import com.didichuxing.doraemonkit.kit.test.report.ScreenShotManager
import com.didichuxing.doraemonkit.util.ActivityUtils
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.lang.Runnable


/**
 * didi Create on 2022/4/22 .
 *
 * Copyright (c) 2022/4/22 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/22 12:26 下午
 * @Description 用一句话说明文件功能
 */

abstract class AbstractMultiController(private val webSocketClient: WebSocketClient) {

    protected val mainScope = MainScope() + CoroutineName(this.toString())

    private val uploadScope = CoroutineScope(SupervisorJob() + Dispatchers.IO) + CoroutineName("mc-upload")

    private val autoTestStateSet: MutableMap<String, AutoTestState> = mutableMapOf()

    private val screenShotManager: ScreenShotManager = ScreenShotManager("doKit/autotest/screen")

    private val delayHandler: DelayHandler = DelayHandler()

    private var screenShotEventTask: EventTask? = null

    private var lastControlEvent: ControlEvent? = null

    fun screenShotForEvent(controlEvent: ControlEvent) {
        lastControlEvent?.let {
            screenShotNow(it)
        }

        if (isDiffTimeEvent(controlEvent)) {
            lastControlEvent = controlEvent
            val task = EventTask(controlEvent)
            screenShotEventTask = task
            delayHandler.postDelayed(task, 1000)
        }
    }

    /**
     * 从机事件处理结果
     */
    fun onControlEventProcessState(autoTestState: AutoTestState) {
        lastControlEvent?.let {
            if (it.eventId == autoTestState.controlEvent.eventId) {
                autoTestStateSet[autoTestState.controlEvent.eventId] = autoTestState
            }
        }
    }

    private fun buildAutoTestMessage(controlEvent: ControlEvent): AutoTestMessage {
        val state: AutoTestState? = autoTestStateSet[controlEvent.eventId]
        state?.let {
            autoTestStateSet.remove(controlEvent.eventId)
            return state.message
        }
        val message = AutoTestMessage(command = "action_response", message = "success")
        message.params["eventId"] = controlEvent.eventId
        return message
    }

    private fun screenShotNow(controlEvent: ControlEvent) {
        val message = buildAutoTestMessage(controlEvent)
        val activity = ActivityUtils.getTopActivity()
        val bitmap = screenShotManager.screenshotBitmap(activity)
        if (bitmap != null) {
            val name = screenShotManager.createNextFileName()
            message.params["imageName"] = name
            message.params["type"] = "jpeg"
            onResponseAutoTestAction(message, bitmap)
        } else {
            message.params["errorMessage"] = "screenShot error."
            message.params["imageName"] = ""
            message.params["type"] = ""
            onResponseAutoTestAction(message)
        }
    }

    private fun isDiffTimeEvent(controlEvent: ControlEvent): Boolean {
        when (controlEvent.eventType) {
            EventType.WSE_CUSTOM_EVENT,
            EventType.APP_ON_FOREGROUND,
            EventType.APP_ON_BACKGROUND,
            EventType.ACTIVITY_BACK_PRESSED -> {
                return true
            }
            EventType.WSE_COMMON_EVENT -> {
                controlEvent.viewC12c?.let {
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
//        uploadScope.launch {
//            val stream = ByteArrayOutputStream(2048)
//            val ok = bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
//            val bytes = stream.toByteArray()
//
//            val textPackage = JsonParser.toTextPackage(PackageType.AUTOTEST, autoTestMessage, "action")
//            val byteString = ByteParser.toByteString(textPackage, bytes)
//            webSocketClient.send(byteString)
//        }
    }

    inner class EventTask(private val controlEvent: ControlEvent) : Runnable {

        private fun isCurrentEvent(): Boolean {
            lastControlEvent?.let {
                return it.eventId == controlEvent.eventId
            }
            return false
        }

        override fun run() {
            if (isCurrentEvent()) {
                lastControlEvent = null
                screenShotNow(controlEvent)
            }
        }
    }

    abstract fun start()


    abstract fun close()


}
