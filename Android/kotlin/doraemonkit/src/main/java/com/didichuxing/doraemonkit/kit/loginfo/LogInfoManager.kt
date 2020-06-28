package com.didichuxing.doraemonkit.kit.loginfo

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.didichuxing.doraemonkit.kit.loginfo.reader.LogcatReaderLoader
import com.didichuxing.doraemonkit.util.ExecutorUtil
import com.didichuxing.doraemonkit.util.LogHelper
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.ArrayList

/**
 * @author lostjobs created on 2020/6/28
 */
object LogInfoManager {
    private const val TAG = "LogInfoManager"
    private const val MESSAGE_PUBLISH_LOG = 1001

    private var mListener: OnLogCatchListener? = null
    private var mLogCatchTask: LogCatchRunnable? = null
    fun start() {
        stop()
        mLogCatchTask = LogCatchRunnable().also {
            ExecutorUtil.execute(it)
        }
    }

    fun stop() {
        mLogCatchTask?.stop()
        mLogCatchTask = null
    }

    fun registerListener(listener: OnLogCatchListener) {
        this.mListener = listener
    }

    fun removeListener() {
        this.mListener = null
    }


    interface OnLogCatchListener {
        /**
         * 日志回调
         */
        fun onLogCatch(logLines: List<LogLine>)
    }


    private class InternalHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            if (msg.what == MESSAGE_PUBLISH_LOG) {
                val logLines = msg.obj
                (logLines as? List<*>)?.filterIsInstance(LogLine::class.java)?.also {
                    mListener?.onLogCatch(it)
                }
            }
        }
    }

    class LogCatchRunnable : Runnable {
        private var isRunning: AtomicBoolean = AtomicBoolean(true)
        private val internalHandler = InternalHandler(Looper.getMainLooper())
        private val mPid = android.os.Process.myPid()

        override fun run() {
            try {
                val loader = LogcatReaderLoader.create(true)
                val maxLines = 10000
                val reader = loader.loadReader()
                val initialLines = LinkedList<LogLine>()
                while (isRunning.get()) {
                    val line = reader.readLine() ?: break
                    val logLine = LogLine.newLogLine(line, false)
                    if (!reader.readyToRecord()) {
                        if (logLine.processId == mPid) {
                            initialLines.add(logLine)
                        }

                        if (initialLines.size > maxLines) {
                            initialLines.removeFirst()
                        }
                    } else if (!initialLines.isEmpty()) {
                        if (logLine.processId == mPid) {
                            initialLines.add(logLine)
                        }

                        val msg = internalHandler.obtainMessage(MESSAGE_PUBLISH_LOG)
                        msg.obj = ArrayList(initialLines)
                        msg.sendToTarget()
                        initialLines.clear()
                    } else {
                        if (logLine.processId == mPid) {
                            val msg = internalHandler.obtainMessage(MESSAGE_PUBLISH_LOG)
                            msg.obj = Collections.singletonList(logLine)
                            msg.sendToTarget()
                        }
                    }
                }
                reader.killQuietly()
            } catch (e: Exception) {
                LogHelper.e(TAG, e.toString())
            }
        }

        fun stop() {
            isRunning.set(false)
        }
    }
}