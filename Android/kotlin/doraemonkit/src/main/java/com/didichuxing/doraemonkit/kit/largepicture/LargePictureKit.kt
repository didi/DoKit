package com.didichuxing.doraemonkit.kit.largepicture

import android.content.Context
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.aop.DokitPluginConfig
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.util.DokitUtil

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-24-17:05
 * 描    述：网络大图检测功能入口
 * 修订历史：
 * ================================================
 */
class LargePictureKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_frameinfo_big_img

    override val icon: Int
        get() = R.mipmap.dk_performance_large_picture

    override fun onClick(context: Context?) {
        kotlinTip()
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_performance_ck_img"
    }
}