package com.didichuxing.doraemonkit.kit.performance.fps

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.AbstractKit

/**
 * Created by wanglikun on 2018/9/13.
 */
class FrameInfoKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_frame_info

    override val icon: Int
        get() = R.mipmap.dk_frame_hist

    override fun onClick(context: Context?) {
        startUniversalActivity(context, FragmentIndex.FRAGMENT_FRAME_INFO)
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_performance_ck_fps"
    }
}