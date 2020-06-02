package com.didichuxing.doraemonkit.kit.health

import android.content.Context
import android.text.TextUtils
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.aop.DokitPluginConfig
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.util.DokitUtil

/**
 * @author jintai
 * @desc: 一键体检kit
 */
class HealthKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_health

    override val icon: Int
        get() = R.mipmap.dk_health

    override fun onClick(context: Context?) {
        kotlinTip()
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_platform_ck_health"
    }
}