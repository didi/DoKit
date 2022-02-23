package com.didichuxing.doraemonkit.kit.mc.all

import android.view.View
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.mc.ability.monitor.McCustomEventMonitor
import com.didichuxing.doraemonkit.kit.mc.all.ui.client.ClientSyncFailedImpl
import com.didichuxing.doraemonkit.kit.mc.all.data.HostInfo
import com.didichuxing.doraemonkit.kit.mc.util.McXposedHookUtils
import com.didichuxing.doraemonkit.util.ActivityUtils

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

    var WS_MODE: WSMode = WSMode.UNKNOW

    var CONNECT_MODE: WSMode = WSMode.UNKNOW

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
        McCustomEventMonitor.onCustomEvent(eventType, view, param)
    }


    fun startHostMode() {
        DoKitManager.WS_MODE = WSMode.HOST
        McXposedHookUtils.hookWindowManagerGlobal()
        if (!McXposedHookUtils.HOOK_ENABLE) {
            McXposedHookUtils.runTimeHook(ActivityUtils.getTopActivity())
        }
    }

    fun startClientMode() {
        DoKitManager.WS_MODE = WSMode.CLIENT
        McXposedHookUtils.hookWindowManagerGlobal()
    }

    fun closeWorkMode() {
        DoKitManager.WS_MODE = WSMode.UNKNOW
    }


}
