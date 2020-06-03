package com.didichuxing.doraemonkit.kit.network

import android.content.Context
import android.text.TextUtils
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.aop.DokitPluginConfig
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.util.DokitUtil

/**
 * @author jintai
 * @desc: 网络监测kit
 */
class MockKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_network_mock

    override val icon: Int
        get() = R.mipmap.dk_net_mock

    override fun onClick(context: Context?) {
        kotlinTip()
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_platform_ck_mock"
    }
}