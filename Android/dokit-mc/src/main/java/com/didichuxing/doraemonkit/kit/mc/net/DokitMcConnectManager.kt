package com.didichuxing.doraemonkit.kit.mc.net

import com.didichuxing.doraemonkit.kit.mc.oldui.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.ui.adapter.McClientHistory

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

}
