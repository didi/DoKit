package com.didichuxing.doraemonkit.kit.test.event.monitor

import android.view.View
import android.widget.TextView
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.test.DoKitTestManager
import com.didichuxing.doraemonkit.kit.test.event.ControlEventManager
import com.didichuxing.doraemonkit.kit.test.utils.XposedHookUtil
import com.didichuxing.doraemonkit.kit.test.event.ViewC12c
import com.didichuxing.doraemonkit.kit.test.event.EventType
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.kit.test.utils.ViewPathUtil
import com.didichuxing.doraemonkit.kit.test.utils.WindowPathUtil

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
object CustomEventMonitor {

    /**
     * 发送自定义事件
     * @return view
     * @return eventType 事件类型
     * @return param 自定义参数
     */
    fun onCustomEvent(eventType: String, view: View? = null, param: Map<String, String>? = null) {
        if (DoKitTestManager.isHostMode()) {
            val activity = ViewPathUtil.getActivity(view)
            val actionId = ControlEventManager.createNextEventId()
            val viewC12c = createViewC12c(view, eventType, param)

            val controlEvent = ControlEvent(
                actionId,
                EventType.WSE_CUSTOM_EVENT,
                mutableMapOf(
                    "activityName" to activity::class.tagName
                ),
                viewC12c
            )
            ControlEventManager.onControlEventAction(activity, view, controlEvent)
        }
    }

    private fun createViewC12c(view: View?, eventType: String, param: Map<String, String>?): ViewC12c {
        var viewRootImplIndex: Int = -1
        if (view != null) {
            val viewParents = WindowPathUtil.filterViewRoot(XposedHookUtil.ROOT_VIEWS);
            viewParents?.let {
                viewRootImplIndex = if (view.rootView.parent == null) {
                    it.size - 1
                } else {
                    it.indexOf(view.rootView.parent)
                }
            }
        }

        return ViewC12c(
            actionName = eventType,
            params = param ?: mutableMapOf(),
            windowIndex = viewRootImplIndex,
            viewPaths = if (view != null) {
                ViewPathUtil.createViewPathOfWindow(view)
            } else {
                null
            },
            text = if (view is TextView) {
                view.text.toString()
            } else {
                ""
            }
        )
    }
}
