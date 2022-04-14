package com.didichuxing.doraemonkit.kit.test.event

import com.didichuxing.doraemonkit.kit.test.event.processor.AccessibilityEventProcessor
import com.didichuxing.doraemonkit.kit.test.event.processor.CustomEventProcessor
import com.didichuxing.doraemonkit.kit.test.event.processor.LifecycleEventProcessor
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
class ControlEventProcessor {

    companion object {
        const val TAG = "ControlEventProcessor"
    }

    private val accessibilityEventProcessor: AccessibilityEventProcessor = AccessibilityEventProcessor()
    private val customEventProcessor: CustomEventProcessor = CustomEventProcessor()
    private val LifecycleEventProcessor: LifecycleEventProcessor = LifecycleEventProcessor()
    private val TcpMessageEventProcessor: TcpMessageEventProcessor = TcpMessageEventProcessor()


    /**
     * 处理来自主机的消息
     */
    fun processControlEvent(controlEvent: ControlEvent) {
        LogHelper.i(TAG, "process() event=$controlEvent")
        ControlEventManager.updateEventId(controlEvent.eventId)

        when (controlEvent.eventType) {
            /**
             * 通用事件处理
             */
            EventType.WSE_COMMON_EVENT -> {
                accessibilityEventProcessor.onControlEventAction(controlEvent)
            }
            /**
             * 自定义事件类型
             */
            EventType.WSE_CUSTOM_EVENT -> {
                customEventProcessor.onControlEventAction(controlEvent)
            }

            EventType.WSE_TCP_EVENT -> {
                TcpMessageEventProcessor.onTcpMessageEvent(controlEvent)
            }

            EventType.ACTIVITY_FINISH -> {
                LifecycleEventProcessor.onActivityFinish(controlEvent)
            }
            /**
             * 模拟返回事件
             */
            EventType.ACTIVITY_BACK_PRESSED -> {
                LifecycleEventProcessor.onBackPressed(controlEvent)
            }
            /**
             * 切换到后台
             */
            EventType.APP_ON_BACKGROUND -> {
                LifecycleEventProcessor.appOnBackground(controlEvent)
            }
            /**
             * 切换到前台
             */
            EventType.APP_ON_FOREGROUND -> {
                LifecycleEventProcessor.appOnForeground(controlEvent)
            }

            else -> {
                LogHelper.e(TAG, "处理事件类型 wsEvent=$controlEvent")
            }
        }
    }


}


