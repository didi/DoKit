package com.didichuxing.doraemonkit.kit.timecounter

import com.didichuxing.doraemonkit.hook.ActivityHookManager
import com.didichuxing.doraemonkit.kit.core.DokitIntent
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.kit.timecounter.bean.TimeCounterRecord
import com.didichuxing.doraemonkit.kit.timecounter.couter.ActivityCounter
import com.didichuxing.doraemonkit.kit.timecounter.couter.ApplicationCounter

/**
 * 耗时记录manager
 *
 * @author yfengtech
 * 2020-07-02 14:09:04
 */
internal object TimeCounterManager {

    var isRunning = false
        private set

    /**
     * 计算Activity耗时
     */
    private val mActivityCounter = ActivityCounter()
    private val mAppCounter = ApplicationCounter()


    private val mActivityListener = object : ActivityHookManager.ActivityListener {
        override fun onPreLaunch() = mActivityCounter.preLaunch()
        override fun onLaunched() = mActivityCounter.launch()
        override fun onPrePause() = mActivityCounter.prePause()
        override fun onPaused() = mActivityCounter.pause()
    }

    /**
     * 打开耗时记录
     */
    fun start() {
        if (isRunning) return
        synchronized(isRunning) {
            isRunning = true
            val intent = DokitIntent(TimeCounterView::class.java)
            intent.mode = DokitIntent.MODE_SINGLE_INSTANCE
            DokitViewManager.instance.attach(intent)
            ActivityHookManager.registerActivityListener(mActivityListener)
        }
    }

    /**
     * 关闭耗时记录
     */
    fun stop() {
        synchronized(isRunning) {
            isRunning = false
            DokitViewManager.instance.detach(TimeCounterView::class.java)
            ActivityHookManager.unregisterActivityListener(mActivityListener)
        }
    }

    fun getAppCounter() = mAppCounter

    /**
     * 获取记录历史(app + activity)
     */
    fun getHistory(): List<TimeCounterRecord> {
        val result = mutableListOf<TimeCounterRecord>()
        if (mAppCounter.getApplicationRecord() != null) {
            result.add(mAppCounter.getApplicationRecord()!!)
        }
        result.addAll(mActivityCounter.getHistory())
        return result
    }
}