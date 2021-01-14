package com.didichuxing.doraemonkit.kit.methodtrace

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.AbstractKit

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-15-18:22
 * 描    述：
 * 修订历史：
 * ================================================
 */
class MethodCostKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_method_cost

    override val icon: Int
        get() = R.mipmap.dk_method_cost

    override fun onClick(context: Context?) {
        kotlinTip()
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_performance_ck_method_coast"
    }
}