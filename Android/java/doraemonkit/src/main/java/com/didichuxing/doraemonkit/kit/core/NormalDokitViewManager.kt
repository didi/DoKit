package com.didichuxing.doraemonkit.kit.core

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.DoKit.Companion.isMainIconShow
import com.didichuxing.doraemonkit.DoKit.Companion.show
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.constant.DoKitConstant.WS_MODE
import com.didichuxing.doraemonkit.constant.DoKitConstant.getModuleProcessor
import com.didichuxing.doraemonkit.constant.DoKitModule
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.main.MainIconDokitView
import com.didichuxing.doraemonkit.kit.performance.PerformanceDokitView
import com.didichuxing.doraemonkit.model.ActivityLifecycleInfo
import com.didichuxing.doraemonkit.util.*
import java.util.*

/**
 * Created by jintai on 2018/10/23.
 * 每个activity悬浮窗管理类
 */
internal class NormalDokitViewManager : AbsDokitViewManager() {
    /**
     * 每个Activity中dokitView的集合
     */
    private val mActivityDoKitViews: MutableMap<Activity, MutableMap<String, AbsDokitView>?> by lazy {
        mutableMapOf()
    }

    /**
     * 全局的同步mActivityFloatDokitViews 应该在页面上显示的dokitView集合
     */
    private val mGlobalSingleDoKitViews: MutableMap<String, GlobalSingleDokitViewInfo> by lazy {
        mutableMapOf()
    }

    private val mContext: Context by lazy { DoKit.APPLICATION }


    /**
     * 当app进入后台时调用
     */
    override fun notifyBackground() {
        mActivityDoKitViews.forEach { maps ->
            maps.value?.forEach { map ->
                map.value.onEnterBackground()
            }
        }


    }

    /**
     * 当app进入前台时调用
     */
    override fun notifyForeground() {

        mActivityDoKitViews.forEach { maps ->
            maps.value?.forEach { map ->
                map.value.onEnterForeground()
            }
        }
    }

    /**
     * 添加activity关联的所有dokitView activity resume的时候回调
     *
     * @param activity
     */
    override fun resumeAndAttachDokitViews(activity: Activity) {

        //app启动
        if (DoKitSystemUtil.isOnlyFirstLaunchActivity(activity)) {
            onMainActivityCreate(activity)
            return
        }
        val activityLifecycleInfo =
            DoKitConstant.ACTIVITY_LIFECYCLE_INFOS[activity.javaClass.canonicalName]
                ?: return
        //新建Activity
        if (activityLifecycleInfo.activityLifeCycleCount == ActivityLifecycleInfo.ACTIVITY_LIFECYCLE_CREATE2RESUME) {
            onActivityCreate(activity)
            return
        }

        //activity resume
        if (activityLifecycleInfo.activityLifeCycleCount > ActivityLifecycleInfo.ACTIVITY_LIFECYCLE_CREATE2RESUME) {
            onActivityResume(activity)
        }

        //判断悬浮窗是否可见
    }

    /**
     * 应用启动
     */
    override fun onMainActivityCreate(activity: Activity) {
        if (activity is UniversalActivity) {
            return
        }
        //倒计时DokitView
        attachCountDownDokitView(activity)
        //启动一机多控悬浮窗
        attachMcDokitView(activity)
        if (!DoKitConstant.AWAYS_SHOW_MAIN_ICON) {
            DoKitConstant.MAIN_ICON_HAS_SHOW = false
            return
        }
        val dokitIntent = DokitIntent(
            MainIconDokitView::class.java
        )
        dokitIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE
        attach(dokitIntent)
        DoKitConstant.MAIN_ICON_HAS_SHOW = true

        //添加录制中的悬浮窗
        LogHelper.i(
            TAG,
            "====onMainActivityCreate===" + SPUtils.getInstance()
                .getBoolean(DoKitConstant.MC_CASE_RECODING_KEY, false)
        )
        if (SPUtils.getInstance().getBoolean(DoKitConstant.MC_CASE_RECODING_KEY, false)) {
            val action: MutableMap<String, String> = HashMap()
            action["action"] = "launch_recoding_view"
            getModuleProcessor(DoKitModule.MODULE_MC)!!.proceed(action)
        }
    }

