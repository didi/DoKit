package com.didichuxing.doraemonkit.kit.loginfo

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit

/**
 * Created by wanglikun on 2018/10/9.
 */
class LogInfoKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_log_info

    override val icon: Int
        get() = R.mipmap.dk_log_info

    override fun onClick(context: Context?) {
        kotlinTip()
    }

    override fun onAppInit(context: Context?) {
    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_log"
    }
}