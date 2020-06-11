package com.didichuxing.doraemonkit.kit.blockmonitor.core

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @desc: 堆栈信息采集类
 */
class StackSampler {
    private val mRunning = AtomicBoolean(false)
    private var mStackThread: HandlerThread? =null
    private var mStackHandler: Handler? = null
    private val sStackMap = LinkedHashMap<Long, String>()
    private var mFilterCache: String? = null


    fun init() {
        if (mStackThread == null) {
            mStackThread = object : HandlerThread("BlockMonitor") {
                override fun onLooperPrepared() {
                    mStackHandler = Handler(mStackThread!!.looper)
                }
            }
            mStackThread?.start()
        }
    }

    fun startDump() {
        if (mStackHandler == null) {
            return
        }
        if (mRunning.get()) {
            return
        }
        mRunning.set(true)
        mStackHandler?.removeCallbacks(mRunnable)
        mStackHandler?.postDelayed(mRunnable, DEFAULT_SAMPLE_INTERVAL.toLong())
    }

    fun getThreadStackEntries(startTime: Long, endTime: Long): ArrayList<String> {
        val result = ArrayList<String>()
        synchronized(sStackMap) {
            for (entryTime in sStackMap.keys) {
                if (startTime < entryTime && entryTime < endTime) {
                    result.add(TIME_FORMATTER.format(entryTime)
                            + SEPARATOR
                            + SEPARATOR
                            + sStackMap[entryTime])
                }
            }
        }
        return result
    }

    fun stopDump() {
        if (mStackHandler == null) {
            return
        }
        if (!mRunning.get()) {
            return
        }
        mRunning.set(false)
        mFilterCache = null
        mStackHandler?.removeCallbacks(mRunnable)
    }

    fun shutDown() {
        stopDump()
        mStackThread?.quit()

    }

    private val mRunnable: Runnable = object : Runnable {
        override fun run() {
            dumpInfo()
            if (mRunning.get()) {
                mStackHandler?.postDelayed(this, DEFAULT_SAMPLE_INTERVAL.toLong())
            }
        }
    }

    private fun dumpInfo() {
        val stringBuilder = StringBuilder()
        val thread = Looper.getMainLooper().thread
        for (stackTraceElement in thread.stackTrace) {
            stringBuilder
                    .append(stackTraceElement.toString())
                    .append(SEPARATOR)
        }
        synchronized(sStackMap) {
            if (sStackMap.size == DEFAULT_MAX_ENTRY_COUNT) {
                sStackMap.remove(sStackMap.keys.iterator().next())
            }
            if (!shouldIgnore(stringBuilder)) {
                sStackMap[System.currentTimeMillis()] = stringBuilder.toString()
            }
        }
    }

    /**
     * 过滤掉重复项
     *
     * @param builder
     * @return
     */
    private fun shouldIgnore(builder: StringBuilder): Boolean {
        if (TextUtils.equals(mFilterCache, builder.toString())) {
            return true
        }
        mFilterCache = builder.toString()
        return false
    }

    companion object {
        private const val TAG = "StackSampler"
        private const val DEFAULT_SAMPLE_INTERVAL = 300
        private const val DEFAULT_MAX_ENTRY_COUNT = 100
        private const val SEPARATOR = "\r\n"
        private val TIME_FORMATTER = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.CHINESE)
    }
}