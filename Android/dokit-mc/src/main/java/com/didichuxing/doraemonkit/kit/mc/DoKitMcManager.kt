package com.didichuxing.doraemonkit.kit.mc

import android.app.Activity
import android.view.View
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.test.event.OnControlEventActionListener
import com.didichuxing.doraemonkit.kit.test.event.monitor.CustomEventMonitor
import com.didichuxing.doraemonkit.kit.test.TestMode
import com.didichuxing.doraemonkit.kit.test.mock.data.HostInfo
import com.didichuxing.doraemonkit.kit.test.mock.MockManager
import com.didichuxing.doraemonkit.kit.test.mock.ProxyMockCallback
import com.didichuxing.doraemonkit.kit.mc.net.DoKitMcConnectClient
import com.didichuxing.doraemonkit.kit.test.DoKitTestManager
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.kit.test.event.ControlEventManager
import com.didichuxing.doraemonkit.mc.R
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


    var WS_MODE: TestMode = TestMode.UNKNOWN

    var CONNECT_MODE: TestMode = TestMode.UNKNOWN

    var sp: SPUtils = SPUtils.getInstance(NAME_DOKIIT_MC_CONFIGALL)

    fun init() {
        loadConfig()

        ControlEventManager.addOnControlEventActionListener(object : OnControlEventActionListener {

            override fun onControlEventAction(activity: Activity?, view: View?, event: ControlEvent) {
                if (view != null && view.id == R.id.dokit_mode_switch_btn) {
                    return
                }
                DoKitMcConnectClient.send(event)
            }
        })

        MockManager.proxyMockCallback = object : ProxyMockCallback {
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
        if (!DoKitTestManager.isHostMode()) {
            return
        }

        if (DoKitManager.MC_CLIENT_PROCESSOR == null) {
            return
        }
        CustomEventMonitor.onCustomEvent(eventType, view, param)
    }


    fun startHostMode() {
        DoKitTestManager.startTest(TestMode.HOST)
    }

    fun startClientMode() {
        DoKitTestManager.startTest(TestMode.CLIENT)
    }

    fun closeWorkMode() {
        DoKitTestManager.closeTest()
    }


}
