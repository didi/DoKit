package com.didichuxing.doraemonkit.kit.mc.ability.processor


import com.didichuxing.doraemonkit.kit.mc.all.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.net.WSEvent


object McTcpMessageEventProcessor {


    fun onTcpMessageEvent(wsEvent: WSEvent) {
        val type: String? = wsEvent.viewC12c?.customEventType
        val message: String? = wsEvent.viewC12c?.customParams
        DoKitMcManager.onTcpMessageEvent(type ?: "", message ?: "")
    }

}
