package com.didichuxing.doraemonkit.kit.core

import android.app.Activity
import com.didichuxing.doraemonkit.constant.DoKitModule
import com.didichuxing.doraemonkit.kit.health.CountDownDoKitView

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-28-15:18
 * 描    述：页面浮标管理类接口
 * 修订历史：
 * ================================================
 */
abstract class AbsDoKitViewManager : DoKitViewManagerInterface {
    protected var TAG = this.javaClass.simpleName

    /**
     * 添加倒计时DokitView
     */
    fun attachCountDownDoKitView(activity: Activity) {
        if (!DoKitManager.APP_HEALTH_RUNNING) {
            return
        }
        if (activity is UniversalActivity) {
            return
        }
        val dokitIntent = DoKitIntent(CountDownDoKitView::class.java)
        dokitIntent.mode = DoKitViewLaunchMode.COUNTDOWN
        attach(dokitIntent)
    }

    /**
     * 添加一机多控标识
     *
     * @param activity
     */
    fun attachMcRecodingDoKitView(activity: Activity) {
        val action: Map<String, String> = mapOf("action" to "launch_recoding_view")
        DoKitManager.getModuleProcessor(DoKitModule.MODULE_MC)?.proceed(action)
    }

    /**
     * 添加主icon
     */
    abstract fun attachMainIcon(activity: Activity?)

    /**
     * 移除主icon
     */
    abstract fun detachMainIcon()

    /**
     * 添加toolPanel
     */
    abstract fun attachToolPanel(activity: Activity?)

    /**
     * 移除toolPanel
     */
    abstract fun detachToolPanel()

    /**
     * main activity 创建时回调
     *
     * @param activity
     */
    abstract fun onMainActivityResume(activity: Activity?)

    /**
     * Activity 创建时回调
     *
     * @param activity
     */
    abstract fun onActivityResume(activity: Activity?)

    /**
     * Activity 页面回退的时候回调
     *
     * @param activity
     */
    abstract fun onActivityBackResume(activity: Activity?)
}
