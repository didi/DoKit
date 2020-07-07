package com.didichuxing.doraemonkit.kit.health

import android.content.Context
import android.text.TextUtils
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.aop.DokitPluginConfig
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.util.DokitUtil.getString

/**
 * @author jintai
 * @desc: 一键体检kit
 * update by pengyushan 2020-07-07
 */
class HealthKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_health

    override val icon: Int
        get() = R.mipmap.dk_health

    override fun onClick(context: Context?) {
        if (!DokitPluginConfig.SWITCH_DOKIT_PLUGIN) {
            ToastUtils.showShort(getString(R.string.dk_plugin_close_tip))
            return
        }

        if (!DokitPluginConfig.SWITCH_NETWORK) {
            ToastUtils.showShort(getString(R.string.dk_plugin_network_close_tip))
            return
        }

        if (!DokitPluginConfig.SWITCH_METHOD) {
            ToastUtils.showShort(getString(R.string.dk_plugin_method_close_tip))
            return
        }

        if (TextUtils.isEmpty(DokitConstant.PRODUCT_ID)) {
            ToastUtils.showShort(getString(R.string.dk_platform_tip))
            return
        }
        startUniversalActivity(context, FragmentIndex.FRAGMENT_HEALTH)
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit = true

    override fun innerKitId(): String {
        return "dokit_sdk_platform_ck_health"
    }
}