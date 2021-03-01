package com.didichuxing.doraemonkit.kit.mc.all

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
object McConstant {
    /**
     * 主机
     */
    const val MULTI_CONTROL_MODE_SERVER = 100

    /**
     * 从机
     */
    const val MULTI_CONTROL_MODE_CLIENT = 200

    /**
     * 一机多控类型
     */
    //@JvmField
    var WS_MODE: WSMode = WSMode.UNKNOW

    /**
     * 主机信息
     */
    var HOST_INFO: HostInfo? = null


}