    /**
     * 新建activity
     *
     * @param activity
     */
    override fun onActivityCreate(activity: Activity) {


        //将所有的dokitView添加到新建的Activity中去
        for (dokitViewInfo: GlobalSingleDokitViewInfo in mGlobalSingleDoKitViews.values) {
            //如果不是性能kitView 则不显示
            if (activity is UniversalActivity && dokitViewInfo.absDokitViewClass != PerformanceDokitView::class.java) {
                continue
            }
            //是否过滤掉 入口icon
            if (!DoKitConstant.AWAYS_SHOW_MAIN_ICON && dokitViewInfo.absDokitViewClass == MainIconDokitView::class.java) {
                DoKitConstant.MAIN_ICON_HAS_SHOW = false
                continue
            }
            if (dokitViewInfo.absDokitViewClass == MainIconDokitView::class.java) {
                DoKitConstant.MAIN_ICON_HAS_SHOW = true
            }
            val dokitIntent = DokitIntent(dokitViewInfo.absDokitViewClass)
            dokitIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE
            dokitIntent.bundle = dokitViewInfo.bundle
            attach(dokitIntent)
        }
        //判断是否有MainIcon
        if (DoKitConstant.AWAYS_SHOW_MAIN_ICON && !isMainIconShow) {
            show()
        }

        //倒计时DokitView
        attachCountDownDokitView(activity)
        //启动一机多控悬浮窗
        attachMcDokitView(activity)
    }

