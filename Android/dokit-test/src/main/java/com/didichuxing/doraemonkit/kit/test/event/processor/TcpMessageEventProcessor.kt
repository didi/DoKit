package com.didichuxing.doraemonkit.kit.test.event.processor


import com.didichuxing.doraemonkit.kit.test.event.ControlEvent


object TcpMessageEventProcessor {


    fun onTcpMessageEvent(wsEvent: ControlEvent) {
        val type: String? = wsEvent.viewC12c?.customEventType
        val message: String? = wsEvent.viewC12c?.customParams
//        DoKitMcManager.onTcpMessageEvent(type ?: "", message ?: "")
    }

}
