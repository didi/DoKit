package com.didichuxing.doraemonkit.kit

import android.app.Activity
import android.content.Context

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
    fun startUniversalActivity(context: Context, fragmentIndex: Int) {

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
     *
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
        return null
    }


}