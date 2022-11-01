package com.didichuxing.doraemonkit.kit.core

import android.app.Activity

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/9-17:00
 * 描    述：
 * 修订历史：
 * ================================================
 */
interface DoKitLifecycleInterface {
    /**
     * 生命周期
     */
    fun onCreate(activity: Activity){}
    fun onStart(activity: Activity){}
    fun onResume(activity: Activity){}
    fun onPause(activity: Activity){}
    fun onStop(activity: Activity){}
    fun onDestroy(activity: Activity){}
    fun finish(activity: Activity){}

    /**
     * 页面事件
     */
    fun onConfigurationChanged(activity: Activity){}
    fun onBackPressed(activity: Activity){}
    fun dispatchTouchEvent(activity: Activity){}
    fun other(activity: Activity){}

    /**
     * app 切换到前台
     */
    fun onForeground(activity: Activity) {}

    /**
     * app 切换到后台
     */
    fun onBackground(activity: Activity) {}
}
