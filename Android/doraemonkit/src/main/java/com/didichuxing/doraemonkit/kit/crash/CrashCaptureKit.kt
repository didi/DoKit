package com.didichuxing.doraemonkit.kit.crash

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.config.CrashCaptureConfig
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.google.auto.service.AutoService

/**
 * Created by wanglikun on 2019/6/12
 */
@AutoService(AbstractKit::class)
class CrashCaptureKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_crash
    override val icon: Int
        get() = R.mipmap.dk_crash_catch

    override fun onClickWithReturn(activity: Activity): Boolean {
        startUniversalActivity(CrashCaptureMainFragment::class.java, activity, null, true)
        return true
    }

    override fun onAppInit(context: Context?) {
        CrashCaptureManager.getInstance().init(context)
        if (CrashCaptureConfig.isCrashCaptureOpen()) {
            CrashCaptureManager.getInstance().start()
        } else {
            CrashCaptureManager.getInstance().stop()
        }
    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_crash"
    }
}