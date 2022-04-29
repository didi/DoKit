package com.didichuxing.doraemonkit.kit.test.event.processor


import android.app.Activity
import android.view.View
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.kit.test.event.ViewC12c
import com.didichuxing.doraemonkit.kit.test.mock.tcp.TcpMockManager

/**
 * didi Create on 2022/4/13 .
 *
 * Copyright (c) 2022/4/13 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/13 3:07 下午
 * @Description 用一句话说明文件功能
 */
class TcpMessageEventProcessor : AbstractEventProcessor() {

    override fun onSimulationEventAction(activity: Activity, view: View?, viewC12c: ViewC12c, controlEvent: ControlEvent) {

    }

    fun onTcpMessageEvent(controlEvent: ControlEvent) {
        val type: String? = controlEvent.params?.get("eventType")
        val message: String? = controlEvent.params?.get("message")
        TcpMockManager.onTcpMessageEvent(type ?: "", message ?: "")
        onControlEventProcessSuccess(controlEvent = controlEvent)
    }

}
