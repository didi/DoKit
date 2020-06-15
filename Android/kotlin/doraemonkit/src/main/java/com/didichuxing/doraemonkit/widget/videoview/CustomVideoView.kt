package com.didichuxing.doraemonkit.widget.videoview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.VideoView
import com.didichuxing.doraemonkit.util.UIUtils.widthPixels
import kotlin.math.abs

/**
 * Created by wanglikun on 2019/4/16
 */
class CustomVideoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        VideoView(context, attrs, defStyleAttr), OnTouchListener {
    private var lastX = 0f
    private var lastY = 0f
    private val thresold = 30
    private var mStateListener: StateListener? = null

    interface StateListener {
        fun changeVolume(deltaY: Float)
        fun changeBrightness(deltaX: Float)
        fun hideHint()
    }

    fun setStateListener(stateListener: StateListener?) {
        mStateListener = stateListener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = View.getDefaultSize(1920, widthMeasureSpec)
        val height = View.getDefaultSize(1080, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.x - lastX
                val deltaY = event.y - lastY
                if (abs(deltaX) < thresold && abs(deltaY) > thresold) {
                    if (event.x < widthPixels / 2) {
                        mStateListener?.changeBrightness(deltaY)
                    } else {
                        mStateListener?.changeVolume(deltaY)
                    }
                }
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_UP -> mStateListener?.hideHint()
        }
        return true
    }

    init {
        setOnTouchListener(this)
    }
}