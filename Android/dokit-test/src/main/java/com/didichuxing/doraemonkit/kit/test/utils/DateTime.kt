package com.didichuxing.doraemonkit.kit.test.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object DateTime {

    @SuppressLint("SimpleDateFormat")
    fun nowTime(): String {

        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS")
        return df.format(Date())
    }

    fun nowTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}
