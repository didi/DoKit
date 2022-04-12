package com.didichuxing.doraemonkit.kit.mc.net

import com.didichuxing.doraemonkit.kit.test.event.EventType
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.util.LogHelper

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/18-17:32
 * 描    述：
 * 修订历史：
 * ================================================
 */
object WSServerProcessor {

    const val TAG = "WSServerProcessor"

    /**
     * 处理来自从机的消息
     */
    fun process(wsEvent: ControlEvent) {
        when (wsEvent.eventType) {
            EventType.WSE_TEST -> {
                LogHelper.e(TAG, "处理事件类型 wsEvent=$wsEvent")
            }

            EventType.WSE_CLOSE -> {

            }

        }
    }


}
