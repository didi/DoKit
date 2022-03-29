package com.didichuxing.doraemonkit.kit.mc.ability

import com.didichuxing.doraemonkit.constant.WSEType
import com.didichuxing.doraemonkit.kit.mc.ability.processor.McAccessibilityEventProcessor
import com.didichuxing.doraemonkit.kit.mc.ability.processor.McCustomEventProcessor
import com.didichuxing.doraemonkit.kit.mc.ability.processor.McLifecycleEventMonitor
import com.didichuxing.doraemonkit.kit.mc.ability.processor.McTcpMessageEventProcessor
import com.didichuxing.doraemonkit.kit.mc.all.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.net.WSEvent
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
    fun process(wsEvent: WSEvent) {
        LogHelper.i(TAG, "process() wsEvent===>$wsEvent")
        DoKitMcManager.updateActionId(wsEvent.actionId)

        when (wsEvent.eventType) {
            /**
             * 通用事件处理
             */
            WSEType.WSE_COMM_EVENT -> {
                McAccessibilityEventProcessor.onAccessibilityEvent(wsEvent)
            }
            /**
             * 自定义事件类型
             */
            WSEType.WSE_CUSTOM_EVENT -> {
                McCustomEventProcessor.onCustomEvent(wsEvent)
            }

            WSEType.WSE_TCP_EVENT -> {
                McTcpMessageEventProcessor.onTcpMessageEvent(wsEvent)
            }

            WSEType.ACTIVITY_FINISH,
                /**
                 * 模拟返回事件
                 */
            WSEType.ACTIVITY_BACK_PRESSED,
                /**
                 * 切换到后台
                 */
            WSEType.APP_ON_BACKGROUND,
                /**
                 * 切换到前台
                 */
            WSEType.APP_ON_FOREGROUND -> {
                McLifecycleEventMonitor.onLifecycleEvent(wsEvent)
            }

            else -> {
                LogHelper.e(TAG, "处理事件类型 wsEvent=$wsEvent")

            }
        }
    }


}


