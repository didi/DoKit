package com.didichuxing.doraemonkit.kit.loginfo.util

import android.text.TextUtils
import java.util.*

/**
 * @author nolan
 */
object StringUtil {
    /**
     * Pad the specified number of spaces to the input string to make it that length
     *
     * @param input
     * @param size
     * @return
     */
    fun padLeft(input: String, size: Int): String {
        require(input.length <= size) { "input must be shorter than or equal to the number of spaces: $size" }
        val sb = StringBuilder()
        for (i in input.length until size) {
            sb.append(" ")
        }
        return sb.append(input).toString()
    }

    /**
     * same as the String.split(), except it doesn't use regexes, so it's faster.
     *
     * @param str       - the string to split up
     * @param delimiter the delimiter
     * @return the split string
     */
    fun split(str: String, delimiter: String): Array<String> {
        val result: MutableList<String> = ArrayList()
        var lastIndex = 0
        var index = str.indexOf(delimiter)
        while (index != -1) {
            result.add(str.substring(lastIndex, index))
            lastIndex = index + delimiter.length
            index = str.indexOf(delimiter, index + delimiter.length)
        }
        result.add(str.substring(lastIndex, str.length))
        return result.toTypedArray()
    }

    /*
        * Replace all occurances of the searchString in the originalString with the replaceString.  Faster than the
        * String.replace() method.  Does not use regexes.
        * <p/>
        * If your searchString is empty, this will spin forever.
        *
        *
        * @param originalString
        * @param searchString
        * @param replaceString
        * @return
        */
    fun replace(originalString: String?, searchString: String, replaceString: String): String {
        val sb = StringBuilder(originalString!!)
        var index = sb.indexOf(searchString)
        while (index != -1) {
            sb.replace(index, index + searchString.length, replaceString)
            index += replaceString.length
            index = sb.indexOf(searchString, index)
        }
        return sb.toString()
    }

    fun join(delimiter: String?, strings: Array<String?>): String {
        if (strings.isEmpty()) {
            return ""
        }
        val stringBuilder = StringBuilder()
        for (str in strings) {
            stringBuilder.append(" ").append(str)
        }
        return stringBuilder.substring(1)
    }

    fun computeLevenshteinDistance(cs1: CharSequence, cs2: CharSequence): Int {
        var str1 = cs1
        var str2 = cs2
        val commonPrefixLength = findCommonPrefixLength(str1, str2)
        if (commonPrefixLength == str1.length && commonPrefixLength == str2.length) {
            return 0 // same exact string
        }
        val commonSuffixLength = findCommonSuffixLength(str1, str2, commonPrefixLength)
        str1 = str1.subSequence(commonPrefixLength, str1.length - commonSuffixLength)
        str2 = str2.subSequence(commonPrefixLength, str2.length - commonSuffixLength)
        val distance = Array(str1.length + 1) { IntArray(str2.length + 1) }
        for (i in 0..str1.length) {
            distance[i][0] = i
        }
        for (j in 0..str2.length) {
            distance[0][j] = j
        }
        for (i in 1..str1.length) {
            for (j in 1..str2.length) {
                distance[i][j] = minimum(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + if (str1[i - 1] == str2[j - 1]) 0 else 1)
            }
        }
        return distance[str1.length][str2.length]
    }

    private fun findCommonPrefixLength(str1: CharSequence, str2: CharSequence): Int {
        val length = Math.min(str1.length, str2.length)
        for (i in 0 until length) {
            if (str1[i] != str2[i]) {
                return i
            }
        }
        return 0
    }

    private fun findCommonSuffixLength(str1: CharSequence, str2: CharSequence, commonPrefixLength: Int): Int {
        val length = Math.min(str1.length, str2.length)
        for (i in 0 until length - commonPrefixLength) {
            if (str1[str1.length - i - 1] != str2[str2.length - i - 1]) {
                return i
            }
        }
        return 0
    }

    private fun minimum(a: Int, b: Int, c: Int): Int {
        return a.coerceAtMost(b).coerceAtMost(c)
    }

    fun join(arr: IntArray, delimiter: String): String {
        if (arr.isEmpty()) {
            return ""
        }
        val sb = StringBuilder()
        for (i in arr) {
            sb.append(delimiter).append(Integer.toString(i))
        }
        return sb.substring(delimiter.length)
    }

    fun capitalize(str: String?): String {
        val sb = StringBuilder(str!!)
        for (i in sb.indices) {
            if (i == 0 || Character.isWhitespace(sb[i - 1])) {
                sb.replace(i, i + 1, Character.toUpperCase(sb[i]).toString())
            }
        }
        return sb.toString()
    }

    @JvmStatic
    fun nullToEmpty(str: CharSequence?): String {
        return str?.toString() ?: ""
    }

    fun isEmptyOrWhitespaceOnly(str: String): Boolean {
        if (TextUtils.isEmpty(str)) {
            return true
        }
        for (element in str) {
            if (!Character.isWhitespace(element)) {
                return false
            }
        }
        return true
    }

    /**
     * same as String.contains, but ignores case.
     *
     * @param str
     * @param query
     * @return
     */
    @JvmStatic
    fun containsIgnoreCase(str: String?, query: String?): Boolean {
        if (str != null && query != null) {
            val limit = str.length - query.length + 1
            for (i in 0 until limit) {
                if (matchesIgnoreCase(str, query, i)) {
                    return true
                }
            }
        }
        return false
    }

    private fun matchesIgnoreCase(str: String, query: String, startingAt: Int): Boolean {
        val len = query.length
        for (i in 0 until len) {
            if (Character.toUpperCase(query[i]) != Character.toUpperCase(str[startingAt + i])) {
                return false
            }
        }
        return true
    }
}