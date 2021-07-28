package com.didichuxing.doraemonkit.kit.parameter.ram

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.google.auto.service.AutoService

@AutoService(AbstractKit::class)
class RamKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_frameinfo_ram
    override val icon: Int
        get() = R.mipmap.dk_ram

    override fun onClickWithReturn(activity: Activity): Boolean {
        startUniversalActivity(RamMainPageFragment::class.java, activity, null, true)
        return true
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_performance_ck_arm"
    }
}