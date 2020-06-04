package com.didichuxing.doraemonkit.kit.alignruler

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.didichuxing.doraemonkit.R

/**
 * @author: xuchun
 * @time: 2020/6/4 - 10:47
 * @desc: 线
 */
class AlignLineView : View {
    private var mTextPaint: Paint? = null
    private var mLinePaint: Paint? = null
    private var mPosX = -1
    private var mPosY = -1

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        mLinePaint = Paint()
        mLinePaint!!.isAntiAlias = true
        mLinePaint!!.color = resources.getColor(R.color.dk_color_CC3A4B)
        mTextPaint = Paint()
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.textSize = resources.getDimensionPixelSize(R.dimen.dk_font_size_14).toFloat()
        mTextPaint!!.color = resources.getColor(R.color.dk_color_333333)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLine(canvas)
        drawText(canvas)
    }

    private fun drawText(canvas: Canvas) {
        if (mPosY == -1 && mPosX == -1) {
            return
        }
        val left = mPosX
        val right = width - mPosX
        val top = mPosY
        val bottom = height - mPosY
        canvas.drawText(left.toString(), left / 2.toFloat(), mPosY.toFloat(), mTextPaint!!)
        canvas.drawText(right.toString(), (mPosX + width) / 2.toFloat(), mPosY.toFloat(), mTextPaint!!)
        canvas.drawText(top.toString(), mPosX.toFloat(), top / 2.toFloat(), mTextPaint!!)
        canvas.drawText(bottom.toString(), mPosX.toFloat(), (mPosY + height) / 2.toFloat(), mTextPaint!!)
    }

    private fun drawLine(canvas: Canvas) {
        if (mPosY == -1 && mPosX == -1) {
            return
        }
        canvas.drawLine(0f, mPosY.toFloat(), width.toFloat(), mPosY.toFloat(), mLinePaint!!)
        canvas.drawLine(mPosX.toFloat(), 0f, mPosX.toFloat(), height.toFloat(), mLinePaint!!)
    }

    fun showInfo(x: Int, y: Int) {
        mPosX = x
        mPosY = y
        invalidate()
    }
}