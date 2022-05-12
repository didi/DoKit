package com.didichuxing.doraemonkit.kit.mc.oldui

import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.test.TestMode
import com.didichuxing.doraemonkit.kit.test.mock.data.HostInfo
import com.didichuxing.doraemonkit.util.SPUtils

/**
 * didi Create on 2022/4/22 .
 *
 * Copyright (c) 2022/4/22 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/22 12:25 下午
 * @Description 用一句话说明文件功能
 */
object DoKitMcManager {


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

    private var mode: TestMode = TestMode.UNKNOWN

    fun getMode(): TestMode {
        return mode
    }

    fun init() {
        loadConfig()
    }

    fun loadConfig() {
        DoKitManager.MC_CONNECT_URL = sp.getString(DOKIT_MC_CONNECT_URL)

    }

    fun saveMcConnectUrl(url: String) {
        DoKitManager.MC_CONNECT_URL = url
        sp.put(DOKIT_MC_CONNECT_URL, url)
    }


}
