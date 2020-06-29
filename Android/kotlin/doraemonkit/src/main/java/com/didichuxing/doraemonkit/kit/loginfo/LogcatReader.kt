package com.didichuxing.doraemonkit.kit.loginfo

import java.io.IOException

/**
 * @author lostjobs created on 2020/6/28
 */
interface LogcatReader {
    @Throws(IOException::class)
    fun readLine(): String?

    fun killQuietly()

    fun readyToRecord(): Boolean

    fun getProcesses(): List<Process>
}