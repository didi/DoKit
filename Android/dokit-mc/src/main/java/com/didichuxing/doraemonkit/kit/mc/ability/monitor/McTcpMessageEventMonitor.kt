package com.didichuxing.doraemonkit.kit.mc.ability.monitor

import com.didichuxing.doraemonkit.constant.WSEType
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.mc.all.view_info.ViewC12c
import com.didichuxing.doraemonkit.kit.mc.ability.DoKitMcEventDispatcher
import com.didichuxing.doraemonkit.kit.mc.all.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.mock.proxy.IdentityUtils
import com.didichuxing.doraemonkit.kit.mc.net.WSEvent
import com.didichuxing.doraemonkit.util.ActivityUtils


object McTcpMessageEventMonitor {

    /**
     * 发送自定义事件
     * @return view
     * @return eventType 事件类型
     * @return param 自定义参数
     */
    fun onMessageEvent(eventType: String, message: String = "") {
        val viewC12c = createViewC12c(eventType, message)
        val actionId = IdentityUtils.createAid()
        val wsEvent = WSEvent(
            WSMode.HOST,
            WSEType.WSE_TCP_EVENT,
            mutableMapOf(
                "activityName" to ActivityUtils.getTopActivity()::class.tagName
            ),
            viewC12c,
            actionId
        )
        DoKitMcManager.updateActionId(actionId)
        DoKitMcEventDispatcher.send(wsEvent)
    }

    private fun createViewC12c(eventType: String, message: String): ViewC12c {
        var viewRootImplIndex: Int = -1
        return ViewC12c(
            customEventType = eventType,
            customParams = message,
            viewRootImplIndex = viewRootImplIndex,
            viewPaths = mutableListOf(),
            text = ""
        )
    }
}
