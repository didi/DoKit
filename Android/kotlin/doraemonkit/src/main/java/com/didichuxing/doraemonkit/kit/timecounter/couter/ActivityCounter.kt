package com.didichuxing.doraemonkit.kit.timecounter.couter

import android.app.Activity
import android.os.SystemClock
import com.blankj.utilcode.util.ActivityUtils
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.kit.timecounter.TimeCounterView
import com.didichuxing.doraemonkit.kit.timecounter.bean.ActivityTimeCounterRecord

internal class ActivityCounter {

    /**
     * 当前activity耗时记录
     */
    private var mCurrentRecord: ActivityTimeCounterRecord? = null

    /**
     * 历史activity耗时记录
     */
    private val mRecordList = mutableListOf<ActivityTimeCounterRecord>()

    /**
     * step.1
     */
    fun prePause() {
        mCurrentRecord = ActivityTimeCounterRecord(System.currentTimeMillis())
        mCurrentRecord?.pauseStartTime = SystemClock.elapsedRealtime()
        val act = ActivityUtils.getTopActivity()
        if (act != null) {
            mCurrentRecord?.mPreviousActivity = act.javaClass.simpleName
        }
    }

    /**
     * step.2
     */
    fun pause() {
        mCurrentRecord?.pauseEndTime = SystemClock.elapsedRealtime()
    }

    /**
     * step.3
     */
    fun preLaunch() {
        if (mCurrentRecord == null) {
            // 存在没有pause的情况
            mCurrentRecord = ActivityTimeCounterRecord(System.currentTimeMillis())
        }
        mCurrentRecord?.launchStartTime = SystemClock.elapsedRealtime()
    }

    /**
     * step.4
     */
    fun launch() {
        mCurrentRecord?.launchEndTime = SystemClock.elapsedRealtime()
        render()
    }

    /**
     * step.5
     */
    private fun render() {

        val activity: Activity? = ActivityUtils.getTopActivity()
        val decorView = activity?.window?.decorView

        mCurrentRecord?.renderStartTime = SystemClock.elapsedRealtime()
        if (activity != null && decorView != null) {
            mCurrentRecord?.mCurrentActivity = activity.javaClass.simpleName
            decorView.post {
                renderEnd()
            }
        } else {
            renderEnd()
        }
    }

    /**
     * step.6
     */
    private fun renderEnd() {
        mCurrentRecord?.renderEndTime = SystemClock.elapsedRealtime()

        mCurrentRecord?.let {
            addToAppHealth(it)
            mRecordList.add(it)

            val view = DokitViewManager.instance.getDokitView(ActivityUtils.getTopActivity(),
                    TimeCounterView::class.java.simpleName) as? TimeCounterView
            view?.showInfo(it)
        }
    }

    private fun addToAppHealth(activityRecord: ActivityTimeCounterRecord) {
        try {
            //TODO 将Activity 打开耗时 添加到AppHealth 中
            //if (DokitConstant.APP_HEALTH_RUNNING) {
            //    if (ActivityUtils.getTopActivity().javaClass.canonicalName != UniversalActivity::class.java.canonicalName) {
            //        val pageLoadBean = AppHealthInfo.DataBean.PageLoadBean()
            //        pageLoadBean.page = ActivityUtils.getTopActivity().javaClass.canonicalName
            //        pageLoadBean.time = activityRecord.totalCost().toString()
            //        pageLoadBean.trace = activityRecord.title()
            //        AppHealthInfoUtil.instance.addPageLoadInfo(pageLoadBean)
            //    }
            //}
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getHistory(): List<ActivityTimeCounterRecord> = mRecordList
}