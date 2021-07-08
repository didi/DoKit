package com.didichuxing.doraemonkit.kit.core

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.DoKit.Companion.isMainIconShow
import com.didichuxing.doraemonkit.DoKit.Companion.show
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.constant.DoKitConstant.getModuleProcessor
import com.didichuxing.doraemonkit.constant.DoKitModule
import com.didichuxing.doraemonkit.kit.health.CountDownDokitView
import com.didichuxing.doraemonkit.kit.main.MainIconDokitView
import com.didichuxing.doraemonkit.model.ActivityLifecycleInfo
import com.didichuxing.doraemonkit.util.DoKitSystemUtil
import com.didichuxing.doraemonkit.util.SPUtils
import java.util.*

/**
 * Created by wanglikun on 2018/10/23.
 * 系统悬浮窗管理类
 */
internal class SystemDokitViewManager : AbsDokitViewManager() {
    /**
     * 参考:
     * https://blog.csdn.net/awenyini/article/details/78265284
     * https://yuqirong.me/2017/09/28/Window%E6%BA%90%E7%A0%81%E8%A7%A3%E6%9E%90(%E4%B8%80)%EF%BC%9A%E4%B8%8EDecorView%E7%9A%84%E9%82%A3%E4%BA%9B%E4%BA%8B/
     */
    private val mWindowManager = DokitViewManager.instance.windowManager
    private val mContext: Context by lazy { DoKit.APPLICATION }
    private val mDokitViews: MutableList<AbsDokitView> by lazy { mutableListOf() }
    private val mListeners: MutableList<DokitViewManager.DokitViewAttachedListener> by lazy { mutableListOf() }

    /**
     * 获取页面上所有的dokitViews
     *
     * @return map
     */
    override fun getDokitViews(activity: Activity): Map<String, AbsDokitView> {

        val dokitViewMaps: MutableMap<String, AbsDokitView> = mutableMapOf()
        for (dokitView in mDokitViews) {
            dokitViewMaps[dokitView.tag] = dokitView
        }
        return dokitViewMaps
    }

    /**
     * 当app进入后台时调用
     */
    override fun notifyBackground() {

        for (dokitView in mDokitViews) {
            dokitView.onEnterBackground()
        }
    }

    /**
     * 当app进入前台时调用
     */
    override fun notifyForeground() {

        for (page in mDokitViews) {
            page.onEnterForeground()
        }
    }

    /**
     * @param activity
     */
    override fun resumeAndAttachDokitViews(activity: Activity) {
        if (activity is UniversalActivity) {
            val countDownDokitView =
                getDokitView(activity, CountDownDokitView::class.java.simpleName)
            if (countDownDokitView != null) {
                DokitViewManager.instance.detach(
                    CountDownDokitView::class.java.simpleName
                )
            }
            return
        }
        //app启动
        if (DoKitSystemUtil.isOnlyFirstLaunchActivity(activity)) {
            onMainActivityCreate(activity)
        }
        val activityLifecycleInfo =
            DoKitConstant.ACTIVITY_LIFECYCLE_INFOS[activity.javaClass.canonicalName]
        //新建Activity
        if (activityLifecycleInfo != null && activityLifecycleInfo.activityLifeCycleCount == ActivityLifecycleInfo.ACTIVITY_LIFECYCLE_CREATE2RESUME) {
            onActivityCreate(activity)
        }

        //activity resume
        if (activityLifecycleInfo != null && activityLifecycleInfo.activityLifeCycleCount > ActivityLifecycleInfo.ACTIVITY_LIFECYCLE_CREATE2RESUME) {
            onActivityResume(activity)
        }

        //生命周期回调
        val dokitViewMap = getDokitViews(activity)
        for (absDokitView in dokitViewMap.values) {
            absDokitView!!.onResume()
        }
    }

    override fun onMainActivityCreate(activity: Activity) {
        //倒计时DokitView
        attachCountDownDokitView(activity)
        attachMcDokitView(activity)
        if (!DoKitConstant.AWAYS_SHOW_MAIN_ICON) {
            return
        }

        //添加main icon
        val intent = DokitIntent(
            MainIconDokitView::class.java
        )
        intent.mode = DokitIntent.MODE_SINGLE_INSTANCE
        DokitViewManager.instance.attach(intent)
        DoKitConstant.MAIN_ICON_HAS_SHOW = true

        //添加录制中的悬浮窗
        if (SPUtils.getInstance().getBoolean(DoKitConstant.MC_CASE_RECODING_KEY, false)) {
            val action: MutableMap<String, String> = HashMap()
            action["action"] = "launch_recoding_view"
            getModuleProcessor(DoKitModule.MODULE_MC)!!.proceed(action)
        }
    }

