package com.didichuxing.doraemonkit.kit.mc

import android.view.View
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.test.event.DoKitMcEventDispatcher
import com.didichuxing.doraemonkit.kit.test.event.OnActionEventListener
import com.didichuxing.doraemonkit.kit.test.event.monitor.CustomEventMonitor
import com.didichuxing.doraemonkit.kit.test.event.monitor.TcpMessageEventMonitor
import com.didichuxing.doraemonkit.kit.test.mock.McNetMockInterceptor
import com.didichuxing.doraemonkit.kit.test.mock.McTcpMessageProcessor
import com.didichuxing.doraemonkit.kit.test.TestMode
import com.didichuxing.doraemonkit.kit.test.mock.data.HostInfo
import com.didichuxing.doraemonkit.kit.test.mock.MockManager
import com.didichuxing.doraemonkit.kit.test.mock.MockProxyDataClient
import com.didichuxing.doraemonkit.kit.test.mock.proxy.IdentityUtils
import com.didichuxing.doraemonkit.kit.mc.net.DoKitMcConnectClient
import com.didichuxing.doraemonkit.kit.test.DoKitTestManager
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.kit.test.util.McXposedHookUtils
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

    var mcNetMockInterceptor: McNetMockInterceptor? = null

    var mcTcpMessageProcessor: McTcpMessageProcessor? = null

    var WS_MODE: TestMode = TestMode.UNKNOW

    var CONNECT_MODE: TestMode = TestMode.UNKNOW

    var currentActionId = IdentityUtils.createAid()

    var sp: SPUtils = SPUtils.getInstance(NAME_DOKIIT_MC_CONFIGALL)

    fun init() {
        loadConfig()

        DoKitMcEventDispatcher.addOnActionEventListener(object : OnActionEventListener {
            override fun onActionEvent(wsEvent: ControlEvent) {
                DoKitMcConnectClient.send(wsEvent)
            }
        })

        MockManager.mockProxyDataClient = object :MockProxyDataClient{
            override fun send(data: String) {
                DoKitMcConnectClient.sendDataProxy(data)
            }
        }
    }

    fun loadConfig() {
        DoKitManager.MC_CONNECT_URL = sp.getString(DOKIT_MC_CONNECT_URL)

    }

    fun saveMcConnectUrl(url: String) {
        DoKitManager.MC_CONNECT_URL = url
        sp.put(DOKIT_MC_CONNECT_URL, url)
    }



    /**
     * 发送自定义事件
     * @return view
     * @return eventType 事件类型
     * @return param 自定义参数
     */
    fun sendCustomEvent(eventType: String, view: View? = null, param: Map<String, String>? = null) {
        if (DoKitTestManager.WS_MODE != TestMode.HOST) {
            return
        }

        if (DoKitManager.MC_CLIENT_PROCESSOR == null) {
            return
        }
        CustomEventMonitor.onCustomEvent(eventType, view, param)
    }

    fun hookTcpSendMessageEvent(message: String): Boolean {
        //从机收发都拦截不处理
        if (DoKitTestManager.WS_MODE == TestMode.CLIENT) {
            return true
        }
        if (DoKitTestManager.WS_MODE == TestMode.HOST) {
            TcpMessageEventMonitor.onMessageEvent("send", message)
        }
        return false
    }

    fun hookTcpReceiveMessageEvent(message: String): Boolean {
        //从机收发都拦截不处理
        if (DoKitTestManager.WS_MODE == TestMode.CLIENT) {
            return true
        }
        if (DoKitTestManager.WS_MODE == TestMode.HOST) {
            TcpMessageEventMonitor.onMessageEvent("receive", message)
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
        DoKitTestManager.WS_MODE = TestMode.HOST
        McXposedHookUtils.hookWindowManagerGlobal()
        if (!McXposedHookUtils.HOOK_ENABLE) {
            McXposedHookUtils.runTimeHook(ActivityUtils.getTopActivity())
        }
    }

    fun startClientMode() {
        DoKitTestManager.WS_MODE = TestMode.CLIENT
        McXposedHookUtils.hookWindowManagerGlobal()
    }

    fun closeWorkMode() {
        DoKitTestManager.WS_MODE = TestMode.UNKNOW
    }


}
