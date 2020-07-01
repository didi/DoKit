package com.didichuxing.doraemonkit.kit.loginfo.util

import android.text.TextUtils
import com.didichuxing.doraemonkit.kit.loginfo.LogLine
import com.didichuxing.doraemonkit.kit.loginfo.util.StringUtil.containsIgnoreCase
import java.util.regex.Pattern


class SearchCriteria(inputQuery: CharSequence?) {
    companion object {
        private val PID_PATTERN = Pattern.compile("pid:(\\d+)", Pattern.CASE_INSENSITIVE)
        private val TAG_PATTERN = Pattern.compile("tag:(\"[^\"]+\"|\\S+)", Pattern.CASE_INSENSITIVE)
    }

    private var pid = -1
    private var tag: String? = null
    private var searchText: String
    private var searchTextAsInt = -1

    val isEmpty: Boolean
        get() = pid == -1 && TextUtils.isEmpty(tag) && TextUtils.isEmpty(searchText)

    init {
        val query = StringBuilder(StringUtil.nullToEmpty(inputQuery))
        val pidMatcher = PID_PATTERN.matcher(query)
        if (pidMatcher.find()) {
            try {
                pid = pidMatcher.group(1)?.toIntOrNull() ?: -1
                query.replace(pidMatcher.start(), pidMatcher.end(), "")
            } catch (e: Throwable) {
            }
        }

        val tagMatcher = TAG_PATTERN.matcher(query)
        if (tagMatcher.find()) {
            tag = tagMatcher.group(1)
            tag?.run {
                if (startsWith("\"") && endsWith("\"")) {
                    tag = this.substring(1, this.lastIndex)
                }
            }
            query.replace(tagMatcher.start(), tagMatcher.end(), "")
        }

        searchText = query.toString().trim()

        try {
            searchTextAsInt = searchText.toInt()
        } catch (ignore: NumberFormatException) {
        }
    }

    fun matches(logLine: LogLine): Boolean {
        if (!checkFoundPid(logLine))
            return false

        if (!checkFoundTag(logLine))
            return false

        return checkFoundText(logLine)
    }

    private fun checkFoundPid(logLine: LogLine): Boolean {
        return pid == -1 || logLine.processId == pid
    }


    private fun checkFoundTag(logLine: LogLine): Boolean {
        return (TextUtils.isEmpty(tag)
                || logLine.tag != null && containsIgnoreCase(logLine.tag, tag))
    }


    private fun checkFoundText(logLine: LogLine): Boolean {
        return (TextUtils.isEmpty(searchText)
                || searchTextAsInt != -1 && searchTextAsInt == logLine.processId
                || logLine.tag != null && containsIgnoreCase(logLine.tag, searchText)
                && containsIgnoreCase(logLine.logOutput, searchText))

    }
}