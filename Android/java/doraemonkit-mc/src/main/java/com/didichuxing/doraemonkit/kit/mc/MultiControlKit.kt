package com.didichuxing.doraemonkit.kit.mc

import android.content.Context
import android.content.Intent
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.mc.all.ui.DoKitMcActivity
import com.didichuxing.doraemonkit.mc.R

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/23-13:30
 * 描    述：一机多控入口
 * 修订历史：
 * ================================================
 */
class MultiControlKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_multi_control
    override val icon: Int
        get() = R.mipmap.dk_icon_mc

    override fun onClick(context: Context?) {
        context?.let {
            val intent = Intent(context, DoKitMcActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            it.startActivity(intent)
        }
    }

    override fun onAppInit(context: Context?) {
    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_platform_ck_mc"
    }
}