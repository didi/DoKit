package com.didichuxing.doraemonkit.widget.viewpager

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import com.didichuxing.doraemonkit.widget.viewpager.transfer.DefaultTransformer

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-27-14:53
 * 描    述：
 * 修订历史：update by pengyushan 2020-07-06
 * ================================================
 */
class VerticalViewPager @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) : ViewPager(context!!, attrs) {
    private fun swapTouchEvent(event: MotionEvent): MotionEvent {
        val width = width.toFloat()
        val height = height.toFloat()
        val swappedX = event.y / height * width
        val swappedY = event.x / width * height
        event.setLocation(swappedX, swappedY)
        return event
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val intercept = super.onInterceptTouchEvent(swapTouchEvent(event))
        swapTouchEvent(event)
        return intercept
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return super.onTouchEvent(swapTouchEvent(ev))
    }

    init {
        setPageTransformer(false, DefaultTransformer())
    }
}