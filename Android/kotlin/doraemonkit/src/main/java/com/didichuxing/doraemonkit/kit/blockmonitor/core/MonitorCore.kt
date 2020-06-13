package com.didichuxing.doraemonkit.kit.blockmonitor.core

import android.os.SystemClock
import android.util.Printer
import com.didichuxing.doraemonkit.kit.blockmonitor.bean.BlockInfo
import com.didichuxing.doraemonkit.kit.blockmonitor.core.BlockMonitorManager.Companion.instance

/**
 * @desc: 检测卡顿的日志类
 */
internal class MonitorCore : Printer {
    private var mStartTime: Long = 0
    private var mStartThreadTime: Long = 0
    private var mPrintingStarted = false
    private val mStackSampler: StackSampler = StackSampler()

    override fun println(x: String) {
        if (!mPrintingStarted) {
            mStartTime = System.currentTimeMillis()
            mStartThreadTime = SystemClock.currentThreadTimeMillis()
            mPrintingStarted = true
            mStackSampler.startDump()
        } else {
            val endTime = System.currentTimeMillis()
            val endThreadTime = SystemClock.currentThreadTimeMillis()
            mPrintingStarted = false
            if (isBlock(endTime)) {
                val entries = mStackSampler.getThreadStackEntries(mStartTime, endTime)
                if (entries.size > 0) {
                    val blockInfo = BlockInfo.newInstance()
                            .setMainThreadTimeCost(mStartTime, endTime, mStartThreadTime, endThreadTime)
                            .setThreadStackEntries(entries)
                            .flushString()
                    instance.notifyBlockEvent(blockInfo)
                }
            }
            mStackSampler.stopDump()
        }
    }

    private fun isBlock(endTime: Long): Boolean {
        return endTime - mStartTime > BLOCK_THRESHOLD_MILLIS
    }

    fun shutDown() {
        mStackSampler.shutDown()
    }

    companion object {
        private const val TAG = "MonitorCore"

        /**
         * 卡顿阈值
         */
        private const val BLOCK_THRESHOLD_MILLIS = 200
    }

    init {
        mStackSampler.init()
    }
}