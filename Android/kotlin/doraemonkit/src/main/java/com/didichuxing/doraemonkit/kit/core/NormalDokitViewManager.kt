package com.didichuxing.doraemonkit.kit.core

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.blankj.utilcode.util.BarUtils
import com.didichuxing.doraemonkit.DoraemonKit.isShow
import com.didichuxing.doraemonkit.DoraemonKit.show
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.kit.health.CountDownDokitView
import com.didichuxing.doraemonkit.kit.main.MainIconDokitView
import com.didichuxing.doraemonkit.kit.performance.PerformanceDokitView
import com.didichuxing.doraemonkit.model.ActivityLifecycleInfo
import com.didichuxing.doraemonkit.util.LogHelper
import com.didichuxing.doraemonkit.util.SystemUtil
import java.lang.ref.WeakReference

/**
 * Created by jint on 2018/10/23.
 * 每个activity悬浮窗管理类
 */
internal class NormalDokitViewManager(val mContext: Context) : DokitViewManagerInterface {
    /**
     * 每个Activity中dokitView的集合
     */
    private val mActivityDokitViews: MutableMap<Activity, MutableMap<String, AbsDokitView?>> by lazy {
        mutableMapOf<Activity, MutableMap<String, AbsDokitView?>>()
    }

    /**
     * 全局的同步mActivityFloatDokitViews 应该在页面上显示的dokitView集合
     */
    private val mGlobalSingleDokitViews: MutableMap<String, GlobalSingleDokitViewInfo> by lazy {
        mutableMapOf<String, GlobalSingleDokitViewInfo>()
    }

    /**
     * 当app进入后台时调用
     */
    override fun notifyBackground() {
        //双层遍历
        mActivityDokitViews.values.forEach { map ->
            map.values.forEach { dokitView ->
                dokitView?.onEnterBackground()
            }
        }

    }

    /**
     * 当app进入前台时调用
     */
    override fun notifyForeground() {
        //双层遍历
        mActivityDokitViews.values.forEach { map ->
            map.values.forEach { dokitView ->
                dokitView?.onEnterForeground()
            }
        }

    }

    /**
     * 添加activity关联的所有dokitView activity resume的时候回调
     *
     * @param activity
     */
    override fun resumeAndAttachDokitViews(activity: Activity?) {
        activity?.let {
            //app启动
            if (SystemUtil.isOnlyFirstLaunchActivity(it)) {
                onMainActivityCreate(it)
                return
            }

            val activityLifecycleInfo = DokitConstant.ACTIVITY_LIFECYCLE_INFOS[it.javaClass.canonicalName]
                    ?: return
            //新建Activity
            if (activityLifecycleInfo.activityLifeCycleCount == ActivityLifecycleInfo.ACTIVITY_LIFECYCLE_CREATE2RESUME) {
                onActivityCreate(it)
                return
            }

            //activity resume
            if (activityLifecycleInfo.activityLifeCycleCount > ActivityLifecycleInfo.ACTIVITY_LIFECYCLE_CREATE2RESUME) {
                onActivityResume(it)
            }
        }


    }

    /**
     * 应用启动
     */
    override fun onMainActivityCreate(activity: Activity?) {
        if (activity is UniversalActivity) {
            return
        }
        //倒计时DokitView
        attachCountDownDokitView(activity)
        if (!DokitConstant.AWAYS_SHOW_MAIN_ICON) {
            return
        }
        val dokitIntent = DokitIntent(MainIconDokitView::class.java)
        attach(dokitIntent)
    }

    /**
     * 新建activity
     *
     * @param activity
     */
    override fun onActivityCreate(activity: Activity?) {

        //将所有的dokitView添加到新建的Activity中去

        for (dokitViewInfo in mGlobalSingleDokitViews.values) {
            if (activity is UniversalActivity && dokitViewInfo.absDokitViewClass != PerformanceDokitView::class.java) {
                continue
            }
            //是否过滤掉 入口icon
            if (!DokitConstant.AWAYS_SHOW_MAIN_ICON && dokitViewInfo.absDokitViewClass == MainIconDokitView::class.java) {
                continue
            }

            val dokitIntent = DokitIntent(dokitViewInfo.absDokitViewClass)
            dokitIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE
            dokitIntent.bundle = dokitViewInfo.bundle
            attach(dokitIntent)
        }
        //判断是否有MainIcon
        if (DokitConstant.AWAYS_SHOW_MAIN_ICON && !isShow) {
            show()
        }

        activity?.let {
            //倒计时DokitView
            attachCountDownDokitView(it)
        }

    }

