package com.didichuxing.doraemonkit.kit.test.event.processor

import android.app.Activity
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.test.event.EventType
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.util.ActivityUtils
import com.didichuxing.doraemonkit.util.DoKitCommUtil

object LifecycleEventMonitor {


    fun onLifecycleEvent(wsEvent: ControlEvent) {
        when (wsEvent.eventType) {
            EventType.ACTIVITY_FINISH -> {
                onActivityFinish(wsEvent)
            }
            EventType.APP_ON_FOREGROUND -> {
                appOnForeground(wsEvent)
            }
            EventType.APP_ON_BACKGROUND -> {
                appOnBackground(wsEvent)
            }
            EventType.ACTIVITY_BACK_PRESSED -> {
                onBackPressed(wsEvent)
            }
        }

    }

    fun appOnForeground(wsEvent: ControlEvent) {
        val activityName = wsEvent.params?.get("activityName")
        activityName?.let {
            val clazz: Class<Activity> =
                Class.forName(it) as Class<Activity>
            DoKitCommUtil.changeAppOnForeground(clazz)
        }
    }

    fun appOnBackground(wsEvent: ControlEvent) {
        ActivityUtils.startHomeActivity()
    }

    fun onBackPressed(wsEvent: ControlEvent) {
        val topActivity = ActivityUtils.getTopActivity()
        if (wsEvent.params?.get("activityName") == topActivity::class.tagName) {
            topActivity.onBackPressed()
        }
    }

    fun onActivityFinish(wsEvent: ControlEvent) {
        val topActivity = ActivityUtils.getTopActivity()
        if (wsEvent.params?.get("activityName") == topActivity::class.tagName) {
            topActivity.finish()
        }
    }
}
