package com.didichuxing.doraemonkit.kit.viewcheck

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.model.ViewInfo
import java.util.*

/**
 * @author: xuchun
 * @time: 2020/6/4 - 14:47
 * @desc: layout边框View
 */
class LayoutBorderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private lateinit var mRectPaint: Paint
    private val mViewInfoList: MutableList<ViewInfo> = ArrayList()

    companion object {
        private const val TAG1 = "LayoutBorderView"
    }

    init {
        initView(context, attrs, defStyleAttr)
    }

    private fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.LayoutBorderView)
        val fill = a.getBoolean(R.styleable.LayoutBorderView_dkFill, false)
        mRectPaint = Paint()
        with(mRectPaint) {
            if (fill) {
                style = Paint.Style.FILL
                color = Color.RED
            } else {
                style = Paint.Style.STROKE
                strokeWidth = 4f
                pathEffect = DashPathEffect(floatArrayOf(4f, 4f), 0f)
                color = Color.RED
            }
        }

        a.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        for (viewInfo in mViewInfoList) {
            if (mRectPaint.style == Paint.Style.FILL) {
                mRectPaint.alpha = viewInfo.drawTimeLevel * 255
            }
            canvas.drawRect(viewInfo.viewRect, mRectPaint)
        }
    }

    fun showViewLayoutBorder(info: ViewInfo?) {
        mViewInfoList.clear()
        info?.let { mViewInfoList.add(info) }
        invalidate()
    }

    fun showViewLayoutBorder(viewInfos: List<ViewInfo>?) {
        if (null == viewInfos || viewInfos.isEmpty()) {
            return
        }
        mViewInfoList.clear()
        mViewInfoList.addAll(viewInfos)
        invalidate()
    }

}