package com.didichuxing.doraemonkit.kit.test.event.processor

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.test.event.*
import com.didichuxing.doraemonkit.kit.test.util.XposedHookUtils
import com.didichuxing.doraemonkit.kit.test.util.ViewPathUtil
import com.didichuxing.doraemonkit.kit.test.util.WindowPathUtil
import com.didichuxing.doraemonkit.util.ActivityUtils
import com.didichuxing.doraemonkit.util.ReflectUtils


/**
 * didi Create on 2022/4/13 .
 *
 * Copyright (c) 2022/4/13 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/13 6:53 下午
 * @Description 用一句话说明文件功能
 */

abstract class AbstractEventProcessor {

    fun onControlEventAction(controlEvent: ControlEvent) {
        if (!checkActivityNow(controlEvent)) {
            onControlEventProcessFailed(
                controlEvent = controlEvent,
                code = EventErrorCode.ACTIVITY_NOT_MATCH,
                message = "activity 不匹配"
            )
        }
        if (controlEvent.viewC12c == null) {
            onControlEventProcessFailed(
                controlEvent = controlEvent,
                code = EventErrorCode.EVENT_ACTION_LOSE,
                message = "event 信息缺失"
            )
        }
        controlEvent.viewC12c?.let {
            val viewC12c = it
            val viewRoot: ViewParent? = findWindowRootView(it)
            if (viewRoot == null) {
                onControlEventProcessFailed(
                    controlEvent = controlEvent,
                    code = EventErrorCode.WINDOW_NOT_FIND,
                    message = "window 找不到"
                )
            } else {

                val decorView: ViewGroup = ReflectUtils.reflect(viewRoot).field("mView").get<View>() as ViewGroup
                val targetView: View? = ViewPathUtil.findViewByViewParentInfo(decorView, viewC12c.viewPaths)
                val activity = ViewPathUtil.getActivity(targetView)
                if (targetView == null) {
                    onControlEventProcessFailed(
                        activity = activity,
                        controlEvent = controlEvent,
                        code = EventErrorCode.VIEW_NOT_FIND,
                        message = "view 找不到"
                    )
                } else {
                    onSimulationEventAction(activity, targetView, viewC12c, controlEvent)
                }
            }

        }

    }

    abstract fun onSimulationEventAction(activity: Activity, view: View, viewC12c: ViewC12c, controlEvent: ControlEvent)

    /**
     * 从机执行 ControlEvent 成功
     */
    protected fun onControlEventProcessSuccess(activity: Activity? = null, view: View? = null, controlEvent: ControlEvent) {
        ControlEventManager.onControlEventProcessSuccess(activity, view, controlEvent)
    }

    /**
     * 从机执行 ControlEvent 失败
     */
    protected fun onControlEventProcessFailed(activity: Activity? = null, view: View? = null, controlEvent: ControlEvent, code: Int, message: String) {
        ControlEventManager.onControlEventProcessFailed(activity, view, controlEvent, code, message)
    }


    private fun checkActivityNow(controlEvent: ControlEvent): Boolean {
        controlEvent.params?.let {
            if (it["activityName"] != ActivityUtils.getTopActivity()::class.tagName) {
                return false
            }
        }
        return true
    }


    private fun findWindowRootView(viewC12c: ViewC12c): ViewParent? {
        if (XposedHookUtils.ROOT_VIEWS == null || viewC12c.windowIndex == -1) {
            return null
        }
        val viewParent: ViewParent? = WindowPathUtil.findViewRoot(XposedHookUtils.ROOT_VIEWS, viewC12c.windowIndex)

        return viewParent
    }

}
