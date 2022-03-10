package com.didichuxing.doraemonkit.kit.mc.ability.monitor

import android.app.Activity
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.constant.WSEType
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.core.DokitLifecycleInterface
import com.didichuxing.doraemonkit.kit.mc.ability.DoKitMcEventDispatcher
import com.didichuxing.doraemonkit.kit.mc.all.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.mock.proxy.IdentityUtils
import com.didichuxing.doraemonkit.kit.mc.net.WSEvent

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
class McLifecycleMonitor : DokitLifecycleInterface {
    companion object {
        const val TAG = "McActivityLifecycleHandler"
    }


    override fun onBackPressed(activity: Activity) {
        if (DoKitManager.WS_MODE == WSMode.HOST) {
            val actionId = IdentityUtils.createAid()
            val wsEvent = WSEvent(
                WSMode.HOST,
                WSEType.ACTIVITY_BACK_PRESSED,
                mutableMapOf(
                    "activityName" to activity::class.tagName,
                    "command" to "onBackPressed"
                ),
                null, actionId
            )
            DoKitMcManager.updateActionId(actionId)
            DoKitMcEventDispatcher.send(wsEvent)
        }
    }


    override fun onForeground(className: String) {
        if (DoKitManager.WS_MODE == WSMode.HOST) {
            val actionId = IdentityUtils.createAid()
            val wsEvent = WSEvent(
                WSMode.HOST,
                WSEType.APP_ON_FOREGROUND,
                mutableMapOf(
                    "command" to "onForeground",
                    "activityName" to className
                ),
                null, actionId
            )
            DoKitMcManager.updateActionId(actionId)
            DoKitMcEventDispatcher.send(wsEvent)
        }
    }

    override fun onBackground() {
        if (DoKitManager.WS_MODE == WSMode.HOST) {
            val actionId = IdentityUtils.createAid()
            val wsEvent = WSEvent(
                WSMode.HOST,
                WSEType.APP_ON_BACKGROUND,
                mutableMapOf(
                    "command" to "onBackground"
                ),
                null, actionId
            )
            DoKitMcManager.updateActionId(actionId)
            DoKitMcEventDispatcher.send(wsEvent)
        }
    }


}