    override fun onActivityCreate(activity: Activity) {
        //判断是否有MainIcon
        if (DoKitConstant.AWAYS_SHOW_MAIN_ICON && !isMainIconShow) {
            show()
        }
        //如果倒计时浮标没显示则重新添加
        val countDownDokitView = getDokitView(activity, CountDownDokitView::class.java.simpleName)
        if (countDownDokitView == null) {
            if (activity is UniversalActivity) {
                return
            }
            attachCountDownDokitView(activity)
        } else {
            if (activity is UniversalActivity) {
                DokitViewManager.instance.detach(
                    CountDownDokitView::class.java.simpleName
                )
            } else {
                //重置倒计时
                (countDownDokitView as CountDownDokitView).resetTime()
            }
        }
        attachMcDokitView(activity)
    }

    override fun onActivityResume(activity: Activity) {
        //移除倒计时浮标
        val countDownDokitView = getDokitView(activity, CountDownDokitView::class.java.simpleName)
        if (countDownDokitView == null) {
            attachCountDownDokitView(activity)
        } else {
            //重置倒计时
            (countDownDokitView as CountDownDokitView).resetTime()
        }
        attachMcDokitView(activity)

        //判断是否存在主入口icon
        val dokitViews = getDokitViews(activity)
        if (dokitViews[MainIconDokitView::class.java.simpleName] == null) {
            if (DoKitConstant.AWAYS_SHOW_MAIN_ICON && activity !is UniversalActivity) {
                //添加main icon
                val intent = DokitIntent(
                    MainIconDokitView::class.java
                )
                intent.mode = DokitIntent.MODE_SINGLE_INSTANCE
                DokitViewManager.instance.attach(intent)
                DoKitConstant.MAIN_ICON_HAS_SHOW = true
            }
        }
    }

    override fun onActivityPause(activity: Activity) {
        val dokitViews = getDokitViews(activity)
        for (absDokitView in dokitViews.values) {
            absDokitView.onPause()
        }
    }

    /**
     * 添加悬浮窗
     *
     * @param pageIntent
     */
    override fun attach(pageIntent: DokitIntent) {
        try {
            if (pageIntent.targetClass == null) {
                return
            }
            if (pageIntent.mode == DokitIntent.MODE_SINGLE_INSTANCE) {
                for (dokitView in mDokitViews) {
                    //如果当前page对象已经存在 则直接返回
                    if (pageIntent.targetClass.isInstance(dokitView)) {
                        return
                    }
                }
            }
            //通过newInstance方式创建floatPage
            val dokitView = pageIntent.targetClass.newInstance()
            dokitView.bundle = pageIntent.bundle
            //page.setTag(pageIntent.tag);
            //添加进page列表
            mDokitViews.add(dokitView)
            dokitView.performCreate(mContext)
            //在window上显示floatIcon
            //WindowManagerImpl具体实现
            mWindowManager.addView(
                dokitView.doKitView,
                dokitView.systemLayoutParams
            )
            dokitView.onResume()
            if (!DoKitConstant.IS_NORMAL_FLOAT_MODE) {
                for (listener in mListeners) {
                    listener.onDokitViewAdd(dokitView)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun detach(tag: String) {
        if (TextUtils.isEmpty(tag)) {
            return
        }
        val it = mDokitViews.iterator()
        while (it.hasNext()) {
            val dokitView = it.next()
            if (tag == dokitView.tag) {
                mWindowManager.removeView(dokitView.doKitView)
                dokitView.performDestroy()
                it.remove()
                return
            }
        }
    }

    override fun detach(activity: Activity, tag: String) {}
    override fun detach(dokitView: AbsDokitView) {
        detach(dokitView!!.javaClass.simpleName)
    }

    override fun detach(activity: Activity, dokitView: AbsDokitView) {}
    override fun detach(dokitViewClass: Class<out AbsDokitView>) {
        detach(dokitViewClass.simpleName)
    }

    override fun detach(activity: Activity, dokitViewClass: Class<out AbsDokitView>) {}
    override fun detachAll() {

        val it = mDokitViews.iterator()
        while (it.hasNext()) {
            val dokitView = it.next()
            mWindowManager.removeView(dokitView.doKitView)
            dokitView.performDestroy()
            it.remove()
        }
    }

    override fun getDokitView(activity: Activity, tag: String): AbsDokitView? {

        if (TextUtils.isEmpty(tag)) {
            return null
        }
        for (dokitView in mDokitViews) {
            if (tag == dokitView.tag) {
                return dokitView
            }
        }
        return null
    }

    /**
     * Activity销毁时调用 不需要实现 为了统一api
     */
    override fun onActivityDestroy(activity: Activity) {}

    /**
     * 在每一个float page创建时 添加监听器
     *
     * @param listener
     */
    fun addListener(listener: DokitViewManager.DokitViewAttachedListener) {
        mListeners.add(listener)
    }

    fun removeListener(listener: DokitViewManager.DokitViewAttachedListener) {
        mListeners.remove(listener)
    }


}