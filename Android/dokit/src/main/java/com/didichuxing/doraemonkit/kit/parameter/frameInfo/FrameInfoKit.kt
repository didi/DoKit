package com.didichuxing.doraemonkit.kit.parameter.frameInfo

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.google.auto.service.AutoService

/**
 * Created by wanglikun on 2018/9/13.
 */
@AutoService(AbstractKit::class)
class FrameInfoKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_frame_info
    override val icon: Int
        get() = R.mipmap.dk_frame_hist

    override fun onClickWithReturn(activity: Activity): Boolean {
        startUniversalActivity(FrameInfoFragment::class.java, activity, null, true)
        return true
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_performance_ck_fps"
    }
}