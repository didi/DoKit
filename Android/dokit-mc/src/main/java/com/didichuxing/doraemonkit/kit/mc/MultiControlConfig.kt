package com.didichuxing.doraemonkit.kit.mc

import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.mc.oldui.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.ui.adapter.McClientHistory
import com.didichuxing.doraemonkit.util.SPUtils


/**
 * didi Create on 2022/4/22 .
 *
 * Copyright (c) 2022/4/22 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/22 12:25 下午
 * @Description 一机多控配置
 */

object MultiControlConfig {


    private const val DOKIT_MC_CONNECT_URL = "dokit_mc_connect_url"
    private const val NAME_DOKIIT_MC_CONFIGALL = "dokiit-mc-config-all"


    private var MC_CONNECT_URL = ""


    var sp: SPUtils = SPUtils.getInstance(NAME_DOKIIT_MC_CONFIGALL)


    var currentConnectHistory: McClientHistory? = null
        set(value) {
            val url = value?.url ?: ""
            saveMcConnectUrl(url)
            field = value
        }


    fun init() {
        loadConfig()
    }

    fun loadConfig() {
        val url = sp.getString(DoKitMcManager.DOKIT_MC_CONNECT_URL)
        DoKitManager.MC_CONNECT_URL = url
        MC_CONNECT_URL = url
    }

    fun saveMcConnectUrl(url: String) {
        MC_CONNECT_URL = url
        DoKitManager.MC_CONNECT_URL = url
        sp.put(DOKIT_MC_CONNECT_URL, url)
    }


}
