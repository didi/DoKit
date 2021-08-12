package com.didichuxing.doraemonkit.kit.blockmonitor

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.google.auto.service.AutoService

/**
 * @desc: 卡顿检测kit
 */
@AutoService(AbstractKit::class)
class BlockMonitorKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_block_monitor
    override val icon: Int
        get() = R.mipmap.dk_block_monitor

    override fun onClickWithReturn(activity: Activity): Boolean {
        startUniversalActivity(BlockMonitorFragment::class.java, activity, null, true)
        return true
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_performance_ck_block"
    }
}