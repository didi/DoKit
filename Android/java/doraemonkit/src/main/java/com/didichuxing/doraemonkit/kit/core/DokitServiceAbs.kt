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
abstract class DokitServiceAbs : DokitServiceInterface {
    /**
     * 生命周期
     */
    override fun onCreate(activity: Activity) {}
    override fun onStart(activity: Activity) {}
    override fun onResume(activity: Activity) {}
    override fun onPause(activity: Activity) {}
    override fun onStop(activity: Activity) {}
    override fun onDestroy(activity: Activity) {}
    override fun finish(activity: Activity) {}

    /**
     * 页面事件
     */
    override fun onConfigurationChanged(activity: Activity) {}
    override fun onBackPressed(activity: Activity) {}
    override fun dispatchTouchEvent(activity: Activity) {}
    override fun other(activity: Activity) {}

    /**
     * app 切换到前台
     */
    override fun onForeground(className: String) {}

    /**
     * app 切换到后台
     */
    override fun onBackground() {}
}