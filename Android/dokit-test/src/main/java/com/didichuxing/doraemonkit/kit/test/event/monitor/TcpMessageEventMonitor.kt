package com.didichuxing.doraemonkit.kit.test.event.monitor

import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.test.event.ActionEventManager
import com.didichuxing.doraemonkit.kit.test.event.ViewC12c
import com.didichuxing.doraemonkit.kit.test.event.DoKitMcEventDispatcher
import com.didichuxing.doraemonkit.kit.test.event.EventType
import com.didichuxing.doraemonkit.kit.test.util.RandomIdentityUtils
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.kit.test.util.DateTime
import com.didichuxing.doraemonkit.util.ActivityUtils


object TcpMessageEventMonitor {

    /**
     * 发送自定义事件
     * @return view
     * @return eventType 事件类型
     * @return param 自定义参数
     */
    fun onMessageEvent(eventType: String, message: String = "") {
        val viewC12c = createViewC12c(eventType, message)
        val actionId = RandomIdentityUtils.createAid()
        val wsEvent = ControlEvent(
            actionId,
            EventType.WSE_TCP_EVENT,
            mutableMapOf(
                "activityName" to ActivityUtils.getTopActivity()::class.tagName,
                "eventType" to eventType,
                "message" to message
            ),
            viewC12c,
            DateTime.nowTime()
        )
        ActionEventManager.updateActionId(actionId)
        DoKitMcEventDispatcher.send(wsEvent)
    }

    private fun createViewC12c(eventType: String, message: String): ViewC12c {
        var viewRootImplIndex: Int = -1
        return ViewC12c(
            windowIndex = viewRootImplIndex,
            viewPaths = mutableListOf(),
            text = ""
        )
    }
}
