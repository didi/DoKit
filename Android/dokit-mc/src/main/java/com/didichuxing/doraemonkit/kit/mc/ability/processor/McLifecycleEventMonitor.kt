package com.didichuxing.doraemonkit.kit.mc.ability.processor

import android.app.Activity
import com.didichuxing.doraemonkit.constant.WSEType
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.mc.net.WSEvent
import com.didichuxing.doraemonkit.util.ActivityUtils
import com.didichuxing.doraemonkit.util.DoKitCommUtil

object McLifecycleEventMonitor {


    fun onLifecycleEvent(wsEvent: WSEvent) {
        when (wsEvent.eventType) {
            WSEType.ACTIVITY_FINISH -> {
                onActivityFinish(wsEvent)
            }
            WSEType.APP_ON_FOREGROUND -> {
                appOnForeground(wsEvent)
            }
            WSEType.APP_ON_BACKGROUND -> {
                appOnBackground(wsEvent)
            }
            WSEType.ACTIVITY_BACK_PRESSED -> {
                onBackPressed(wsEvent)
            }
        }

    }

    fun appOnForeground(wsEvent: WSEvent) {
        val activityName = wsEvent.commParams?.get("activityName")
        activityName?.let {
            val clazz: Class<Activity> =
                Class.forName(it) as Class<Activity>
            DoKitCommUtil.changeAppOnForeground(clazz)
        }
    }

    fun appOnBackground(wsEvent: WSEvent) {
        ActivityUtils.startHomeActivity()
    }

    fun onBackPressed(wsEvent: WSEvent) {
        val topActivity = ActivityUtils.getTopActivity()
        if (wsEvent.commParams?.get("activityName") == topActivity::class.tagName) {
            topActivity.onBackPressed()
        }
    }

    fun onActivityFinish(wsEvent: WSEvent) {
        val topActivity = ActivityUtils.getTopActivity()
        if (wsEvent.commParams?.get("activityName") == topActivity::class.tagName) {
            topActivity.finish()
        }
    }
}
