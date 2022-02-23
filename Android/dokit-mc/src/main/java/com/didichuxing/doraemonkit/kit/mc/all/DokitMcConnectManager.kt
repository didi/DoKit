package com.didichuxing.doraemonkit.kit.mc.all

import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.mc.all.ui.McClientHistory
import com.didichuxing.doraemonkit.kit.mc.all.ui.connect.ConnectDokitView
import com.didichuxing.doraemonkit.util.ToastUtils

object DokitMcConnectManager {


    var connectMode: ConnectMode = ConnectMode.CLOSE

    var itemHistory: McClientHistory? = null
    var currentConnectHistory: McClientHistory? = null

    var currentClientHistory: McClientHistory? = null

    var dokitFloatViews = mutableListOf<ConnectDokitView>()

    fun changeHostMode() {
        DoKitMcManager.CONNECT_MODE = WSMode.HOST
        DoKitMcManager.startHostMode()
        updateConnectModeDokitView()
        ToastUtils.showShort("主机模式")
    }

    fun changeClientMode() {
        DoKitMcManager.CONNECT_MODE = WSMode.CLIENT
        DoKitMcManager.startClientMode()
        updateConnectModeDokitView()
        ToastUtils.showShort("从机模式")
    }

    private fun updateConnectModeDokitView() {
        dokitFloatViews.forEach {
            it.updateConnectMode()
        }
    }

}
