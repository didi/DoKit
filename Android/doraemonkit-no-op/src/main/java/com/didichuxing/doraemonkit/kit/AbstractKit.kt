package com.didichuxing.doraemonkit.kit

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import com.blankj.utilcode.util.ActivityUtils
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.kit.core.UniversalActivity

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-20-15:29
 * 描    述：
 * 修订历史：
 * ================================================
 */
abstract class AbstractKit : IKit {
    /**
     * 启动UniversalActivity
     *
     * @param context
     * @param fragmentIndex
     */
    fun startUniversalActivity(context: Context, fragmentIndex: Int) {}
}