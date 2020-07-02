package com.didichuxing.doraemonkit.kit.timecounter

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.AbstractKit

/**
 * app启动、页面跳转的计时kit
 */
class TimeCounterKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_time_counter

    override val icon: Int
        get() = R.mipmap.dk_time_counter

    override fun onClick(context: Context?) {
        startUniversalActivity(context, FragmentIndex.FRAGMENT_TIME_COUNTER)
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_performance_ck_open_coast"
    }

}