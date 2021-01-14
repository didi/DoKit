package com.didichuxing.doraemonkit.util

import android.app.Activity
import androidx.fragment.app.Fragment
import java.util.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-18-10:27
 * 描    述：
 * 修订历史：
 * ================================================
 */
object LifecycleListenerUtil {
    var LIFECYCLE_LISTENERS: MutableList<LifecycleListener> = ArrayList()

    /**
     * 悬浮窗初始化时注册activity以及fragment生命周期回调监听
     *
     * @param listener
     */
    fun registerListener(listener: LifecycleListener) {
        LIFECYCLE_LISTENERS.add(listener)
    }

    /**
     * 悬浮窗关闭时注销监听
     *
     * @param listener
     */
    fun unRegisterListener(listener: LifecycleListener) {
        LIFECYCLE_LISTENERS.remove(listener)
    }

    interface LifecycleListener {
        fun onActivityResumed(activity: Activity?)
        fun onActivityPaused(activity: Activity?)
        fun onFragmentAttached(f: Fragment?)
        fun onFragmentDetached(f: Fragment?)
    }
}