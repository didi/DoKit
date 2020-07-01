package com.didichuxing.doraemonkit.kit.crash

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.config.CrashCaptureConfig
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.AbstractKit

/**
 * Created by wanglikun on 2019/6/12
 */
class CrashCaptureKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_crash

    override val icon: Int
        get() = R.mipmap.dk_crash_catch

    override val isInnerKit: Boolean
        get() = true

    override fun onClick(context: Context?) {
        startUniversalActivity(context, FragmentIndex.FRAGMENT_CRASH)
    }

    override fun onAppInit(context: Context?) {
        CrashCaptureManager.instance.init(context)
        if (CrashCaptureConfig.isCrashCaptureOpen) {
            CrashCaptureManager.instance.start()
        } else {
            CrashCaptureManager.instance.stop()
        }
    }

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_crash"
    }
}