    /**
     * activity onResume
     *
     * @param activity
     */
    override fun onActivityResume(activity: Activity?) {

        val existDokitViews: MutableMap<String, AbsDokitView?>? = mActivityDokitViews[activity]
        //先清除页面上启动模式为DokitIntent.MODE_ONCE 的dokitView

        existDokitViews?.let {
            //千万注意不要使用for循环去移除对象 下面注释的这段代码存在bug
//            for (AbsDokitView existDokitView : existDokitViews.values()) {
//                if (existDokitView.getMode() == DokitIntent.MODE_ONCE) {
//                    detach(existDokitView.getClass());
//                }
//            }
            val modeOnceDokitViews: MutableList<String> = mutableListOf()
            existDokitViews.values.forEach { existDokitView ->
                if (existDokitView?.mode == DokitIntent.MODE_ONCE) {
                    modeOnceDokitViews.add(existDokitView.javaClass.simpleName)
                }
            }

            modeOnceDokitViews.forEach { tag ->
                detach(tag)
            }
        }


        //更新所有全局DokitView的位置
        if (mGlobalSingleDokitViews.isNotEmpty()) {
            for (globalSingleDokitViewInfo in mGlobalSingleDokitViews.values) {
                if (activity is UniversalActivity && globalSingleDokitViewInfo.absDokitViewClass != PerformanceDokitView::class.java) {
                    continue
                }
                //是否过滤掉 入口icon
                if (!DokitConstant.AWAYS_SHOW_MAIN_ICON && globalSingleDokitViewInfo.absDokitViewClass == MainIconDokitView::class.java) {
                    continue
                }

                //LogHelper.i(TAG, " activity  resume==>" + activity.getClass().getSimpleName() + "  dokitView==>" + globalSingleDokitViewInfo.getTag());
                //判断resume Activity 中时候存在指定的dokitview
                var existDokitView: AbsDokitView? = null
                if (existDokitViews != null && existDokitViews.isNotEmpty()) {
                    existDokitView = existDokitViews[globalSingleDokitViewInfo.tag]
                }


                //当前页面已存在dokitview
                if (existDokitView?.rootView != null) {
                    existDokitView.rootView!!.visibility = View.VISIBLE
                    //更新位置
                    existDokitView.updateViewLayout(existDokitView.tag, true)
                    existDokitView.onResume()
                } else {
                    //添加相应的
                    val dokitIntent = DokitIntent(globalSingleDokitViewInfo.absDokitViewClass)
                    dokitIntent.mode = globalSingleDokitViewInfo.mode
                    dokitIntent.bundle = globalSingleDokitViewInfo.bundle
                    attach(dokitIntent)
                }
            }
            if (!mGlobalSingleDokitViews.containsKey(MainIconDokitView::class.java.simpleName)) {
                attachMainIconDokitView(activity)
            }
        } else {
            //假如不存在全局的icon这需要全局显示主icon
            attachMainIconDokitView(activity)
        }
        attachCountDownDokitView(activity)
    }

    private fun attachMainIconDokitView(activity: Activity?) {
        //假如不存在全局的icon这需要全局显示主icon
        if (DokitConstant.AWAYS_SHOW_MAIN_ICON && activity !is UniversalActivity) {
            val dokitIntent = DokitIntent(MainIconDokitView::class.java)
            dokitIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE
            attach(dokitIntent)
        }
    }

    override fun onActivityPause(activity: Activity?) {
        val dokitViews: Map<String, AbsDokitView?> = getDokitViews(activity)
        for (absDokitView in dokitViews.values) {
            absDokitView!!.onPause()
        }
    }

    /**
     * 添加倒计时DokitView
     */
    private fun attachCountDownDokitView(activity: Activity?) {
        if (!DokitConstant.APP_HEALTH_RUNNING) {
            return
        }
        if (activity is UniversalActivity) {
            return
        }
        val dokitIntent = DokitIntent(CountDownDokitView::class.java)
        dokitIntent.mode = DokitIntent.MODE_ONCE
        attach(dokitIntent)
    }

