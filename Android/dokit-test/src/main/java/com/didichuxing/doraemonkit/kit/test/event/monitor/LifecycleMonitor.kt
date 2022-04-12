package com.didichuxing.doraemonkit.kit.test.event.monitor

import android.app.Activity
import com.didichuxing.doraemonkit.kit.test.TestMode
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.core.DokitLifecycleInterface
import com.didichuxing.doraemonkit.kit.test.DoKitTestManager
import com.didichuxing.doraemonkit.kit.test.event.ActionEventManager
import com.didichuxing.doraemonkit.kit.test.event.DoKitMcEventDispatcher
import com.didichuxing.doraemonkit.kit.test.event.EventType
import com.didichuxing.doraemonkit.kit.test.mock.proxy.IdentityUtils
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.kit.test.util.DateTime

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
class LifecycleMonitor : DokitLifecycleInterface {
    companion object {
        const val TAG = "McActivityLifecycleHandler"
    }


    override fun onBackPressed(activity: Activity) {
        if (DoKitTestManager.WS_MODE == TestMode.HOST) {
            val actionId = IdentityUtils.createAid()
            val wsEvent = ControlEvent(
                actionId,
                EventType.ACTIVITY_BACK_PRESSED,
                mutableMapOf(
                    "activityName" to activity::class.tagName,
                    "command" to "onBackPressed"
                ),
                null,
                DateTime.nowTime()
            )
            ActionEventManager.updateActionId(actionId)
            DoKitMcEventDispatcher.send(wsEvent)
        }
    }


    override fun onForeground(className: String) {
        if (DoKitTestManager.WS_MODE == TestMode.HOST) {
            val actionId = IdentityUtils.createAid()
            val wsEvent = ControlEvent(
                actionId,
                EventType.APP_ON_FOREGROUND,
                mutableMapOf(
                    "command" to "onForeground",
                    "activityName" to className
                ),
                null,
                DateTime.nowTime()
            )
            ActionEventManager.updateActionId(actionId)
            DoKitMcEventDispatcher.send(wsEvent)
        }
    }

    override fun onBackground() {
        if (DoKitTestManager.WS_MODE == TestMode.HOST) {
            val actionId = IdentityUtils.createAid()
            val wsEvent = ControlEvent(
                actionId,
                EventType.APP_ON_BACKGROUND,
                mutableMapOf(
                    "command" to "onBackground"
                ),
                null,
                DateTime.nowTime()
            )
            ActionEventManager.updateActionId(actionId)
            DoKitMcEventDispatcher.send(wsEvent)
        }
    }


}
