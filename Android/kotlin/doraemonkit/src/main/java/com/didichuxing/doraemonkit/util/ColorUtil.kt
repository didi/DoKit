package com.didichuxing.doraemonkit.util

import android.graphics.Color
import androidx.annotation.ColorInt

/**
 * Created by wanglikun on 2018/9/15.
 */
object ColorUtil {
    fun parseColorInt(@ColorInt color: Int): String {
        return String.format("#%06X", 0xFFFFFF and color)
    }

    fun isColdColor(@ColorInt color: Int): Boolean {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        return hsv[2] <= 0.8f
    }
}