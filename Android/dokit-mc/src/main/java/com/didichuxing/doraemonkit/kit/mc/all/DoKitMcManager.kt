package com.didichuxing.doraemonkit.kit.mc.all

import android.view.View
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.mc.ability.monitor.McCustomEventMonitor
import com.didichuxing.doraemonkit.kit.mc.ability.monitor.McTcpMessageEventMonitor
import com.didichuxing.doraemonkit.kit.mc.all.ui.client.ClientSyncFailedImpl
import com.didichuxing.doraemonkit.kit.mc.all.data.HostInfo
import com.didichuxing.doraemonkit.kit.mc.mock.proxy.IdentityUtils
import com.didichuxing.doraemonkit.kit.mc.util.McXposedHookUtils
import com.didichuxing.doraemonkit.util.ActivityUtils
import com.didichuxing.doraemonkit.util.SPUtils

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
    const val DOKIT_MC_CONNECT_URL = "dokit_mc_connect_url"
    const val DOKIT_H5_MC_INJECT_JS = "dokit_h5_mc_inject_js"
    const val NAME_DOKIIT_MC_CONFIGALL = "dokiit-mc-config-all"

    /**
     * 是否处于录制状态
     */
    var IS_MC_RECODING = false


    /**
     * 主机信息
     */
    var HOST_INFO: HostInfo? = null

    var MC_CASE_ID: String = ""

    @JvmField
    var mcNetMockInterceptor: McNetMockInterceptor? = null

    @JvmField
    var mcTcpMessageProcessor: McTcpMessageProcessor ? = null

    var WS_MODE: WSMode = WSMode.UNKNOW

    var CONNECT_MODE: WSMode = WSMode.UNKNOW

    var PROXY_MODE: WSMode = WSMode.CONNECT

    var currentActionId = IdentityUtils.createAid()

    var sp: SPUtils = SPUtils.getInstance(NAME_DOKIIT_MC_CONFIGALL)

    fun init() {
        loadConfig()
    }

    fun loadConfig() {
        DoKitManager.MC_CONNECT_URL = sp.getString(DOKIT_MC_CONNECT_URL)
        DoKitManager.H5_DOKIT_MC_INJECT = sp.getBoolean(DOKIT_H5_MC_INJECT_JS)
    }

    fun saveMcConnectUrl(url: String) {
        DoKitManager.MC_CONNECT_URL = url
        sp.put(DOKIT_MC_CONNECT_URL, url)
    }


    fun saveMcH5Inject(switch: Boolean) {
        DoKitManager.H5_DOKIT_MC_INJECT = switch
        sp.put(DOKIT_H5_MC_INJECT_JS, switch)
    }

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

    fun hookTcpSendMessageEvent(message: String): Boolean {
        //从机收发都拦截不处理
        if (DoKitManager.WS_MODE == WSMode.CLIENT) {
            return true
        }
        if (DoKitManager.WS_MODE == WSMode.HOST) {
            McTcpMessageEventMonitor.onMessageEvent("send", message)
        }
        return false
    }

    fun hookTcpReceiveMessageEvent(message: String): Boolean {
        //从机收发都拦截不处理
        if (DoKitManager.WS_MODE == WSMode.CLIENT) {
            return true
        }
        if (DoKitManager.WS_MODE == WSMode.HOST) {
            McTcpMessageEventMonitor.onMessageEvent("receive", message)
        }
        return false
    }

    fun onTcpMessageEvent(type: String, message: String) {
        if (mcTcpMessageProcessor != null) {
            mcTcpMessageProcessor?.onTcpMessageEvent(type, message)
        }
    }

    fun updateActionId(id: String) {
        if (id.isNullOrEmpty()) {
            currentActionId = IdentityUtils.createAid()
        } else {
            currentActionId = id
        }
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
