package com.didichuxing.doraemonkit.kit.loginfo

import android.text.TextUtils
import android.util.Log
import java.util.regex.Pattern

/**
 * @author lostjobs created on 2020/6/28
 */
class LogLine(originalLine: String, expanded: Boolean) {
    companion object {

        private const val TIMESTAMP_LENGTH = 19

        private val logPattern = Pattern.compile( // log level
                "(\\w)" +
                        "/" +  // tag
                        "([^(]+)" +
                        "\\(\\s*" +  // pid
                        "(\\d+)" +  // optional weird number that only occurs on ZTE blade
                        "(?:\\*\\s*\\d+)?" +
                        "\\): ")

        private val filterPattern = "ResourceType|memtrack|android.os.Debug|BufferItemConsumer|DPM.*|MDM.*|ChimeraUtils|BatteryExternalStats.*|chatty.*|DisplayPowerController|WidgetHelper|WearableService|DigitalWidget.*|^ANDR-PERF-.*"


        private fun convertCharToLogLevel(loglevelChar: Char): Int {
            return when (loglevelChar) {
                'D' -> Log.DEBUG
                'E' -> Log.ERROR
                'I' -> Log.INFO
                'V' -> Log.VERBOSE
                'W' -> Log.WARN
                'F' -> Log.VERBOSE
                else -> -1
            }
        }

        private fun convertLogLevelToChar(logLevel: Int): Char {
            return when (logLevel) {
                Log.DEBUG -> return 'D'
                Log.ERROR -> return 'E'
                Log.INFO -> return 'I'
                Log.VERBOSE -> return 'V'
                Log.WARN -> return 'W'
                else -> ' '
            }
        }


        fun newLogLine(originalLine: String, expanded: Boolean): LogLine = LogLine(originalLine, expanded)
    }

    var logLevel: Int = Log.DEBUG
    var tag: String? = null
    var logOutput: String
    var processId: Int = -1
    var timestamp: String? = null
    val logLevelText: Char
        get() = convertLogLevelToChar(logLevel)
    var expanded: Boolean = false
    private var highlighted: Boolean = false

    init {
        this.expanded = expanded

        var startIdx = 0

        if (!TextUtils.isEmpty(originalLine)
                && Character.isDigit(originalLine[0])
                && originalLine.length >= TIMESTAMP_LENGTH) {
            val timestamp = originalLine.substring(0, TIMESTAMP_LENGTH - 1)
            this.timestamp = timestamp
            startIdx = TIMESTAMP_LENGTH
        }

        val matcher = logPattern.matcher(originalLine)

        if (matcher.find(startIdx)) {
            val logLevelChar = matcher.group(1)?.get(0) ?: 'V'
            val logText = originalLine.substring(matcher.end())
            if (logText.matches("^maxLineHeight.*|Failed to read.*".toRegex())) {
                this.logLevel = convertCharToLogLevel('V')
            } else {
                this.logLevel = convertCharToLogLevel(logLevelChar)
            }

            val tagText = matcher.group(2)
            if (tagText?.matches(filterPattern.toRegex()) == true) {
                this.logLevel = convertCharToLogLevel('V')
            }

            this.tag = logText

            val processId = matcher.group(3)
            this.processId = processId?.toIntOrNull() ?: -1

            this.logOutput = logText
        } else {
            this.logOutput = originalLine
            this.logLevel - 1
        }
    }

    fun originalLine(): String {
        if (logLevel == -1)
            return logOutput

        val originLineBuilder = StringBuilder()

        timestamp?.run {
            originLineBuilder.append(this).append(' ')
        }

        originLineBuilder.append(convertLogLevelToChar(logLevel))
                .append('/')
                .append(tag)
                .append('(')
                .append(processId)
                .append("): ")
                .append(logOutput)

        return originLineBuilder.toString()
    }

}