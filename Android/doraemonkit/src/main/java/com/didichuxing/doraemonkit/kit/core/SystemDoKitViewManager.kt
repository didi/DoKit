package com.didichuxing.doraemonkit.kit.core

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.DoKit.Companion.isMainIconShow
import com.didichuxing.doraemonkit.DoKit.Companion.show
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.health.CountDownDoKitView
import com.didichuxing.doraemonkit.kit.main.MainIconDoKitView
import com.didichuxing.doraemonkit.kit.toolpanel.ToolPanelDoKitView
import com.didichuxing.doraemonkit.util.DoKitSystemUtil

/**
 * Created by wanglikun on 2018/10/23.
 * 系统悬浮窗管理类
 */
internal class SystemDoKitViewManager : AbsDokitViewManager() {
    /**
     * 参考:
     * https://blog.csdn.net/awenyini/article/details/78265284
     * https://yuqirong.me/2017/09/28/Window%E6%BA%90%E7%A0%81%E8%A7%A3%E6%9E%90(%E4%B8%80)%EF%BC%9A%E4%B8%8EDecorView%E7%9A%84%E9%82%A3%E4%BA%9B%E4%BA%8B/
     */
    private val mWindowManager = DokitViewManager.instance.windowManager
    private val mContext: Context by lazy { DoKit.APPLICATION }
    private val mDoKitViews: MutableList<AbsDokitView> by lazy { mutableListOf() }
    private val mListeners: MutableList<DokitViewManager.DokitViewAttachedListener> by lazy { mutableListOf() }

    /**
     * 获取页面上所有的dokitViews
     *
     * @return map
     */
    override fun getDoKitViews(activity: Activity): Map<String, AbsDokitView> {
        val doKitViewMaps: MutableMap<String, AbsDokitView> = mutableMapOf()
        for (doKitView in mDoKitViews) {
            doKitViewMaps[doKitView.tag] = doKitView
        }
        return doKitViewMaps
    }

    /**
     * 当app进入后台时调用
     */
    override fun notifyBackground() {
        for (doKitView in mDoKitViews) {
            doKitView.onEnterBackground()
        }
    }

    /**
     * 当app进入前台时调用
     */
    override fun notifyForeground() {
        for (page in mDoKitViews) {
            page.onEnterForeground()
        }
    }

    /**
     * @param activity
     */
    override fun dispatchOnActivityResumed(activity: Activity) {
        if (activity is UniversalActivity) {
            val countDownDoKitView =
                DoKit.getDoKitView<CountDownDoKitView>(activity, CountDownDoKitView::class)
            if (countDownDoKitView != null) {
                detach(
                    CountDownDoKitView::class.tagName
                )
            }
            return
        }
        //app启动
        if (DoKitSystemUtil.isOnlyFirstLaunchActivity(activity)) {
            onMainActivityResume(activity)
        }


        DoKitManager.ACTIVITY_LIFECYCLE_INFOS[activity.javaClass.canonicalName]?.let {
            //新建Activity
            if (it.lifeCycleStatus == DoKitLifeCycleStatus.RESUME && it.isInvokeStopMethod == false) {
                onActivityResume(activity)
                return
            }

            //activity resume
            if (it.lifeCycleStatus == DoKitLifeCycleStatus.RESUME && it.isInvokeStopMethod == true) {
                onActivityBackResume(activity)
            }
        }


        //需要手动调用 生命周期回调
        val dokitViewMap = getDoKitViews(activity)
        for (absDokitView in dokitViewMap.values) {
            absDokitView.onResume()
        }
    }


    override fun attachMainIcon(activity: Activity) {
        //假如不存在全局的icon这需要全局显示主icon
        if (DoKitManager.ALWAYS_SHOW_MAIN_ICON && activity !is UniversalActivity) {
            attach(DokitIntent(MainIconDoKitView::class.java))
            DoKitManager.MAIN_ICON_HAS_SHOW = true
        } else {
            DoKitManager.MAIN_ICON_HAS_SHOW = false
        }
    }

    override fun detachMainIcon() {
        detach(MainIconDoKitView::class.tagName)
    }

    override fun attachToolPanel(activity: Activity) {
        attach(DokitIntent(ToolPanelDoKitView::class.java))
    }

    override fun detachToolPanel() {
        detach(ToolPanelDoKitView::class.tagName)
    }

