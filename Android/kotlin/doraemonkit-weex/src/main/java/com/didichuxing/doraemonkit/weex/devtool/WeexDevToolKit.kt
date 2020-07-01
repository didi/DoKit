package com.didichuxing.doraemonkit.weex.devtool

import android.content.Context
import android.content.Intent
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.weex.R
import com.didichuxing.doraemonkit.weex.util.launchActivity

/**
 * Transformed by alvince on 2020/7/1
 *
 * @author haojianglong
 * @date 2019-06-11
 */
class WeexDevToolKit : AbstractKit() {

    override val name: Int
        get() = R.string.dk_dev_tool_name

    override val icon: Int
        get() = R.mipmap.dk_custom

    override val isInnerKit: Boolean
        get() = true

    override fun onAppInit(context: Context?) {
    }

    override fun onClick(context: Context?) {
        context?.also {
            Intent(it, DevToolActivity::class.java)
                .apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                .also { intent ->
                    it.launchActivity(intent)
                }
        }
    }

    override fun innerKitId(): String = "dokit_sdk_weex_ck_devtool"

}