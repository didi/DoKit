package com.didichuxing.doraemonkit.kit.gpsmock

import android.content.Context
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.aop.DokitPluginConfig
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.util.DokitUtil.getString

/**
 * Created by wanglikun on 2018/9/20.
 */
class GpsMockKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_gps_mock

    override val icon: Int
        get() = R.mipmap.dk_gps_mock

    override fun onClick(context: Context?) {
        if (!DokitPluginConfig.SWITCH_DOKIT_PLUGIN) {
            ToastUtils.showShort(getString(R.string.dk_plugin_close_tip))
            return
        }

        if (!DokitPluginConfig.SWITCH_GPS) {
            ToastUtils.showShort(getString(R.string.dk_plugin_gps_close_tip))
            return
        }

        startUniversalActivity(FragmentIndex.FRAGMENT_GPS_MOCK)
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean = true
    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_gps"
    }
}