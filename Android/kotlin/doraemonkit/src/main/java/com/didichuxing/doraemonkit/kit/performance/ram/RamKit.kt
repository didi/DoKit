package com.didichuxing.doraemonkit.kit.performance.ram

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit

class RamKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_frameinfo_ram

    override val icon: Int
        get() = R.mipmap.dk_ram

    override fun onClick(context: Context?) {
        kotlinTip()
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_performance_ck_arm"
    }
}