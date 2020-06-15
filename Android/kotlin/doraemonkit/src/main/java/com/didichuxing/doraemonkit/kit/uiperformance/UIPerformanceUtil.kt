package com.didichuxing.doraemonkit.kit.uiperformance

import android.app.Activity
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import com.didichuxing.doraemonkit.model.ViewInfo
import com.didichuxing.doraemonkit.util.LogHelper.d
import com.didichuxing.doraemonkit.util.UIUtils.getDokitAppContentView
import java.util.*

/**
 *
 * Desc:ui层级工具类
 * <p>
 * Date: 2020-06-15
 * Company:
 * Updater:
 * Update Time:
 * Update Comments:
 *
 * Author: pengyushan
 */
object UIPerformanceUtil {
    private const val TAG = "UIPerformanceUtil"
    /**
     *
     * Desc:获取activity界面信息
     * <p>
     * Author: pengyushan
     * Date: 2020-06-15
     */
    fun getActivityViewInfo(activity: Activity?,performanceCanvas: Canvas? = null): List<ViewInfo> {
        if (activity == null) {
            d(TAG, "resume activity is null")
            return ArrayList()
        }
        if (activity.window == null) {
            d(TAG, "resume activity window is null")
            return ArrayList()
        }
        return getViewInfo(getDokitAppContentView(activity),performanceCanvas)
    }

    /**
     *
     * Desc:获取view界面信息
     * <p>
     * Author: pengyushan
     * Date: 2020-06-15
     */
    private fun getViewInfo(view: View?,performanceCanvas: Canvas?): List<ViewInfo> {
        val info: MutableList<ViewInfo> = ArrayList()
        parseViews(view, info, 0,performanceCanvas)
        return info
    }

    /**
     *
     * Desc:解析界面信息
     * <p>
     * Author: pengyushan
     * Date: 2020-06-15
     * @param view View? 所要解析的view
     * @param infos MutableList<ViewInfo>  界面信息
     * @param layerNum Int  界面层级
     */
    private fun parseViews(view: View?, info: MutableList<ViewInfo>, layerNum: Int,performanceCanvas: Canvas?) {
        var layerNum = layerNum
        if (view == null) {
            return
        }
        layerNum++
        if (view is ViewGroup) {
            val childCount = view.childCount
            if (childCount != 0) {
                for (index in childCount - 1 downTo 0) {
                    parseViews(view.getChildAt(index), info, layerNum,performanceCanvas)
                }
            }
        } else {
            val startTime = System.nanoTime()
            performanceCanvas?.let { view.draw(performanceCanvas) }
            val endTime = System.nanoTime()
            val time = (endTime - startTime) / 10000 / 100f
            val viewInfo = ViewInfo(view)
            viewInfo.drawTime = time
            viewInfo.layerNum = layerNum
            info.add(viewInfo)
        }
    }
}