package com.didichuxing.doraemonkit.kit.test.event

import com.didichuxing.doraemonkit.kit.test.event.processor.AccessibilityEventProcessor
import com.didichuxing.doraemonkit.kit.test.event.processor.CustomEventProcessor
import com.didichuxing.doraemonkit.kit.test.event.processor.LifecycleEventMonitor
import com.didichuxing.doraemonkit.kit.test.event.processor.TcpMessageEventProcessor
import com.didichuxing.doraemonkit.util.*


/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/18-17:32
 * 描    述：
 * 修订历史：
 * ================================================
 */
object WSClientProcessor {

    const val TAG = "WSClientProcessor"

    /**
     * 处理来自主机的消息
     */
    fun process(wsEvent: ControlEvent) {
        LogHelper.i(TAG, "process() wsEvent===>$wsEvent")
        ActionEventManager.updateActionId(wsEvent.eventId)

        when (wsEvent.eventType) {
            /**
             * 通用事件处理
             */
            EventType.WSE_COMM_EVENT -> {
                AccessibilityEventProcessor.onAccessibilityEvent(wsEvent)
            }
            /**
             * 自定义事件类型
             */
            EventType.WSE_CUSTOM_EVENT -> {
                CustomEventProcessor.onCustomEvent(wsEvent)
            }

            EventType.WSE_TCP_EVENT -> {
                TcpMessageEventProcessor.onTcpMessageEvent(wsEvent)
            }

            EventType.ACTIVITY_FINISH,
                /**
                 * 模拟返回事件
                 */
            EventType.ACTIVITY_BACK_PRESSED,
                /**
                 * 切换到后台
                 */
            EventType.APP_ON_BACKGROUND,
                /**
                 * 切换到前台
                 */
            EventType.APP_ON_FOREGROUND -> {
                LifecycleEventMonitor.onLifecycleEvent(wsEvent)
            }

            else -> {
                LogHelper.e(TAG, "处理事件类型 wsEvent=$wsEvent")
            }
        }
    }


}


