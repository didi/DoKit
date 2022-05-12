package com.didichuxing.doraemonkit.gps_mock.lbs.manual

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.aop.DokitPluginConfig
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.util.DoKitCommUtil
import com.didichuxing.doraemonkit.util.ToastUtils

/**
 * Created by changzuozhen on 2021年1月22日
 * 功能不完美
 */
//@AutoService(AbstractKit.class)
class PosAdjustKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_gps_mock_manual
    override val icon: Int
        get() = R.mipmap.dk_mock_location

    override val isInnerKit: Boolean
        get() = true

    override fun onClickWithReturn(activity: Activity): Boolean {
        if (!DokitPluginConfig.SWITCH_DOKIT_PLUGIN) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_close_tip))
            return false
        }
        if (!DokitPluginConfig.SWITCH_GPS) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_gps_close_tip))
            return false
        }
        DoKit.launchFloating<PosAdjustKitView>()
        return true
    }

    override fun onAppInit(context: Context?) {}

    override fun innerKitId(): String = "dokit_sdk_lbs_ck_pos_adjust"
}
