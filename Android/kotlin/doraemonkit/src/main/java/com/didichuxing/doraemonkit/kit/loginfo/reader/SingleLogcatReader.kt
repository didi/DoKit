package com.didichuxing.doraemonkit.kit.loginfo.reader

import com.didichuxing.doraemonkit.kit.loginfo.helper.LogcatHelper
import com.didichuxing.doraemonkit.kit.loginfo.helper.RuntimeHelper
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * @author lostjobs created on 2020/6/28
 */
class SingleLogcatReader(recordingMode: Boolean, var logBuffer: String, var lastLine: String?) : AbsLogcatReader(recordingMode) {

    private val logcatProcess = LogcatHelper.getLogcatProcess(logBuffer)
    private val bufferReader = BufferedReader(InputStreamReader(logcatProcess.inputStream), 8192)

    override fun readLine(): String {
        val line = bufferReader.readLine()
        if (recordingMode && lastLine != null){
            if (lastLine == line) {
                lastLine = null
            }
        }
        return line
    }

    override fun killQuietly() {
        RuntimeHelper.destroy(logcatProcess)
    }

    override fun readyToRecord(): Boolean = recordingMode && lastLine == null

    override fun getProcesses(): List<Process> = listOf(logcatProcess)
}