    override fun onMainActivityResume(activity: Activity) {
        attachMainIcon(activity)
        //倒计时DokitView
        attachCountDownDoKitView(activity)
        attachMcRecodingDoKitView(activity)

//        if (SPUtils.getInstance().getBoolean(DoKitConstant.MC_CASE_RECODING_KEY, false)) {
//            val action: Map<String, String> = mapOf("action" to "launch_recoding_view")
//            DoKitConstant.getModuleProcessor(DoKitModule.MODULE_MC)?.proceed(action)
//        }
    }

    override fun onActivityResume(activity: Activity) {
        //判断是否有MainIcon
        if (DoKitManager.ALWAYS_SHOW_MAIN_ICON && !isMainIconShow) {
            show()
        }
        //如果倒计时浮标没显示则重新添加
        val countDownDokitView =
            DoKit.getDoKitView<CountDownDoKitView>(activity, CountDownDoKitView::class)
        if (countDownDokitView == null) {
            if (activity is UniversalActivity) {
                return
            }
            attachCountDownDoKitView(activity)
        } else {
            if (activity is UniversalActivity) {
                detach(CountDownDoKitView::class.tagName)
            } else {
                //重置倒计时
                (countDownDokitView as CountDownDoKitView).reset()
            }
        }
    }

    override fun onActivityBackResume(activity: Activity) {
        //移除倒计时浮标
        val countDownDokitView =
            DoKit.getDoKitView<CountDownDoKitView>(activity, CountDownDoKitView::class)
        if (countDownDokitView == null) {
            attachCountDownDoKitView(activity)
        } else {
            //重置倒计时
            (countDownDokitView as CountDownDoKitView).reset()
        }

        //判断是否存在主入口icon
        val dokitViews = getDoKitViews(activity)
        if (dokitViews[MainIconDoKitView::class.tagName] == null) {
            if (DoKitManager.ALWAYS_SHOW_MAIN_ICON && activity !is UniversalActivity) {
                //添加main icon
                val intent = DokitIntent(MainIconDoKitView::class.java)
                attach(intent)
                DoKitManager.MAIN_ICON_HAS_SHOW = true
            }
        }
    }

    override fun onActivityPaused(activity: Activity) {
        val dokitViews = getDoKitViews(activity)
        for (absDokitView in dokitViews.values) {
            absDokitView.onPause()
        }
    }

    override fun onActivityStopped(activity: Activity) {
    }

    /**
     * 添加悬浮窗
     *
     * @param pageIntent
     */
    override fun attach(pageIntent: DokitIntent) {
        try {

            for (dokitView in mDoKitViews) {
                //如果当前page对象已经存在 则直接返回
                if (pageIntent.targetClass.isInstance(dokitView)) {
                    return
                }
            }
            //通过newInstance方式创建floatPage
            val dokitView = pageIntent.targetClass.newInstance()
            dokitView.bundle = pageIntent.bundle
            //page.setTag(pageIntent.tag);
            //添加进page列表
            mDoKitViews.add(dokitView)
            dokitView.performCreate(mContext)
            //在window上显示floatIcon
            //WindowManagerImpl具体实现
            mWindowManager.addView(
                dokitView.doKitView,
                dokitView.systemLayoutParams
            )
            dokitView.onResume()
            if (!DoKitManager.IS_NORMAL_FLOAT_MODE) {
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
        val it = mDoKitViews.iterator()
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


    override fun detach(dokitView: AbsDokitView) {
        detach(dokitView.tagName)
    }


    override fun detach(doKitViewClass: Class<out AbsDokitView>) {
        detach(doKitViewClass.tagName)
    }


    override fun detachAll() {

        val it = mDoKitViews.iterator()
        while (it.hasNext()) {
            val doKitView = it.next()
            mWindowManager.removeView(doKitView.doKitView)
            doKitView.performDestroy()
            it.remove()
        }
    }

    override fun <T : AbsDokitView> getDoKitView(activity: Activity, clazz: Class<T>): AbsDokitView? {
        if (TextUtils.isEmpty(clazz.tagName)) {
            return null
        }
        for (dokitView in mDoKitViews) {
            if (clazz.tagName == dokitView.tag) {
                return dokitView
            }
        }
        return null
    }




    /**
     * Activity销毁时调用 不需要实现 为了统一api
     */
    override fun onActivityDestroyed(activity: Activity) {}

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