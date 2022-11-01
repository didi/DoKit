package com.didichuxing.doraemonkit.kit.connect

import com.didichuxing.doraemonkit.util.SPUtils
/**
 * didi Create on 2022/4/12 .
 *
 * Copyright (c) 2022/4/12 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/12 6:07 下午
 * @Description 用一句话说明文件功能
 */
object ConnectConfig {

    private const val NAME_CONNECT_CONFIG_FILE = "dokit_connect_config_all"
    private const val KEY_CONNECT_SERIAL = "connect_serial"

    private var configSP: SPUtils = SPUtils.getInstance(NAME_CONNECT_CONFIG_FILE)

    private var connectSerial: String? = null


    fun getConnectSerial(): String {
        val text = connectSerial
        if (text == null) {
            connectSerial = configSP.getString(KEY_CONNECT_SERIAL, "")
        } else {
            return text
        }
        return configSP.getString(KEY_CONNECT_SERIAL, "")
    }

    fun saveConnectSerial(text: String) {
        connectSerial = text
        configSP.put(KEY_CONNECT_SERIAL, text)
    }


}