    /**
     * 在当前Activity中添加指定悬浮窗
     *
     * @param dokitIntent
     */
    override fun attach(dokitIntent: DokitIntent?) {
        try {
            if (dokitIntent?.activity == null) {
                LogHelper.e(TAG, "activity = null")
                return
            }

            //通过newInstance方式创建floatPage
            val dokitView = dokitIntent.targetClass.newInstance()!!
            //判断当前Activity是否存在dokitView map
            val dokitViews: MutableMap<String, AbsDokitView?>?
            if (mActivityDokitViews[dokitIntent.activity] == null) {
                dokitViews = mutableMapOf()
                mActivityDokitViews[dokitIntent.activity] = dokitViews
            } else {
                dokitViews = mActivityDokitViews[dokitIntent.activity]
            }
            //判断该dokitview是否已经显示在页面上 同一个类型的dokitview 在页面上只显示一个
            if (dokitIntent.mode == DokitIntent.MODE_SINGLE_INSTANCE) {
                if (dokitViews!![dokitIntent.tag] != null) {
                    //拿到指定的dokitView并更新位置
                    dokitViews[dokitIntent.tag]!!.updateViewLayout(dokitIntent.tag, true)
                    return
                }
            }

            //在当前Activity中保存dokitView
            dokitViews!![dokitView.tag] = dokitView
            //设置dokitview的属性
            dokitView.mode = dokitIntent.mode
            dokitView.bundle = dokitIntent.bundle
            dokitView.tag = dokitIntent.tag
            dokitView.attachActivity = WeakReference(dokitIntent.activity)
            dokitView.performCreate(mContext)
            //在全局dokitviews中保存该类型的
            if (dokitIntent.mode == DokitIntent.MODE_SINGLE_INSTANCE) {
                mGlobalSingleDokitViews[dokitView.tag] = createGlobalSingleDokitViewInfo(dokitView)
            }
            //得到activity window中的根布局
            val mDecorView = dokitIntent.activity.window.decorView as FrameLayout


            //往DecorView的子RootView中添加dokitView
            if (dokitView.rootView != null) {
                getDokitRootContentView(dokitIntent.activity, mDecorView)
                        .addView(dokitView.rootView,
                                dokitView.normalLayoutParams)
                //延迟100毫秒调用
                dokitView.postDelayed(100, Runnable {
                    dokitView.onResume()
                    //操作DecorRootView
                    dokitView.dealDecorRootView(getDokitRootContentView(dokitIntent.activity, mDecorView))
                })

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    //    private static final String DOKIT_ROOT_VIEW_TAG = "DokitRootView";
    /**
     * @return rootView
     */
    private fun getDokitRootContentView(activity: Activity?, decorView: FrameLayout): FrameLayout {
        var dokitRootView = decorView.findViewById<FrameLayout>(R.id.dokit_contentview_id)
        if (dokitRootView != null) {
            return dokitRootView
        }
        dokitRootView = DokitFrameLayout(mContext)
        //普通模式的返回按键监听
        dokitRootView.setOnKeyListener(View.OnKeyListener { v, keyCode, event -> //LogHelper.i(TAG, "keyCode===>" + keyCode + " " + v.getClass().getSimpleName());
            //监听返回键
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                val dokitViews: MutableMap<String, AbsDokitView?> = getDokitViews(activity)
                if (dokitViews.isEmpty()) {
                    return@OnKeyListener false
                }
                for (dokitView in dokitViews.values) {
                    if (dokitView!!.shouldDealBackKey()) {
                        return@OnKeyListener dokitView.onBackPressed()
                    }
                }
                return@OnKeyListener false
            }
            return@OnKeyListener false
        })
        dokitRootView.setClipChildren(false)
        //解决无法获取返回按键的问题
        dokitRootView.setFocusable(true)
        dokitRootView.setFocusableInTouchMode(true)
        dokitRootView.requestFocus()
        dokitRootView.setId(R.id.dokit_contentview_id)
        val dokitParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        try {
            //解决由于项目集成SwipeBackLayout而出现的dokit入口不显示
            if (BarUtils.isStatusBarVisible(activity!!)) {
                dokitParams.topMargin = BarUtils.getStatusBarHeight()
            }
            if (BarUtils.isSupportNavBar()) {
                if (BarUtils.isNavBarVisible(activity)) {
                    dokitParams.bottomMargin = BarUtils.getNavBarHeight()
                }
            }
        } catch (e: Exception) {
            //e.printStackTrace();
        }
        dokitRootView.setLayoutParams(dokitParams)
        //添加到DecorView中 为了不和用户自己往根布局中添加view干扰
        decorView.addView(dokitRootView)
        return dokitRootView
    }

    /**
     * 移除每个activity指定的dokitView
     */
    override fun detach(dokitView: AbsDokitView?) {
        //调用当前Activity的指定dokitView的Destroy方法
        //dokitView.performDestroy();
        dokitView?.let {
            detach(dokitView.tag)
        }
    }

    override fun detach(activity: Activity?, dokitView: AbsDokitView?) {
        dokitView?.let {
            detach(activity, dokitView.tag)
        }
    }

    /**
     * 根据tag 移除ui和列表中的数据
     *
     * @param tag
     */
    override fun detach(tag: String?) {

        //移除每个activity中指定的dokitView
        for (activityKey in mActivityDokitViews.keys) {
            val dokitViews = mActivityDokitViews[activityKey] ?: continue
            //定位到指定dokitView
            val dokitView = dokitViews[tag] ?: continue
            dokitView.rootView?.let { rootView ->
                rootView.visibility = View.GONE
                getDokitRootContentView(dokitView.activity, activityKey.window.decorView as FrameLayout).removeView(dokitView.rootView)
            }


            //移除指定UI
            //请求重新绘制
            activityKey.window.decorView.requestLayout()
            //执行dokitView的销毁
            dokitView.performDestroy()
            //移除map中的数据
            dokitViews.remove(tag)
        }
        //同步移除全局指定类型的dokitView
        if (mGlobalSingleDokitViews.containsKey(tag)) {
            mGlobalSingleDokitViews.remove(tag)
        }
    }

    override fun detach(activity: Activity?, tag: String?) {
        if (activity == null) {
            return
        }


        val dokitViews = mActivityDokitViews[activity] ?: return
        //定位到指定dokitView
        val dokitView = dokitViews[tag] ?: return
        dokitView.rootView?.let {
            it.visibility = View.GONE
            getDokitRootContentView(dokitView.activity, activity.window.decorView as FrameLayout).removeView(dokitView.rootView)
        }

        //移除指定UI
        //请求重新绘制
        activity.window.decorView.requestLayout()
        //执行dokitView的销毁
        dokitView.performDestroy()
        //移除map中的数据
        dokitViews.remove(tag)
        if (mGlobalSingleDokitViews.containsKey(tag)) {
            mGlobalSingleDokitViews.remove(tag)
        }
    }

    override fun detach(dokitViewClass: Class<out AbsDokitView?>?) {
        detach(dokitViewClass!!.simpleName)
    }

    override fun detach(activity: Activity?, dokitViewClass: Class<out AbsDokitView?>?) {
        detach(activity, dokitViewClass!!.simpleName)
    }

    /**
     * 移除所有activity的所有dokitView
     */
    override fun detachAll() {

        //移除每个activity中所有的dokitView
        for (activityKey in mActivityDokitViews.keys) {
            val dokitViews = mActivityDokitViews[activityKey]
            //移除指定UI
            getDokitRootContentView(activityKey, activityKey?.window?.decorView as FrameLayout).removeAllViews()
            //移除map中的数据
            dokitViews?.let {
                dokitViews.clear()
            }
        }
        mGlobalSingleDokitViews.clear()
    }

    /**
     * Activity销毁时调用
     */
    override fun onActivityDestroy(activity: Activity?) {

        val dokitViewMap = getDokitViews(activity)
        for (dokitView in dokitViewMap.values) {
            dokitView?.performDestroy()
        }
        mActivityDokitViews.remove(activity)
    }

    /**
     * 获取当前页面指定的dokitView
     *
     * @param activity
     * @param tag
     * @return
     */
    override fun getDokitView(activity: Activity?, tag: String?): AbsDokitView? {
        if (TextUtils.isEmpty(tag) || activity == null) {
            return null
        }

        if (mActivityDokitViews[activity] == null) {
            return null
        } else {
            mActivityDokitViews[activity]?.let {
                return it[tag]
            }
            return null
        }

    }

    /**
     * 获取当前页面所有的dokitView
     *
     * @param activity
     * @return
     */
    override fun getDokitViews(activity: Activity?): MutableMap<String, AbsDokitView?> {
        if (activity == null) {
            return mutableMapOf()
        }
        if (mActivityDokitViews[activity] != null) {
            return mActivityDokitViews[activity]!!
        }

        return mutableMapOf()

    }

    private fun createGlobalSingleDokitViewInfo(dokitView: AbsDokitView): GlobalSingleDokitViewInfo {
        return GlobalSingleDokitViewInfo(dokitView.javaClass, dokitView.tag, DokitIntent.MODE_SINGLE_INSTANCE, dokitView.bundle)
    }

    companion object {
        private const val TAG = "NormalDokitViewManager"
    }

}