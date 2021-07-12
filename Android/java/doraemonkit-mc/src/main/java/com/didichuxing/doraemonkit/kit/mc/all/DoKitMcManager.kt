package com.didichuxing.doraemonkit.kit.mc.all

import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.mc.server.HostInfo

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

    /**
     * 是否处于录制状态
     */
    var IS_MC_RECODING = false


    /**
     * 主机信息
     */
    var HOST_INFO: HostInfo? = null

    var MC_CASE_ID: String = ""


}