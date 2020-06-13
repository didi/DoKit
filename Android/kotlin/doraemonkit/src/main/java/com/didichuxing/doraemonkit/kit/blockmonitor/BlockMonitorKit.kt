package com.didichuxing.doraemonkit.kit.blockmonitor

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.AbstractKit

/**
 * @desc: 卡顿检测kit
 */
class BlockMonitorKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_block_monitor

    override val icon: Int
        get() = R.mipmap.dk_block_monitor

    override fun onClick(context: Context?) {
        startUniversalActivity(context, FragmentIndex.FRAGMENT_BLOCK_MONITOR)
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_performance_ck_block"
    }
}