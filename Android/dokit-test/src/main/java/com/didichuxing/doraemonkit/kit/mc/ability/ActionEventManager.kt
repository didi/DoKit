package com.didichuxing.doraemonkit.kit.mc.ability

import com.didichuxing.doraemonkit.kit.mc.mock.proxy.IdentityUtils

object ActionEventManager {


     var currentActionId: String = ""

    fun updateActionId(id: String) {
        if (id.isNullOrEmpty()) {
            currentActionId = IdentityUtils.createAid()
        } else {
            currentActionId = id
        }
    }


}
