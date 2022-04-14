package com.didichuxing.doraemonkit.kit.test.event.monitor

import android.app.Activity
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.core.DoKitLifecycleInterface
import com.didichuxing.doraemonkit.kit.test.DoKitTestManager
import com.didichuxing.doraemonkit.kit.test.event.ControlEventManager
import com.didichuxing.doraemonkit.kit.test.event.EventType
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/9-17:40
 * 描    述：
 * 修订历史：
 * ================================================
 * 监听页面生命周期并处理
 */
class LifecycleEventMonitor : DoKitLifecycleInterface {
    companion object {
        const val TAG = "LifecycleEventMonitor"
    }


    override fun onBackPressed(activity: Activity) {
        onLifecycleEvent(activity, EventType.ACTIVITY_BACK_PRESSED)
    }

    override fun onForeground(activity: Activity) {
        onLifecycleEvent(activity, EventType.APP_ON_FOREGROUND)
    }

    override fun onBackground(activity: Activity) {
        onLifecycleEvent(activity, EventType.APP_ON_BACKGROUND)
    }

    private fun onLifecycleEvent(activity: Activity, eventType: EventType) {
        if (DoKitTestManager.isHostMode()) {
            val actionId = ControlEventManager.createNextEventId()
            val controlEvent = ControlEvent(
                actionId,
                eventType,
                mutableMapOf(
                    "command" to "onBackground",
                    "activityName" to activity::class.tagName
                ),
                null
            )
            ControlEventManager.onControlEventAction(activity, null, controlEvent)
        }
    }


}
