package com.didichuxing.doraemonkit.kit

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.util.ActivityUtils
import com.didichuxing.doraemonkit.kit.core.BaseFragment

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-20-15:29
 * 描    述：内置工具必须重写innerKitId而且需要和doraemonkit模块下的assets/dokit_system_kits.json文件中的innerKitId保持一致
 * 否则该工具无法在工具面板中显示
 * 修订历史：
 * ================================================
 */
abstract class AbstractKit : IKit {
    /**
     * 启动UniversalActivity
     *
     * @param fragmentClass
     * @param context
     * @param bundle
     * @param isSystemFragment 是否是内置kit
     */
    fun startUniversalActivity(
        fragmentClass: Class<out BaseFragment>,
        context: Context?,
        bundle: Bundle? = null,
        isSystemFragment: Boolean = false
    ) {
        DoKit.launchFullScreen(fragmentClass, context, bundle, isSystemFragment)
    }


    /**
     * 是否是内置kit 外部kit不需要实现
     *
     * @return
     */
    open val isInnerKit: Boolean
        get() = false

    /**
     * 是否可以显示在工具面板上
     */
    var canShow: Boolean = true

    /**
     * 返回kitId
     * 内置工具必须返回而且需要和doraemonkit模块下的assets/dokit_system_kits.json文件中的innerKitId保持一致
     * 否则该工具无法在工具面板中显示
     * @return
     */
    open fun innerKitId(): String {
        return ""
    }


    /**
     * 返回当前栈顶的activity
     * @return activity
     */
    fun currentActivity(): Activity? {
        return ActivityUtils.getTopActivity()
    }

    @Deprecated("已废弃，重不重写都不影响功能", ReplaceWith(""))
    override val category: Int
        get() = Category.DEFAULT


}
