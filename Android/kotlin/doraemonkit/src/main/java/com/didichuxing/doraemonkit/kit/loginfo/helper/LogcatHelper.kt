package com.didichuxing.doraemonkit.kit.loginfo.helper

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * @author lostjobs created on 2020/6/28
 */
object LogcatHelper {

    private const val BUFFER_MAIN = "main"

    @Throws(IOException::class)
    fun getLogcatProcess(buffer: String): Process {
        val args = getLogcatArgs(buffer)
        return RuntimeHelper.exec(args)
    }

    private fun getLogcatArgs(buffer: String): MutableList<String> {
        val args = mutableListOf("logcat", "-v", "time")
        if (buffer != BUFFER_MAIN) {
            args.add("-b")
            args.add(buffer)
        }
        return args
    }

    fun getLastLogLine(buffer: String): String {
        var dumpLogcatProcess: Process? = null
        val result: StringBuilder = StringBuilder()
        try {
            val args = getLogcatArgs(buffer)
            args.add("-d")
            dumpLogcatProcess = RuntimeHelper.exec(args)

            BufferedReader(InputStreamReader(dumpLogcatProcess.inputStream), 8192)
                    .useLines {
                        result.append(it.last())
                    }

        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            dumpLogcatProcess?.run { RuntimeHelper.destroy(this) }
        }
        return result.toString()
    }
}