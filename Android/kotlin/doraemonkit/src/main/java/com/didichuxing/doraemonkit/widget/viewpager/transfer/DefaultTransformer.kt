package com.didichuxing.doraemonkit.widget.viewpager.transfer

import android.view.View
import androidx.viewpager.widget.ViewPager.PageTransformer

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-27-14:55
 * 描    述：
 * 修订历史：update by pengyushan 2020-07-06
 * ================================================
 */
class DefaultTransformer : PageTransformer {
    override fun transformPage(view: View, position: Float) {
        var alpha = 0f
        if (position in 0.0..1.0) {
            alpha = 1 - position
        } else if (-1 < position && position < 0) {
            alpha = position + 1
        }
        view.alpha = alpha
        view.translationX = view.width * -position
        val yPosition = position * view.height
        view.translationY = yPosition
    }
}