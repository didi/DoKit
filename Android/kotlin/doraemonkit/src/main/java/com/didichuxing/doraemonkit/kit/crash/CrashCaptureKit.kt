package com.didichuxing.doraemonkit.kit.crash

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit

/**
 * Created by wanglikun on 2019/6/12
 */
class CrashCaptureKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_crash

    override val icon: Int
        get() = R.mipmap.dk_crash_catch

    override fun onClick(context: Context?) {
        kotlinTip()
    }

    override fun onAppInit(context: Context?) {

    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_crash"
    }
}