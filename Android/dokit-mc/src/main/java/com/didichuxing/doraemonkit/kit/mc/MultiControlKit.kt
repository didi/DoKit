package com.didichuxing.doraemonkit.kit.mc

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.didichuxing.doraemonkit.aop.DokitPluginConfig
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.mc.oldui.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.ui.DoKitMcActivity
import com.didichuxing.doraemonkit.mc.R
import com.didichuxing.doraemonkit.util.DoKitCommUtil
import com.didichuxing.doraemonkit.util.ToastUtils
import com.google.auto.service.AutoService

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/23-13:30
 * 描    述：一机多控入口
 * 修订历史：
 * ================================================
 */
@AutoService(AbstractKit::class)
class MultiControlKit : AbstractKit() {
    override val name: Int get() = R.string.dk_kit_multi_control
    override val icon: Int get() = R.mipmap.dk_icon_mc

    override fun onClickWithReturn(activity: Activity): Boolean {
        if (!DokitPluginConfig.SWITCH_DOKIT_PLUGIN) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_close_tip))
            return false
        }
        val intent = Intent(activity, DoKitMcActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(intent)

        return true
    }

    override fun onAppInit(context: Context?) {
        MultiControlConfig.init()
        DoKitMcManager.init()
    }

    override val isInnerKit: Boolean get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_platform_ck_mc"
    }
}
