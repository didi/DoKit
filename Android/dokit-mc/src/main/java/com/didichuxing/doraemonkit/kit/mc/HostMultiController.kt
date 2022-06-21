package com.didichuxing.doraemonkit.kit.mc

import android.app.Activity
import android.view.View
import com.didichuxing.doraemonkit.kit.connect.data.PackageType
import com.didichuxing.doraemonkit.kit.connect.parser.JsonParser
import com.didichuxing.doraemonkit.kit.connect.ws.WebSocketClient
import com.didichuxing.doraemonkit.kit.test.DoKitTestManager
import com.didichuxing.doraemonkit.kit.test.TestMode
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.kit.test.event.ControlEventManager
import com.didichuxing.doraemonkit.kit.test.event.OnControlEventActionListener
import com.didichuxing.doraemonkit.kit.test.event.OnControlEventInterceptor
import com.didichuxing.doraemonkit.kit.test.mock.MockManager
import com.didichuxing.doraemonkit.mc.R


/**
 * didi Create on 2022/4/22 .
 *
 * Copyright (c) 2022/4/22 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/22 12:25 下午
 * @Description 主机控制实现
 */

class HostMultiController(webSocketClient: WebSocketClient) : AbstractMultiController(webSocketClient) {


    private val onControlEventInterceptor = object : OnControlEventInterceptor {
        override fun onControlEventAction(activity: Activity?, view: View?, controlEvent: ControlEvent): Boolean {
            if (view != null && view.id == R.id.dokit_mode_switch_btn) {
                return true
            }
            return false
        }
    }
    private val onControlEventActionListener = object : OnControlEventActionListener {
        override fun onControlEventAction(activity: Activity?, view: View?, event: ControlEvent) {
            screenShotForEvent(event)
            webSocketClient.let {
                it.send(JsonParser.toJson(PackageType.BROADCAST, event, "action"))
            }
        }
    }

    override fun start() {
        ControlEventManager.addOnControlEventInterceptor(onControlEventInterceptor)
        ControlEventManager.addOnControlEventActionListener(onControlEventActionListener)
        DoKitTestManager.startTest(TestMode.HOST)
        MockManager.startTest(TestMode.HOST)
    }


    override fun close() {
        ControlEventManager.removeOnControlEventInterceptor(onControlEventInterceptor)
        ControlEventManager.removeOnControlEventActionListener(onControlEventActionListener)
        DoKitTestManager.closeTest()
        MockManager.closeTest()
    }

}
