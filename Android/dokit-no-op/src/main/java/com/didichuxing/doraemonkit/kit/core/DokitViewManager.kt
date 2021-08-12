package com.didichuxing.doraemonkit.kit.core

import android.app.Activity


/**
 * Created by jintai on 2018/10/23.
 * 浮标管理类
 */
class DokitViewManager : DokitViewManagerInterface {

    companion object {
        @JvmStatic
        val instance: DokitViewManager by lazy {
            DokitViewManager()
        }

    }


    /**
     * 当app进入后台时调用
     */
    override fun notifyBackground() {
    }

    /**
     * 当app进入前台时调用
     */
    override fun notifyForeground() {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun dispatchOnActivityResumed(activity: Activity?) {
    }

    override fun attach(dokitIntent: DokitIntent) {
    }

    override fun detach(dokitView: AbsDokitView) {
    }

    override fun detach(tag: String) {
    }


    override fun detach(doKitViewClass: Class<out AbsDokitView>) {
    }


    /**
     * 移除所有activity的所有dokitView
     */
    override fun detachAll() {
    }

    override fun <T : AbsDokitView> getDoKitView(
        activity: Activity?,
        clazz: Class<T>
    ): AbsDokitView? {
        return null
    }

    override fun getDoKitViews(activity: Activity?): Map<String, AbsDokitView>? {
        return null
    }


    /**
     * 系统悬浮窗需要调用
     */
    interface DokitViewAttachedListener {
        fun onDokitViewAdd(dokitView: AbsDokitView?)
    }







}