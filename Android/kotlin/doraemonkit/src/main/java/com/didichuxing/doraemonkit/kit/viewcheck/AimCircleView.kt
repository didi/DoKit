package com.didichuxing.doraemonkit.kit.viewcheck

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.didichuxing.doraemonkit.R

/**
 * @author: xuchun
 * @time: 2020/6/4 - 10:47
 * @desc: 圆形靶子
 */
class AimCircleView : View {
    private lateinit var mPaint: Paint

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    fun init() {
        mPaint = Paint()
        mPaint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCircle(canvas)
    }

    private fun drawCircle(canvas: Canvas) {
        val cx = width / 2.toFloat()
        val cy = width / 2.toFloat()
        var radius = width / 2.toFloat()
        with(mPaint) {
            style = Paint.Style.FILL
            color = resources.getColor(R.color.dk_color_FFFFFF)
            canvas.drawCircle(cx, cy, radius, this)
            radius = resources.getDimensionPixelSize(R.dimen.dk_dp_40) / 2.toFloat()
            color = resources.getColor(R.color.dk_color_30CC3A4B)
            canvas.drawCircle(cx, cy, radius, this)
            radius = resources.getDimensionPixelSize(R.dimen.dk_dp_20) / 2.toFloat()
            color = resources.getColor(R.color.dk_color_CC3A4B)
            canvas.drawCircle(cx, cy, radius, this)
            radius = width / 2.toFloat()
            style = Paint.Style.STROKE
            strokeWidth = 4f
            color = resources.getColor(R.color.dk_color_337CC4)
            canvas.drawCircle(cx, cy, radius - 2, this)
        }

    }
}