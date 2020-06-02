package com.didichuxing.doraemonkit.kit.weaknetwork

import android.content.Context
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.aop.DokitPluginConfig
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.util.DokitUtil

/**
 * 模拟弱网
 *
 *
 * Created by xiandanin on 2019/5/7 19:05
 */
class WeakNetworkKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_weak_network

    override val icon: Int
        get() = R.mipmap.dk_weak_network

    override fun onClick(context: Context?) {
        kotlinTip()
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_weaknetwork"
    }
}