package com.didichuxing.doraemonkit.kit.timecounter.couter

import android.os.SystemClock
import com.didichuxing.doraemonkit.kit.timecounter.bean.ApplicationTimeCounterRecord

internal class ApplicationCounter {

    private var applicationRecord: ApplicationTimeCounterRecord? = null

    /**
     * step 1
     */
    fun onAppAttachBaseContextStart() {
        applicationRecord = ApplicationTimeCounterRecord(System.currentTimeMillis())
        applicationRecord?.attachStartTime = SystemClock.elapsedRealtime()
    }


    /**
     * step 2
     */
    fun onAppAttachBaseContextEnd() {
        applicationRecord?.attachEndTime = SystemClock.elapsedRealtime()
    }

    /**
     * step 3
     */
    fun onAppCreateStart() {
        applicationRecord?.createStartTime = SystemClock.elapsedRealtime()
    }

    /**
     * step 4
     */
    fun onAppCreateEnd() {
        applicationRecord?.createEndTime = SystemClock.elapsedRealtime()
    }

    fun getApplicationRecord(): ApplicationTimeCounterRecord? {
        return if (applicationRecord?.isFinish() == true) {
            applicationRecord
        } else {
            null
        }
    }

}