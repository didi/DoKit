package com.didichuxing.doraemonkit.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 * Created by wanglikun on 2018/10/30.
 */
object ImageUtil {
    fun decodeSampledBitmapFromFilePath(imagePath: String?,
                                        reqWidth: Int, reqHeight: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, options)
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(imagePath, options)
    }

    fun calculateInSampleSize(options: BitmapFactory.Options,
                              reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }

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