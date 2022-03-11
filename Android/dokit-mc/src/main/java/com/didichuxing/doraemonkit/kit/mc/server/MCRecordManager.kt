package com.didichuxing.doraemonkit.kit.mc.server

import com.didichuxing.doraemonkit.constant.WSEType
import com.didichuxing.doraemonkit.kit.mc.all.WSEvent

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/10/1-15:51
 * 描    述：主机行为录制
 * 修订历史：
 * ================================================
 */
object MCRecordManager {
    /**
     * 是否处于录制状态
     */
    var isEventRecoding = false

    val mWsEvents: MutableList<WSEvent> by lazy {
        mutableListOf<WSEvent>()
    }

    /**
     * 有效的事件类型
     */
    private val effectiveEventTypes = arrayOf(
        WSEType.APP_ON_FOREGROUND,
        WSEType.APP_ON_BACKGROUND,
        WSEType.ACTIVITY_BACK_PRESSED,
        WSEType.WSE_COMM_EVENT
    )

    /**
     * 拦截所有事件
     */
    fun intercept(wsEvent: WSEvent) {
        //拦截无效的事件类型
        if (needFilter(wsEvent.eventType)) {
            return
        }

        //保存有效的事件类型
        mWsEvents.add(wsEvent)
    }

    /**
     * 是否过滤特定的事件类型
     */
    private fun needFilter(eventType: WSEType): Boolean {
        if (effectiveEventTypes.contains(eventType)) {
            return false
        }

        return true
    }
}
