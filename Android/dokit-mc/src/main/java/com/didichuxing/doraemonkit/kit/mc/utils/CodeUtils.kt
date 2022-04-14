package com.didichuxing.doraemonkit.kit.mc.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import com.didichuxing.doraemonkit.kit.mc.utils.DensityUtils.dp2px
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/10-17:00
 * 描    述：
 * 修订历史：
 * ================================================
 */
object CodeUtils {
    // 宽度值，影响中间图片大小
    private const val IMAGE_HALFWIDTH = 80

    /**
     * 生成中间带图片的二维码
     *
     * @param context 上下文
     * @param content 二维码内容
     * @param logo    二维码中间的图片
     * @return 生成的二维码图片
     * @throws WriterException 生成二维码异常
     */
    @Throws(WriterException::class)
    fun createCode(context: Context?, content: String?, logo: Bitmap): Bitmap {
        var logo = logo
        val m = Matrix()
        val sx = 2.toFloat() * IMAGE_HALFWIDTH / logo.width
        val sy = 2.toFloat() * IMAGE_HALFWIDTH / logo.height
        // 设置缩放信息
        m.setScale(sx, sy)
        // 将logo图片按martix设置的信息缩放
        logo = Bitmap.createBitmap(
            logo, 0, 0, logo.width,
            logo.height, m, false
        )
        val writer = MultiFormatWriter()
        val hst: Hashtable<EncodeHintType?, Any?> = Hashtable()
        // 设置字符编码
        hst[EncodeHintType.CHARACTER_SET] = "UTF-8"
        // 设置二维码容错率
        hst[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        // 生成二维码矩阵信息
        val matrix = writer.encode(
            content, BarcodeFormat.QR_CODE,
            dp2px(context!!, 300f),
            dp2px(context, 300f), hst
        )
        // 矩阵高度
        val width = matrix.width
        // 矩阵宽度
        val height = matrix.height
        val halfW = width / 2
        val halfH = height / 2
        // 定义数组长度为矩阵高度*矩阵宽度，用于记录矩阵中像素信息
        val pixels = IntArray(width * height)
        // 从行开始迭代矩阵
        for (y in 0 until height) {
            // 迭代列
            for (x in 0 until width) {
                // 该位置用于存放图片信息
                if (x > halfW - IMAGE_HALFWIDTH && x < halfW + IMAGE_HALFWIDTH && y > halfH - IMAGE_HALFWIDTH && y < halfH + IMAGE_HALFWIDTH) {
                    // 记录图片每个像素信息
                    pixels[y * width + x] = logo.getPixel(
                        x - halfW
                                + IMAGE_HALFWIDTH, y - halfH + IMAGE_HALFWIDTH
                    )
                } else {
                    // 如果有黑块点，记录信息
                    if (matrix[x, y]) {
                        // 记录黑块信息
                        pixels[y * width + x] = -0x1000000
                    } else {
                        pixels[y * width + x] = Color.WHITE
                    }
                }
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        // 通过像素数组生成bitmap
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    /**
     * 生成用户的二维码
     *
     * @param context 上下文
     * @param content 二维码内容
     * @return 生成的二维码图片
     * @throws WriterException 生成二维码异常
     */
    @Throws(WriterException::class)
    fun createCode(context: Context?, content: String?): Bitmap {
        //生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        val matrix = MultiFormatWriter().encode(
            content, BarcodeFormat.QR_CODE,
            dp2px(context!!, 250f),
            dp2px(context, 250f)
        )
        val width = matrix.width
        val height = matrix.height
        //二维矩阵转为一维像素数组,也就是一直横着排了
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (matrix[x, y]) {
                    pixels[y * width + x] = -0x1000000
                } else {
                    pixels[y * width + x] = Color.WHITE
                }
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        //通过像素数组生成bitmap
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }
}
