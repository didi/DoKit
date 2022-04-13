package com.didichuxing.doraemonkit.kit.test.event.monitor

import android.app.Activity
import android.view.View
import android.widget.TextView
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.test.event.ActionEventManager
import com.didichuxing.doraemonkit.kit.test.util.McXposedHookUtils
import com.didichuxing.doraemonkit.kit.test.event.ViewC12c
import com.didichuxing.doraemonkit.kit.test.event.DoKitMcEventDispatcher
import com.didichuxing.doraemonkit.kit.test.event.EventType
import com.didichuxing.doraemonkit.kit.test.util.RandomIdentityUtils
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.kit.test.util.ViewPathUtil
import com.didichuxing.doraemonkit.util.ActivityUtils

object CustomEventMonitor {

    /**
     * 发送自定义事件
     * @return view
     * @return eventType 事件类型
     * @return param 自定义参数
     */
    fun onCustomEvent(eventType: String, view: View? = null, param: Map<String, String>? = null) {
        val viewC12c = createViewC12c(view, eventType, param)
        val actionId = RandomIdentityUtils.createAid()
        val wsEvent = ControlEvent(
            actionId,
            EventType.WSE_CUSTOM_EVENT,
            mutableMapOf(
                "activityName" to if (view != null && view.context is Activity) {
                    view.context::class.tagName
                } else {
                    ActivityUtils.getTopActivity()::class.tagName
                }
            ),
            viewC12c,
            ""

        )
        ActionEventManager.updateActionId(actionId)
        DoKitMcEventDispatcher.send(wsEvent)
    }

    private fun createViewC12c(view: View?, eventType: String, param: Map<String, String>?): ViewC12c {
        var viewRootImplIndex: Int = -1
        if (view != null) {
            McXposedHookUtils.ROOT_VIEWS?.let {
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
