package com.didichuxing.doraemonkit.kit.test.event.processor

import android.app.Activity
import android.view.View
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.kit.test.event.ViewC12c
import com.didichuxing.doraemonkit.util.ActivityUtils
import com.didichuxing.doraemonkit.util.DoKitCommUtil

/**
 * didi Create on 2022/4/13 .
 *
 * Copyright (c) 2022/4/13 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/13 3:07 下午
 * @Description 用一句话说明文件功能
 */
class LifecycleEventProcessor : AbstractEventProcessor() {

    override fun onSimulationEventAction(activity: Activity, view: View?, viewC12c: ViewC12c, controlEvent: ControlEvent) {

    }

    fun appOnForeground(controlEvent: ControlEvent) {
        val activityName = controlEvent.params?.get("activityName")
        activityName?.let {
            val clazz: Class<Activity> =
                Class.forName(it) as Class<Activity>
            DoKitCommUtil.changeAppOnForeground(clazz)
        }

        onControlEventProcessSuccess(controlEvent = controlEvent)
    }

    fun appOnBackground(controlEvent: ControlEvent) {
        ActivityUtils.startHomeActivity()
        onControlEventProcessSuccess(controlEvent = controlEvent)
    }

    fun onBackPressed(controlEvent: ControlEvent) {
        val topActivity = ActivityUtils.getTopActivity()
        if (controlEvent.params?.get("activityName") == topActivity::class.tagName) {
            topActivity.onBackPressed()
        }
        onControlEventProcessSuccess(controlEvent = controlEvent)
    }

    fun onActivityFinish(controlEvent: ControlEvent) {
        val topActivity = ActivityUtils.getTopActivity()
        if (controlEvent.params?.get("activityName") == topActivity::class.tagName) {
            topActivity.finish()
        }
        onControlEventProcessSuccess(controlEvent = controlEvent)
    }
}
