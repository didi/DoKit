package com.didichuxing.doraemonkit.kit.test.report

import com.didichuxing.doraemonkit.kit.test.event.EventType
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent

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

    val mWsEvents: MutableList<ControlEvent> by lazy {
        mutableListOf<ControlEvent>()
    }

    /**
     * 有效的事件类型
     */
    private val effectiveEventTypes = arrayOf(
        EventType.APP_ON_FOREGROUND,
        EventType.APP_ON_BACKGROUND,
        EventType.ACTIVITY_BACK_PRESSED,
        EventType.WSE_COMMON_EVENT
    )

    /**
     * 拦截所有事件
     */
    fun intercept(wsEvent: ControlEvent) {
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
    private fun needFilter(eventType: EventType): Boolean {
        if (effectiveEventTypes.contains(eventType)) {
            return false
        }

        return true
    }
}
