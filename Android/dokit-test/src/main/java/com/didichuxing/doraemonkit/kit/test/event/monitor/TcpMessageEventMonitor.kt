package com.didichuxing.doraemonkit.kit.test.event.monitor

import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.test.DoKitTestManager
import com.didichuxing.doraemonkit.kit.test.event.ControlEventManager
import com.didichuxing.doraemonkit.kit.test.event.EventType
import com.didichuxing.doraemonkit.kit.test.utils.RandomIdentityUtil
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.util.ActivityUtils

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
object TcpMessageEventMonitor {

    /**
     * 发送自定义事件
     * @return view
     * @return eventType 事件类型
     * @return param 自定义参数
     */
    fun onTcpMessageEvent(eventType: String, message: String = "") {
        if (DoKitTestManager.isHostMode()) {
            val actionId = RandomIdentityUtil.createAid()
            val wsEvent = ControlEvent(
                actionId,
                EventType.WSE_TCP_EVENT,
                mutableMapOf(
                    "activityName" to ActivityUtils.getTopActivity()::class.tagName,
                    "eventType" to eventType,
                    "message" to message
                ),
                null
            )
            ControlEventManager.onControlEventAction(null, null, wsEvent)
        }
    }

}
