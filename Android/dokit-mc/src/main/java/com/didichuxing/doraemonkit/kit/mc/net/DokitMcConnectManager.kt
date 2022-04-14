package com.didichuxing.doraemonkit.kit.mc.net

import com.didichuxing.doraemonkit.kit.mc.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.ui.adapter.McClientHistory
import com.didichuxing.doraemonkit.kit.mc.ui.connect.ConnectDokitView
import com.didichuxing.doraemonkit.kit.test.TestMode
import com.didichuxing.doraemonkit.util.ToastUtils

object DokitMcConnectManager {


    var connectMode: ConnectMode = ConnectMode.CLOSE

    var itemHistory: McClientHistory? = null
    var currentConnectHistory: McClientHistory? = null
        set(value) {
            val url = value?.url ?: ""
            DoKitMcManager.saveMcConnectUrl(url)
            field = value
        }

    var currentClientHistory: McClientHistory? = null

    var dokitFloatViews = mutableListOf<ConnectDokitView>()

    fun changeHostMode() {
        DoKitMcManager.CONNECT_MODE = TestMode.HOST
        DoKitMcManager.startHostMode()
        updateConnectModeDokitView()

        DoKitMcConnectClient.sendChangeHostMode()
        ToastUtils.showShort("主机模式")
    }

    fun changeClientMode() {
        DoKitMcManager.CONNECT_MODE = TestMode.CLIENT
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
