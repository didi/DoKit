package com.didichuxing.doraemonkit.kit.dokitforweb

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.aop.DokitPluginConfig
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.weaknetwork.WeakNetworkFragment
import com.didichuxing.doraemonkit.util.DoKitCommUtil
import com.didichuxing.doraemonkit.util.ToastUtils
import com.google.auto.service.AutoService

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-24-17:05
 * 描    述：数据库远程访问入口 去掉
 * 修订历史：
 * ================================================
 */
@AutoService(AbstractKit::class)
class DokitForWebKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_dokit_for_web
    override val icon: Int
        get() = R.mipmap.dk_dokit_for_web

    override fun onClickWithReturn(activity: Activity): Boolean {
        if (!DokitPluginConfig.SWITCH_DOKIT_PLUGIN) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_close_tip))
            return false
        }
        startUniversalActivity(DoKitForWebJsInjectFragment::class.java, activity, null, true)
        return true
    }

    override fun onAppInit(context: Context?) {
        DokitForWeb.loadConfig()
    }
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_platform_ck_dokit_for_web"
    }
}
