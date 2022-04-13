package com.didichuxing.doraemonkit.kit.test.event.processor


import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.kit.test.mock.tcp.TcpMockManager


object TcpMessageEventProcessor {


    fun onTcpMessageEvent(wsEvent: ControlEvent) {
        val type: String? = wsEvent.params?.get("eventType")
        val message: String? = wsEvent.params?.get("message")
        TcpMockManager.onTcpMessageEvent(type ?: "", message ?: "")
    }

}