    /**
     * activity onResume
     *
     * @param activity
     */
    override fun onActivityResume(activity: Activity) {

        val existDokitViews: Map<String, AbsDokitView>? = mActivityDoKitViews[activity]

        existDokitViews?.let {
            val modeOnceDokitViews: MutableList<String> = ArrayList()
            for (existDokitView: AbsDokitView in existDokitViews.values) {
                if (existDokitView.mode == DokitIntent.MODE_ONCE) {
                    modeOnceDokitViews.add(existDokitView.javaClass.simpleName)
                }
            }
            for (tag: String in modeOnceDokitViews) {
                detach(tag)
            }
        }


        //更新所有全局DokitView的位置
        if (mGlobalSingleDoKitViews.isNotEmpty()) {
            for (globalSingleDokitViewInfo: GlobalSingleDokitViewInfo in mGlobalSingleDoKitViews.values) {
                //如果不是性能kitView 则需要重新更新位置
                if (activity is UniversalActivity && globalSingleDokitViewInfo.absDokitViewClass != PerformanceDokitView::class.java) {
                    continue
                }
                //是否过滤掉 入口icon
                if (!DoKitConstant.AWAYS_SHOW_MAIN_ICON && globalSingleDokitViewInfo.absDokitViewClass == MainIconDokitView::class.java) {
                    DoKitConstant.MAIN_ICON_HAS_SHOW = false
                    continue
                }
                if (globalSingleDokitViewInfo.absDokitViewClass == MainIconDokitView::class.java) {
                    DoKitConstant.MAIN_ICON_HAS_SHOW = true
                }

                //LogHelper.i(TAG, " activity  resume==>" + activity.getClass().getSimpleName() + "  dokitView==>" + globalSingleDokitViewInfo.getTag());
                //判断resume Activity 中时候存在指定的dokitview
                var existDokitView: AbsDokitView? = null
                if (existDokitViews != null && existDokitViews.isNotEmpty()) {
                    existDokitView = existDokitViews[globalSingleDokitViewInfo.tag]
                }

                //当前页面已存在dokitview
                if (existDokitView?.doKitView != null) {
                    existDokitView.doKitView!!.visibility = View.VISIBLE
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
            if (!mGlobalSingleDoKitViews.containsKey(MainIconDokitView::class.java.simpleName)) {
                attachMainIconDokitView(activity)
            }
        } else {
            //假如不存在全局的icon这需要全局显示主icon
            attachMainIconDokitView(activity)
        }
        attachCountDownDokitView(activity)
        //启动一机多控悬浮窗
        attachMcDokitView(activity)
    }

    private fun attachMainIconDokitView(activity: Activity) {
        //假如不存在全局的icon这需要全局显示主icon
        if (DoKitConstant.AWAYS_SHOW_MAIN_ICON && activity !is UniversalActivity) {
            val dokitIntent = DokitIntent(
                MainIconDokitView::class.java
            )
            dokitIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE
            attach(dokitIntent)
        }
    }

    override fun onActivityPause(activity: Activity) {
        val dokitViews = getDokitViews(activity)
        dokitViews?.let {
            for (absDokitView: AbsDokitView in it.values) {
                absDokitView.onPause()
            }
        }

    }

    /**
     * 在当前Activity中添加指定悬浮窗
     *
     * @param dokitIntent
     */
    override fun attach(dokitIntent: DokitIntent) {
        try {
            if (dokitIntent.activity == null) {
                LogHelper.e(TAG, "activity = null")
                return
            }
            if (dokitIntent.targetClass == null) {
                return
            }


            //通过newInstance方式创建floatPage
            val dokitView = dokitIntent.targetClass.newInstance()
            //判断当前Activity是否存在dokitView map
            val dokitViews: MutableMap<String, AbsDokitView>?
            if (mActivityDoKitViews[dokitIntent.activity] == null) {
                dokitViews = HashMap()
                mActivityDoKitViews[dokitIntent.activity] = dokitViews
            } else {
                dokitViews = mActivityDoKitViews[dokitIntent.activity]
            }
            if (dokitViews == null) {
                return
            }

            //判断该dokitview是否已经显示在页面上 同一个类型的dokitview 在页面上只显示一个
            if (dokitIntent.mode == DokitIntent.MODE_SINGLE_INSTANCE) {
                if (dokitViews[dokitIntent.tag] != null) {
                    //拿到指定的dokitView并更新位置
                    dokitViews[dokitIntent.tag]?.updateViewLayout(dokitIntent.tag, true)
                    return
                }
            }
            //在当前Activity中保存dokitView
            dokitViews[dokitView.tag] = dokitView
            //设置dokitview的属性
            dokitView.mode = dokitIntent.mode
            dokitView.bundle = dokitIntent.bundle
            dokitView.tag = dokitIntent.tag
            dokitView.setActivity(dokitIntent.activity)
            dokitView.performCreate(mContext)
            //在全局dokitviews中保存该类型的
            if (dokitIntent.mode == DokitIntent.MODE_SINGLE_INSTANCE) {
                mGlobalSingleDoKitViews[dokitView.tag] = createGlobalSingleDokitViewInfo(dokitView)
            }
            //得到activity window中的根布局
            //final ViewGroup mDecorView = getDecorView(dokitIntent.activity);

            //往DecorView的子RootView中添加dokitView
            if (dokitView.normalLayoutParams != null && dokitView.doKitView != null) {
                getDoKitRootContentView(dokitIntent.activity)
                    .addView(
                        dokitView.doKitView,
                        dokitView.normalLayoutParams
                    )
                //延迟100毫秒调用
                dokitView.postDelayed({
                    dokitView.onResume()
                    //操作DecorRootView
                    dokitView.dealDecorRootView(getDoKitRootContentView(dokitIntent.activity))
                }, MC_DELAY.toLong())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    //    private static final String DOKIT_ROOT_VIEW_TAG = "DokitRootView";
    /**
     * @return rootView
     */
    private fun getDoKitRootContentView(activity: Activity): FrameLayout {
        val decorView = getDecorView(activity)
        var dokitRootView = decorView.findViewById<FrameLayout>(R.id.dokit_contentview_id)
        if (dokitRootView != null) {
            return dokitRootView
        }
        dokitRootView = DokitFrameLayout(mContext, DokitFrameLayout.DoKitFrameLayoutFlag_ROOT)
        //普通模式的返回按键监听
        dokitRootView.setOnKeyListener(View.OnKeyListener { v, keyCode, event -> //LogHelper.i(TAG, "keyCode===>" + keyCode + " " + v.getClass().getSimpleName());
            //监听返回键
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                val dokitViews: Map<String, AbsDokitView>? = getDokitViews(activity)
                if (dokitViews == null || dokitViews.isEmpty()) {
                    return@OnKeyListener false
                }
                for (dokitView: AbsDokitView in dokitViews.values) {
                    if (dokitView.shouldDealBackKey()) {
                        return@OnKeyListener dokitView.onBackPressed()
                    }
                }
                return@OnKeyListener false
            }
            false
        })
        dokitRootView.setClipChildren(false)
        dokitRootView.setClipToPadding(false)

        //解决无法获取返回按键的问题
        dokitRootView.setFocusable(true)
        dokitRootView.setFocusableInTouchMode(true)
        dokitRootView.requestFocus()
        dokitRootView.setId(R.id.dokit_contentview_id)
        val dokitParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        try {
            //解决由于项目集成SwipeBackLayout而出现的dokit入口不显示
            if (BarUtils.isStatusBarVisible((activity))) {
                dokitParams.topMargin = BarUtils.getStatusBarHeight()
            }
            if (BarUtils.isSupportNavBar()) {
                if (BarUtils.isNavBarVisible((activity))) {
                    dokitParams.bottomMargin = BarUtils.getNavBarHeight()
                }
            }
        } catch (e: Exception) {
            //e.printStackTrace();
        }
        dokitParams.gravity = Gravity.BOTTOM
        dokitRootView.setLayoutParams(dokitParams)
        //添加到DecorView中 为了不和用户自己往根布局中添加view干扰
        decorView.addView(dokitRootView)
        return dokitRootView
    }

    /**
     * 移除每个activity指定的dokitView
     */
    override fun detach(dokitView: AbsDokitView) {
        //调用当前Activity的指定dokitView的Destroy方法
        //dokitView.performDestroy();
        detach(dokitView.tag)
    }

    override fun detach(activity: Activity, dokitView: AbsDokitView) {
        detach(activity, dokitView.tag)
    }

    /**
     * 根据tag 移除ui和列表中的数据
     *
     * @param tag
     */
    override fun detach(tag: String) {

        if (WS_MODE === WSMode.HOST) {
            getDecorView(ActivityUtils.getTopActivity()).postDelayed(
                Runnable { realDetach(tag) }, MC_DELAY.toLong()
            )
        } else {
            realDetach(tag)
        }
    }

    private fun realDetach(tag: String) {
        //移除每个activity中指定的dokitView
        for (activityKey: Activity in mActivityDoKitViews.keys) {
            val dokitViews = mActivityDoKitViews[activityKey]
            //定位到指定dokitView
            val dokitView = dokitViews!!.get(tag) ?: continue
            if (dokitView.doKitView != null) {
                dokitView.doKitView!!.visibility = View.GONE
                getDoKitRootContentView(dokitView.activity).removeView(dokitView.doKitView)
            }

            //移除指定UI
            //请求重新绘制
            getDecorView(activityKey).requestLayout()
            //执行dokitView的销毁
            dokitView.performDestroy()
            //移除map中的数据
            dokitViews.remove(tag)
        }
        //同步移除全局指定类型的dokitView
        if (mGlobalSingleDoKitViews.containsKey(tag)) {
            mGlobalSingleDoKitViews.remove(tag)
        }
    }

    override fun detach(activity: Activity, tag: String) {
        detach(tag)
    }

    override fun detach(dokitViewClass: Class<out AbsDokitView>) {
        detach(dokitViewClass.simpleName)
    }

    override fun detach(activity: Activity, dokitViewClass: Class<out AbsDokitView>) {
        detach(activity, dokitViewClass.simpleName)
    }

    /**
     * 移除所有activity的所有dokitView
     */
    override fun detachAll() {

        //移除每个activity中所有的dokitView
        for (activityKey: Activity in mActivityDoKitViews.keys) {
            val dokitViews = mActivityDoKitViews[activityKey]
            //移除指定UI
            getDoKitRootContentView(activityKey).removeAllViews()
            //移除map中的数据
            dokitViews!!.clear()
        }
        mGlobalSingleDoKitViews.clear()
    }

    /**
     * Activity销毁时调用
     */
    override fun onActivityDestroy(activity: Activity) {

        //移除dokit根布局
        val dokitRootView = activity!!.findViewById<View>(R.id.dokit_contentview_id)
        if (dokitRootView != null) {
            getDecorView(activity).removeView(dokitRootView)
        }
        val dokitViewMap: Map<String, AbsDokitView> = getDokitViews(activity) ?: return
        for (dokitView: AbsDokitView in dokitViewMap.values) {
            dokitView.performDestroy()
        }
        mActivityDoKitViews.remove(activity)
    }

    /**
     * 获取页面根布局
     *
     * @param activity
     * @return
     */
    private fun getDecorView(activity: Activity): ViewGroup {
        return activity.window.decorView as ViewGroup
    }

    /**
     * 获取当前页面指定的dokitView
     *
     * @param activity
     * @param tag
     * @return
     */
    override fun getDokitView(activity: Activity, tag: String): AbsDokitView? {
        if (TextUtils.isEmpty(tag)) {
            return null
        }

        return if (mActivityDoKitViews[activity] == null) {
            null
        } else mActivityDoKitViews[activity]?.get(tag)
    }

    /**
     * 获取当前页面所有的dokitView
     *
     * @param activity
     * @return
     */
    override fun getDokitViews(activity: Activity): Map<String, AbsDokitView>? {

        return if (mActivityDoKitViews[activity] == null) emptyMap<String, AbsDokitView>() else mActivityDoKitViews[activity]
    }

    private fun createGlobalSingleDokitViewInfo(dokitView: AbsDokitView): GlobalSingleDokitViewInfo {
        return GlobalSingleDokitViewInfo(
            dokitView.javaClass,
            dokitView.tag,
            DokitIntent.MODE_SINGLE_INSTANCE,
            dokitView.bundle
        )
    }

    companion object {
        private const val MC_DELAY = 100
    }


}