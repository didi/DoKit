package com.didichuxing.doraemonkit.kit.performance

import android.content.Context
import com.blankj.utilcode.util.ActivityUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.DokitIntent
import com.didichuxing.doraemonkit.kit.core.DokitViewManager.Companion.instance
import com.didichuxing.doraemonkit.kit.performance.manager.PerformanceFragmentCloseListener
import com.didichuxing.doraemonkit.kit.performance.manager.datasource.DataSourceFactory
import java.util.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-11-16:05
 * 描    述：性能监控 帧率、 CPU、RAM、流量监控统一显示的DokitView 管理类
 * 修订历史：
 * ================================================
 */
object PerformanceDokitViewManager {
    var singleperformanceViewInfos = TreeMap<String, performanceViewInfo>()

    /**
     * @param performanceType 参考 DataSourceFactory
     */
    fun open(performanceType: Int, title: String, listener: PerformanceFragmentCloseListener?) {
        open(performanceType, title, PerformanceDokitView.DEFAULT_REFRESH_INTERVAL, listener)
    }

    fun open(performanceType: Int, title: String, interval: Int, listener: PerformanceFragmentCloseListener?) {
        var performanceDokitView = instance.getDokitView(ActivityUtils.getTopActivity(), PerformanceDokitView::class.java.simpleName) as PerformanceDokitView?
        if (performanceDokitView == null) {
            val dokitIntent = DokitIntent(PerformanceDokitView::class.java)
            dokitIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE
            instance.attach(dokitIntent)
            performanceDokitView = instance.getDokitView(ActivityUtils.getTopActivity(), PerformanceDokitView::class.java.simpleName) as PerformanceDokitView?
            performanceDokitView!!.addItem(performanceType, title, interval)
        } else {
            performanceDokitView.addItem(performanceType, title, interval)
        }
        performanceDokitView.addPerformanceFragmentCloseListener(listener)
        singleperformanceViewInfos[title] = performanceViewInfo(performanceType, title, interval)
    }

    /**
     * 性能检测设置页面关闭时调用
     *
     * @param listener
     */
    @JvmStatic
    fun onPerformanceSettingFragmentDestroy(listener: PerformanceFragmentCloseListener?) {
        val performanceDokitView = instance.getDokitView(ActivityUtils.getTopActivity(), PerformanceDokitView::class.java.simpleName) as PerformanceDokitView?
        performanceDokitView?.removePerformanceFragmentCloseListener(listener!!)
    }

    /**
     * @param performanceType 参考 DataSourceFactory
     */
    @JvmStatic
    fun close(performanceType: Int, title: String) {
        val performanceDokitView = instance.getDokitView(ActivityUtils.getTopActivity(), PerformanceDokitView::class.java.simpleName) as PerformanceDokitView?
        performanceDokitView?.removeItem(performanceType)
        singleperformanceViewInfos.remove(title)
    }

    fun getTitleByPerformanceType(context: Context, performanceType: Int): String {
        var title = ""
        when (performanceType) {
            DataSourceFactory.TYPE_NETWORK -> title = context.getString(R.string.dk_kit_net_monitor)
            else -> {
            }
        }
        return title
    }
}