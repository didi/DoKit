package com.didichuxing.doraemonkit.kit.test.event.processor

import android.app.Activity
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.test.event.*
import com.didichuxing.doraemonkit.kit.test.utils.XposedHookUtil
import com.didichuxing.doraemonkit.kit.test.utils.ViewPathUtil
import com.didichuxing.doraemonkit.kit.test.utils.WindowPathUtil
import com.didichuxing.doraemonkit.util.ActivityUtils
import com.didichuxing.doraemonkit.util.LogHelper
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
                val decorView: ViewGroup = ActivityUtils.getTopActivity().window.decorView as ViewGroup
                onControlEventAction(controlEvent, viewC12c, decorView)
            } else {
                val decorView: ViewGroup = ReflectUtils.reflect(viewRoot).field("mView").get<View>() as ViewGroup
                onControlEventAction(controlEvent, viewC12c, decorView)
            }
        }

    }

    private fun onControlEventAction(controlEvent: ControlEvent, viewC12c: ViewC12c, decorView: ViewGroup) {
        val targetView: View? = ViewPathUtil.findViewByViewParentInfo(decorView, viewC12c.viewPaths)
        val activity = ViewPathUtil.getActivity(targetView)
        if (targetView == null && forceViewCheck()) {
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

    open fun forceViewCheck(): Boolean {
        return true
    }

    abstract fun onSimulationEventAction(activity: Activity, view: View?, viewC12c: ViewC12c, controlEvent: ControlEvent)

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
            val activityName = it["activityName"]
            val nowActivity: Activity? = ActivityUtils.getTopActivity()
            val nowActivityName: String = nowActivity?.tagName ?: ""
            if (activityName != null && activityName != nowActivityName) {
                return false
            }
        }
        return true
    }


    private fun findWindowRootView(viewC12c: ViewC12c): ViewParent? {
        if (XposedHookUtil.ROOT_VIEWS == null) {
            return null
        }
        var viewParent: ViewParent? = null
        val panelNode = viewC12c.doKitViewPanelNode

        if (panelNode != null) {
            //增强查找DoKit悬浮窗口
            val viewParents = WindowPathUtil.filterDoKitViewRoot(XposedHookUtil.ROOT_VIEWS)
            if (panelNode.windowIndex >= viewParents.size) {
                return null
            }
            val doKitViewParent = viewParents[panelNode.windowIndex]
            val title = WindowPathUtil.getsDoKitViewRootTitle(doKitViewParent)
            if (TextUtils.equals(panelNode.className, title)) {
                viewParent = doKitViewParent
            } else {
                LogHelper.e("DoKit", "findWindowRootView() check failed.")
            }
        } else {
            if (viewC12c.windowIndex >= 0) {
                val windowNode = viewC12c.windowNode
                if (windowNode != null) {
                    val parents: List<ViewParent> = WindowPathUtil.filterViewRoot(XposedHookUtil.ROOT_VIEWS)
                    val windows: List<ViewParent> = WindowPathUtil.filterWindowViewRoot(parents, viewC12c.windowNode)
                    if (windowNode.index >= 0 && windowNode.index < windows.size) {
                        viewParent = windows[windowNode.index]
                    } else {
                        if (windows.isNotEmpty()) {
                            viewParent = windows.last()
                        }
                    }
                } else {
                    viewParent = WindowPathUtil.findViewRoot(XposedHookUtil.ROOT_VIEWS, viewC12c.windowIndex)
                }
            }
        }
        return viewParent
    }


}
