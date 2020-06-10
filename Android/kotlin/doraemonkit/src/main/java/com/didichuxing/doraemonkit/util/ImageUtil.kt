package com.didichuxing.doraemonkit.util

import android.graphics.Bitmap

/**
 * 图片相关的工具类
 * @author Donald Yan
 * @date 2020/6/9
 */
object ImageUtil {

    /**
     * 获取图片指定位置的色值
     */
    fun getPixel(bitmap: Bitmap?, x: Int, y: Int): Int {
        if (bitmap == null) {
            return -1
        }
        if (x < 0 || x > bitmap.width) {
            return -1
        }
        return if (y < 0 || y > bitmap.height) {
            -1
        } else bitmap.getPixel(x, y)
    }
}