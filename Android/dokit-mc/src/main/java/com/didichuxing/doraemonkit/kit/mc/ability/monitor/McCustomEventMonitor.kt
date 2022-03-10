package com.didichuxing.doraemonkit.kit.mc.ability.monitor

import android.app.Activity
import android.view.View
import android.widget.TextView
import com.didichuxing.doraemonkit.constant.WSEType
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.mc.util.McXposedHookUtils
import com.didichuxing.doraemonkit.kit.mc.all.view_info.ViewC12c
import com.didichuxing.doraemonkit.kit.mc.ability.DoKitMcEventDispatcher
import com.didichuxing.doraemonkit.kit.mc.all.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.mock.proxy.IdentityUtils
import com.didichuxing.doraemonkit.kit.mc.net.WSEvent
import com.didichuxing.doraemonkit.kit.mc.util.ViewPathUtil
import com.didichuxing.doraemonkit.util.ActivityUtils
import com.didichuxing.doraemonkit.util.GsonUtils

object McCustomEventMonitor {

    /**
     * 发送自定义事件
     * @return view
     * @return eventType 事件类型
     * @return param 自定义参数
     */
    fun onCustomEvent(eventType: String, view: View? = null, param: Map<String, String>? = null) {
        val viewC12c = createViewC12c(view, eventType, param)
        val actionId = IdentityUtils.createAid()
        val wsEvent = WSEvent(
            WSMode.HOST,
            WSEType.WSE_CUSTOM_EVENT,
            mutableMapOf(
                "activityName" to if (view != null && view.context is Activity) {
                    view.context::class.tagName
                } else {
                    ActivityUtils.getTopActivity()::class.tagName
                }
            ),
            viewC12c,
            actionId
        )
        DoKitMcManager.updateActionId(actionId)
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
            customEventType = eventType,
            customParams = if (param == null) {
                "{}"
            } else {
                GsonUtils.toJson(param)
            },
            viewRootImplIndex = viewRootImplIndex,
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
