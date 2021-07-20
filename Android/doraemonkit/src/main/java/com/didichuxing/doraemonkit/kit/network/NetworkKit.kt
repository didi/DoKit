package com.didichuxing.doraemonkit.kit.network

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.aop.DokitPluginConfig
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.network.ui.NetWorkMonitorFragment
import com.didichuxing.doraemonkit.util.DoKitCommUtil
import com.didichuxing.doraemonkit.util.ToastUtils
import com.google.auto.service.AutoService

/**
 * @desc: 网络监测kit
 */
@AutoService(AbstractKit::class)
class NetworkKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_network_monitor
    override val icon: Int
        get() = R.mipmap.dk_net_monitor

    override fun onClickWithReturn(activity: Activity): Boolean {
        if (!DokitPluginConfig.SWITCH_DOKIT_PLUGIN) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_close_tip))
            return false
        }
        if (!DokitPluginConfig.SWITCH_NETWORK) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_network_close_tip))
            return false
        }
        startUniversalActivity(NetWorkMonitorFragment::class.java, activity, null, true)
        return true
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_performance_ck_network"
    }
}