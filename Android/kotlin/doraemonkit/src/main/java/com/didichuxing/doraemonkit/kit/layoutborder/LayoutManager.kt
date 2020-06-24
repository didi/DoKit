package com.didichuxing.doraemonkit.kit.layoutborder

import android.app.Activity
import android.view.ViewGroup
import com.blankj.utilcode.util.ActivityUtils
import com.didichuxing.doraemonkit.kit.core.DokitIntent
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.kit.core.UniversalActivity
import com.didichuxing.doraemonkit.util.LifecycleListenerUtil
import com.didichuxing.doraemonkit.util.LifecycleListenerWrapper

/**
 * 布局相关管理类。
 * 包括布局边框的显示隐藏、布局层级的显示隐藏
 * @author Donald Yan
 * @date 2020/6/19
 */
class LayoutManager private constructor() {

    companion object {
        val instance: LayoutManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            LayoutManager()
        }
    }

    private val mLifecycleListener: LifecycleListenerWrapper = object : LifecycleListenerWrapper() {
        override fun onActivityResumed(activity: Activity?) {
            if (activity != null) {
                resolveActivity(activity)
            }
        }
    }

    private var mViewBorderFrameLayout: ViewBorderFrameLayout? = null

    /**
     * 显示布局边框
     */
    fun showBorder() {
        resolveActivity(ActivityUtils.getTopActivity())
        LifecycleListenerUtil.registerListener(mLifecycleListener)
        showLevel()
    }

    /**
     * 解析activity的布局，将所有子view从根布局中移除并添加到布局边框容器中。
     */
    private fun resolveActivity(activity: Activity) {
        if (activity is UniversalActivity) {
            return
        }
        val root = activity.window?.decorView as ViewGroup
        mViewBorderFrameLayout = ViewBorderFrameLayout(root.context)
        while (root.childCount != 0) {
            val child = root.getChildAt(0)
            if (child is ViewBorderFrameLayout) {
                mViewBorderFrameLayout = child
                return
            }
            root.removeView(child)
            mViewBorderFrameLayout!!.addView(child)
        }
        root.addView(mViewBorderFrameLayout)
    }

    /**
     * 隐藏布局边框
     */
    fun hideBorder() {
        mViewBorderFrameLayout?.close()
        mViewBorderFrameLayout = null
        LifecycleListenerUtil.unRegisterListener(mLifecycleListener)
    }

    /**
     * 显示布局层级
     */
    fun showLevel() {
        val dokitIntent = DokitIntent(LayoutLevelDokitView::class.java)
        dokitIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE
        DokitViewManager.instance.attach(dokitIntent)
    }

    /**
     * 隐藏布局层级
     */
    fun hideLevel() {
        DokitViewManager.instance.detach(LayoutLevelDokitView::class.java)
    }

    /**
     * 关闭所有布局工具
     */
    fun close() {
        hideBorder()
        hideLevel()
    }
}