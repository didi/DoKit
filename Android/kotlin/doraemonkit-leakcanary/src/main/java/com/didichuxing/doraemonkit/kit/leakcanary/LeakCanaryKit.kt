package com.didichuxing.doraemonkit.kit.leakcanary

import android.content.Context
import android.content.Intent
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.squareup.leakcanary.R
import com.squareup.leakcanary.internal.DisplayLeakActivity
import java.lang.Exception

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-24-17:05
 * 描    述：内存泄漏leakCanary功能入口
 * 修订历史：
 * ================================================
 */
class LeakCanaryKit : AbstractKit() {
    override val name: Int = R.string.dk_frameinfo_leakcanary

    override val icon: Int = R.mipmap.leak_canary_icon

    override fun onClick(context: Context?) {
        context?.let {
            val intent = Intent(context, DisplayLeakActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            it.startActivity(intent)
        }

    }

    override fun onAppInit(context: Context?) {

    }

    override val isInnerKit: Boolean = true

    override fun innerKitId(): String = "dokit_sdk_performance_ck_leak"

}