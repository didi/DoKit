package com.didichuxing.doraemonkit.kit.performance.manager

import android.content.Context
import com.blankj.utilcode.util.ActivityUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.DokitIntent
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.kit.performance.PerformanceDokitView
import com.didichuxing.doraemonkit.kit.performance.PerformanceDokitView.Companion.DEFAULT_REFRESH_INTERVAL
import com.didichuxing.doraemonkit.kit.performance.manager.datasource.DataSourceFactory
import java.util.*

/**
 *
 * Desc:性能监控 帧率、 CPU、RAM、流量监控统一显示的DokitView 管理类
 * <p>
 * Date: 2020-06-09
 * Company:
 * Updater:
 * Update Time:
 * Update Comments:
 *
 * Author: pengyushan
 */
object PerformanceDokitViewManager {
    var singleperformanceViewInfos = TreeMap<String, PerformanceViewInfoEntity>()

    /**
     * @param performanceType 参考 DataSourceFactory
     */
    fun open(performanceType: Int, title: String, listener: PerformanceFragmentCloseListener?) {
        open(performanceType, title, DEFAULT_REFRESH_INTERVAL, listener)
    }

    fun open(performanceType: Int, title: String, interval: Int, listener: PerformanceFragmentCloseListener?) {
        var performanceDokitView = DokitViewManager.instance.getDokitView(ActivityUtils.getTopActivity(), PerformanceDokitView::class.java.simpleName) as? PerformanceDokitView
        if (performanceDokitView==null){
            val dokitIntent = DokitIntent(PerformanceDokitView::class.java)
            dokitIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE
            DokitViewManager.instance.attach(dokitIntent)
            performanceDokitView = DokitViewManager.instance.getDokitView(ActivityUtils.getTopActivity(), PerformanceDokitView::class.java.simpleName)as PerformanceDokitView
            performanceDokitView.addItem(performanceType, title, interval)
        }else {
            performanceDokitView.addItem(performanceType, title, interval)
        }
        performanceDokitView.addPerformanceFragmentCloseListener(listener)
        singleperformanceViewInfos[title] = PerformanceViewInfoEntity(performanceType, title, interval)
    }

    /**
     * 性能检测设置页面关闭时调用
     *
     * @param listener
     */
    fun onPerformanceSettingFragmentDestroy(listener: PerformanceFragmentCloseListener) {
        val performanceDokitView = DokitViewManager.instance.getDokitView(ActivityUtils.getTopActivity(), PerformanceDokitView::class.java.simpleName) as? PerformanceDokitView
        performanceDokitView?.removePerformanceFragmentCloseListener(listener)
    }

    /**
     * @param performanceType 参考 DataSourceFactory
     */
    fun close(performanceType: Int, title: String) {
        val performanceDokitView = DokitViewManager.instance.getDokitView(ActivityUtils.getTopActivity(), PerformanceDokitView::class.java.simpleName) as? PerformanceDokitView
        performanceDokitView?.removeItem(performanceType)
        singleperformanceViewInfos.remove(title)
    }

    fun getTitleByPerformanceType(context: Context?, performanceType: Int): String {
        var title = ""
        if (context!=null) {
            when (performanceType) {
                DataSourceFactory.TYPE_FPS -> title = context.getString(R.string.dk_kit_frame_info_desc)
                DataSourceFactory.TYPE_CPU -> title = context.getString(R.string.dk_frameinfo_cpu)
                DataSourceFactory.TYPE_RAM -> title = context.getString(R.string.dk_ram_detection_title)
                else -> {
                }
            }
        }
        return title
    }
}