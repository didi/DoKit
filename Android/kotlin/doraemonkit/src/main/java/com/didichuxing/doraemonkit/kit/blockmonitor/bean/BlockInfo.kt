/*
 * Copyright (C) 2016 MarkZhai (http://zhaiyifan.cn).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.didichuxing.doraemonkit.kit.blockmonitor.bean

import java.text.SimpleDateFormat
import java.util.*

/**
 * Information to trace a block.
 */
class BlockInfo {

    companion object {
        const val SEPARATOR = "\r\n"
        private const val KV = " = "
        private val TIME_FORMATTER = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.CHINESE)
        private const val KEY_TIME_COST = "time"
        private const val KEY_THREAD_TIME_COST = "thread-time"
        private const val KEY_TIME_COST_START = "time-start"
        private const val KEY_TIME_COST_END = "time-end"
        private const val KEY_STACK = "stack"
        fun newInstance(): BlockInfo {
            return BlockInfo()
        }
    }

    // Per Block Info fields
    @JvmField
    var timeCost: Long = 0
    private var threadTimeCost: Long = 0

    @JvmField
    var time: Long = 0
    var timeStart: String? = null
    private var timeEnd: String? = null
    var threadStackEntries: ArrayList<String> = ArrayList()
    private val timeSb = StringBuilder()
    private val stackSb = StringBuilder()

    @JvmField
    var concernStackString: String? = null

    fun flushString(): BlockInfo {
        val separator = SEPARATOR
        timeSb.append(KEY_TIME_COST).append(KV).append(timeCost).append(separator)
        timeSb.append(KEY_THREAD_TIME_COST).append(KV).append(threadTimeCost).append(separator)
        timeSb.append(KEY_TIME_COST_START).append(KV).append(timeStart).append(separator)
        timeSb.append(KEY_TIME_COST_END).append(KV).append(timeEnd).append(separator)
        if (threadStackEntries.isNotEmpty()) {
            val temp = StringBuilder()
            for (s in threadStackEntries) {
                temp.append(s)
                temp.append(separator)
            }
            stackSb.append(KEY_STACK).append(KV).append(temp.toString()).append(separator)
        }
        return this
    }

    fun setThreadStackEntries(threadStackEntries: ArrayList<String>): BlockInfo {
        this.threadStackEntries = threadStackEntries
        return this
    }

    fun setMainThreadTimeCost(realTimeStart: Long, realTimeEnd: Long, threadTimeStart: Long, threadTimeEnd: Long): BlockInfo {
        timeCost = realTimeEnd - realTimeStart
        threadTimeCost = threadTimeEnd - threadTimeStart
        timeStart = TIME_FORMATTER.format(realTimeStart)
        timeEnd = TIME_FORMATTER.format(realTimeEnd)
        return this
    }

    override fun toString(): String {
        return """
            $timeSb
            $stackSb
            """.trimIndent()
    }


}