package com.didichuxing.doraemonkit.util

import java.util.Date

/**
 * Created by wangxueying on 2020-06-30.
 */
object FormatUtil {

    fun format(time: Long): String {
        return Date(time).toString()
    }
}