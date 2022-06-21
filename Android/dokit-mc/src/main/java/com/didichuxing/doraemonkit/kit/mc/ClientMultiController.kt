package com.didichuxing.doraemonkit.kit.mc

import android.app.Activity
import android.view.View
import com.didichuxing.doraemonkit.kit.connect.data.TextPackage
import com.didichuxing.doraemonkit.kit.connect.ws.WebSocketClient
import com.didichuxing.doraemonkit.kit.test.DoKitTestManager
import com.didichuxing.doraemonkit.kit.test.TestMode
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.kit.test.event.ControlEventManager
import com.didichuxing.doraemonkit.kit.test.event.OnControlEventActionProcessListener
import com.didichuxing.doraemonkit.kit.test.mock.MockManager
import com.didichuxing.doraemonkit.kit.test.report.AutoTestMessage
import com.didichuxing.doraemonkit.kit.test.report.AutoTestState
import com.didichuxing.doraemonkit.util.GsonUtils
import kotlinx.coroutines.launch


/**
 * didi Create on 2022/4/22 .
 *
 * Copyright (c) 2022/4/22 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/22 12:25 下午
 * @Description 用一句话说明文件功能
 */

class ClientMultiController(webSocketClient: WebSocketClient) : AbstractMultiController(webSocketClient) {


    private val onControlEventActionProcessListener = object : OnControlEventActionProcessListener {
        override fun onControlEventProcessSuccess(activity: Activity?, view: View?, controlEvent: ControlEvent) {
            val message = AutoTestMessage(command = "action_response", message = "success")
            message.params["eventId"] = controlEvent.eventId
            val state = AutoTestState(activity, view, controlEvent, message)
            onControlEventProcessState(state)
        }

        override fun onControlEventProcessFailed(activity: Activity?, view: View?, controlEvent: ControlEvent, code: Int, message: String) {
            val msg = AutoTestMessage(command = "action_response", message = "failed")
            msg.params["eventId"] = controlEvent.eventId
            msg.params["message"] = message
            msg.params["code"] = "" + code
            val state = AutoTestState(activity, view, controlEvent, msg)
            onControlEventProcessState(state)
        }
    }

    override fun start() {
        ControlEventManager.addOnControlEventActionProcessListener(onControlEventActionProcessListener)
        DoKitTestManager.startTest(TestMode.CLIENT)
        MockManager.startTest(TestMode.CLIENT)
    }

    override fun close() {
        DoKitTestManager.closeTest()
        MockManager.closeTest()
        ControlEventManager.removeOnControlEventActionProcessListener(onControlEventActionProcessListener)
    }

    /**
     * 接收到自动化测试事件
     */
    fun onReceiveAction(textPackage: TextPackage) {
        val text = textPackage.data
        val controlEvent = GsonUtils.fromJson<ControlEvent>(text, ControlEvent::class.java)

        mainScope.launch {
            screenShotForEvent(controlEvent)
            ControlEventManager.onReceiveControlEventAction(controlEvent)
        }

    }
}
