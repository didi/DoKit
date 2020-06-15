package com.didichuxing.doraemonkit.kit.uiperformance

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ActivityUtils
import com.didichuxing.doraemonkit.model.ViewInfo
import com.didichuxing.doraemonkit.util.LifecycleListenerUtil
import com.didichuxing.doraemonkit.util.LifecycleListenerUtil.registerListener
import com.didichuxing.doraemonkit.util.LifecycleListenerUtil.unRegisterListener
import com.didichuxing.doraemonkit.util.LogHelper.d
import com.didichuxing.doraemonkit.util.UIUtils.getDokitAppContentView
import com.didichuxing.doraemonkit.util.UIUtils.heightPixels
import com.didichuxing.doraemonkit.util.UIUtils.widthPixels
import java.util.*

/**
 *
 * Desc:ui层级管理类
 * <p>
 * Date: 2020-06-15
 * Company:
 * Updater:
 * Update Time:
 * Update Comments:
 *
 * Author: pengyushan
 */
class UIPerformanceManager private constructor() : LifecycleListenerUtil.LifecycleListener {
    private var mPerformanceCanvas: Canvas? = null
    private val mListeners: MutableList<PerformanceDataListener> = ArrayList()

    private object Holder {
        val INSTANCE = UIPerformanceManager()
    }

    /**
     *
     * Desc:开始跟踪显示界面信息
     * <p>
     * Author: pengyushan
     * Date: 2020-06-15
     */
    fun startTrack() {
        val canvasBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888)
        mPerformanceCanvas = Canvas(canvasBitmap)
        registerListener(this)
    }

    /**
     *
     * Desc:停止跟踪显示界面信息
     * <p>
     * Author: pengyushan
     * Date: 2020-06-15
     */
    fun stopTrack() {
        mListeners.clear()
        mPerformanceCanvas = null
        unRegisterListener(this)
    }

    fun addListener(listener: PerformanceDataListener) {
        mListeners.add(listener)
    }

    fun removeListener(listener: PerformanceDataListener) {
        mListeners.remove(listener)
    }

    override fun onActivityResumed(activity: Activity?) {
        for (listener in mListeners) {
            listener.onRefresh(UIPerformanceUtil.getActivityViewInfo(activity,mPerformanceCanvas))
        }
    }

    override fun onActivityPaused(activity: Activity?) {}
    override fun onFragmentAttached(f: Fragment?) {
        for (listener in mListeners) {
            listener.onRefresh(UIPerformanceUtil.getActivityViewInfo(f!!.activity,mPerformanceCanvas))
        }
    }

    override fun onFragmentDetached(f: Fragment?) {
        for (listener in mListeners) {
            listener.onRefresh(UIPerformanceUtil.getActivityViewInfo(f!!.activity,mPerformanceCanvas))
        }
    }

    interface PerformanceDataListener {
        fun onRefresh(viewInfos: List<ViewInfo>?)
    }

    /**
     * 初始化时直接显示显示层级
     */
    fun initRefresh() {
        for (listener in mListeners) {
            listener.onRefresh(UIPerformanceUtil.getActivityViewInfo(ActivityUtils.getTopActivity(),mPerformanceCanvas))
        }
    }

    companion object {
        private const val TAG = "UIPerformanceManager"
        @JvmStatic
        val instance = Holder.INSTANCE
    }
}