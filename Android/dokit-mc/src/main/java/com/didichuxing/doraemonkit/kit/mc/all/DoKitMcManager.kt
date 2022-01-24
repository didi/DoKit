package com.didichuxing.doraemonkit.kit.mc.all

import android.app.Activity
import android.view.View
import android.widget.TextView
import com.didichuxing.doraemonkit.constant.WSEType
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.mc.all.view_info.ViewC12c
import com.didichuxing.doraemonkit.kit.mc.client.ClientSyncFailedImpl
import com.didichuxing.doraemonkit.kit.mc.server.DoKitWsServer
import com.didichuxing.doraemonkit.kit.mc.server.HostInfo
import com.didichuxing.doraemonkit.kit.mc.util.ViewPathUtil
import com.didichuxing.doraemonkit.util.ActivityUtils
import com.didichuxing.doraemonkit.util.GsonUtils
import io.ktor.http.cio.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/9-17:42
 * 描    述：
 * 修订历史：
 * ================================================
 */
object DoKitMcManager {

    /**
     * 同步失败回调
     */
    val syncFailedListener = ClientSyncFailedImpl()

    /**
     * 主机
     */
    const val MULTI_CONTROL_MODE_SERVER = 100

    /**
     * 从机
     */
    const val MULTI_CONTROL_MODE_CLIENT = 200

    const val MC_CASE_ID_KEY = "MC_CASE_ID"
    const val MC_CASE_RECODING_KEY = "MC_CASE_RECODING"

    /**
     * 是否处于录制状态
     */
    var IS_MC_RECODING = false


    /**
     * 主机信息
     */
    var HOST_INFO: HostInfo? = null

    var MC_CASE_ID: String = ""

    var mcNetMockInterceptor: McNetMockInterceptor? = null

    /**
     * 发送自定义事件
     * @return view
     * @return eventType 事件类型
     * @return param 自定义参数
     */
    fun sendCustomEvent(eventType: String, view: View? = null, param: Map<String, String>? = null) {
        if (DoKitManager.WS_MODE != WSMode.HOST) {
            return
        }

        if (DoKitManager.MC_CLIENT_PROCESSOR == null) {
            return
        }

        val viewC12c = createViewC12c(view, eventType, param)
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
            viewC12c
        )
        DoKitWsServer.send(wsEvent)
    }

    private fun createViewC12c(
        view: View?,
        eventType: String,
        param: Map<String, String>?
    ): ViewC12c {
        var viewRootImplIndex: Int = -1
        if (view != null) {
            DoKitWindowManager.ROOT_VIEWS?.let {
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
