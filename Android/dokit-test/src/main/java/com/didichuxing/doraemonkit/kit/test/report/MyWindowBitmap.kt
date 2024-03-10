package com.didichuxing.doraemonkit.kit.test.report

import android.graphics.Bitmap
import android.graphics.Rect
import android.view.View
import android.view.ViewParent


/**
 * didi Create on 2022/4/1 .
 * <p>
 * Copyright (c) 2022/4/1 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/1 4:25 下午
 * @Description 截屏图片 ，用于合成屏幕截图
 */

data class MyWindowBitmap(
    val parent: ViewParent,
    val view: View,
    val bitmap: Bitmap?,
    val winFrame: Rect,
    val appVisible: Boolean,
    val decorView: Boolean = false,
    val doKitView: Boolean = false
)
