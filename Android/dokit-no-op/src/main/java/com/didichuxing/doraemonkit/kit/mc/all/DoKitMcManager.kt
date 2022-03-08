package com.didichuxing.doraemonkit.kit.mc.all

import android.view.View
import com.didichuxing.doraemonkit.constant.WSMode

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

    }


    fun startHostMode() {

    }

    fun startClientMode() {

    }

    fun closeWorkMode() {

    }


}
