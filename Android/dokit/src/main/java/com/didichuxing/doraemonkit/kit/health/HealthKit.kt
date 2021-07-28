package com.didichuxing.doraemonkit.kit.health

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.aop.DokitPluginConfig
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.util.DoKitCommUtil
import com.didichuxing.doraemonkit.util.ToastUtils
import com.google.auto.service.AutoService

/**
 * @author jintai
 * @desc: 一键体检kit
 */
@AutoService(AbstractKit::class)
class HealthKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_health
    override val icon: Int
        get() = R.mipmap.dk_health

    override fun onClickWithReturn(activity: Activity): Boolean {
        if (TextUtils.isEmpty(DoKitManager.PRODUCT_ID)) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_platform_tip))
            return false
        }
        if (!DokitPluginConfig.SWITCH_DOKIT_PLUGIN) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_close_tip))
            return false
        }
        if (!DokitPluginConfig.SWITCH_NETWORK) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_network_close_tip))
            return false
        }
        if (!DokitPluginConfig.SWITCH_METHOD) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_method_close_tip))
            return false
        }
        startUniversalActivity(HealthFragment::class.java, activity, null, true)
        return true
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_platform_ck_health"
    }
}