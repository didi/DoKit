package com.didichuxing.doraemonkit.kit.weaknetwork

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.aop.DokitPluginConfig
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.util.DoKitCommUtil
import com.didichuxing.doraemonkit.util.ToastUtils
import com.google.auto.service.AutoService

/**
 * 模拟弱网
 *
 *
 * Created by xiandanin on 2019/5/7 19:05
 */
@AutoService(AbstractKit::class)
class WeakNetworkKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_weak_network
    override val icon: Int
        get() = R.mipmap.dk_weak_network

    override fun onClickWithReturn(activity: Activity): Boolean {
        if (!DokitPluginConfig.SWITCH_DOKIT_PLUGIN) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_close_tip))
            return false
        }
        if (!DokitPluginConfig.SWITCH_NETWORK) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_network_close_tip))
            return false
        }
        startUniversalActivity(WeakNetworkFragment::class.java, activity, null, true)
        return true
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_weaknetwork"
    }
}