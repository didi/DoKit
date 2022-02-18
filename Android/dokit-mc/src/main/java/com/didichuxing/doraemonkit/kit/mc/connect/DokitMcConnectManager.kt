package com.didichuxing.doraemonkit.kit.mc.connect

import com.didichuxing.doraemonkit.kit.mc.all.ui.McClientHistory
import com.didichuxing.doraemonkit.util.ToastUtils

object DokitMcConnectManager {


    var connectMode: ConnectMode = ConnectMode.CLOSE


    var itemHistory: McClientHistory? = null
    var currentConnectHistory: McClientHistory? = null

    var currentClientHistory: McClientHistory? = null

    fun changeHostMode() {
        ToastUtils.showShort("主机模式")
    }

    fun changeClientMode() {
        ToastUtils.showShort("从机模式")
    }
}
