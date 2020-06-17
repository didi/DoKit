package com.didichuxing.doraemonkit.kit.colorpick

import android.graphics.Bitmap

/**
 * 屏幕图像捕获者接口
 * @author Donald Yan
 * @date 2020/6/16
 */
interface ImageCapturer {
    /**
     * 开始捕获
     */
    fun capture() {

    }

    /**
     * 获取指定坐标位置为中心的指定大小的图片
     */
    fun getPartBitmap(x: Int, y: Int, width: Int, height: Int): Bitmap?

    /**
     * 销毁
     */
    fun destroy()
}