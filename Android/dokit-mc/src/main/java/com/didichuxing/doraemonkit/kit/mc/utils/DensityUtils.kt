package com.didichuxing.doraemonkit.kit.mc.utils

import android.content.Context

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/10-17:00
 * 描    述：
 * 修订历史：
 * ================================================
 */
/**
 * px与dp互相转换
 * Created by yangle on 2016/4/12.
 */
object DensityUtils {
    fun dp2px(context: Context, dp: Float): Int {
        //获取设备密度
        val density = context.resources.displayMetrics.density
        //4.3, 4.9, 加0.5是为了四舍五入
        return (dp * density + 0.5f).toInt()
    }

    fun px2dp(context: Context, px: Int): Float {
        //获取设备密度
        val density = context.resources.displayMetrics.density
        return px / density
    }
}
