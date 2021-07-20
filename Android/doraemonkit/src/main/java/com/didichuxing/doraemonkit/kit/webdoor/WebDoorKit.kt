package com.didichuxing.doraemonkit.kit.webdoor

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.google.auto.service.AutoService

/**
 * Created by wanglikun on 2018/10/10.
 */
@AutoService(AbstractKit::class)
class WebDoorKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_web_door
    override val icon: Int
        get() = R.mipmap.dk_web_door

    override fun onClickWithReturn(activity: Activity): Boolean {
        startUniversalActivity(WebDoorFragment::class.java, activity, null, true)
        return true
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_h5"
    }
}