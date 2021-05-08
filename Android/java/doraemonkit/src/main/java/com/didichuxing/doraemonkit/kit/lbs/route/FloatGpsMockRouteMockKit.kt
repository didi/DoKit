package com.didichuxing.doraemonkit.kit.lbs.route

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.aop.DokitPluginConfig
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter
import com.didichuxing.doraemonkit.util.DoKitCommUtil
import com.didichuxing.doraemonkit.util.ToastUtils
import com.google.auto.service.AutoService

/**
 * Created by changzuozhen on 2021年1月22日
 */
@AutoService(AbstractKit::class)
class FloatGpsMockRouteMockKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_gps_mock_route
    override val icon: Int
        get() = R.mipmap.dk_mock_location_route

    override fun onClick(context: Context?) {
        if (!DokitPluginConfig.SWITCH_DOKIT_PLUGIN) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_close_tip))
            return
        }
        if (!DokitPluginConfig.SWITCH_GPS) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_gps_close_tip))
            return
        }
        SimpleDokitStarter.startFloating(RouteKitView::class.java)
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_lbs_ck_route"
    }
}