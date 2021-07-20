package com.didichuxing.doraemondemo.comm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.didichuxing.doraemonkit.DoKit

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：3/11/21-17:40
 * 描    述：
 * 修订历史：
 * ================================================
 */
object CommLauncher {
    const val FRAGMENT_CLASS: String = "FRAGMENT_CLASS"

    fun startActivity(
        targetClass: Class<out CommBaseFragment?>,
        context: Context,
        bundle: Bundle? = null
    ) {
        context?.startActivity(Intent(context, CommFragmentActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(FRAGMENT_CLASS, targetClass)
            if (bundle != null) {
                putExtras(bundle)
            }
        })
    }
}