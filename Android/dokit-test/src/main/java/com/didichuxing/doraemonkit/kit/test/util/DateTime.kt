package com.didichuxing.doraemonkit.kit.test.util

import java.text.SimpleDateFormat
import java.util.*

object DateTime {

    fun nowTime(): String {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS")
        return df.format(Date())
    }
}
