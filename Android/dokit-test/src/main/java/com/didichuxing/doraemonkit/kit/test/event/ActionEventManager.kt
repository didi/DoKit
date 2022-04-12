package com.didichuxing.doraemonkit.kit.test.event

import com.didichuxing.doraemonkit.kit.test.mock.proxy.IdentityUtils

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
