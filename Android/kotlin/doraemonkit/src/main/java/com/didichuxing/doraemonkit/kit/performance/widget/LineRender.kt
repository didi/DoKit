package com.didichuxing.doraemonkit.kit.performance.widget

import android.content.Context
import android.graphics.*
import android.text.TextUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.util.UIUtils.dp2px

/**
 * @desc: 折线图渲染器
 */
class LineRender(private val mContext: Context) {
    // 顶部预留50sp,防止文字绘制出界
    private val mPaddingTop = 50

    // 底部预留2dp,防止圆点绘制补全
    private val mPaddingBottom: Int
    private val GRAPH_STROKE_WIDTH = 2
    private val SMALL_RADIUS = 10f
    private val CIRCLE_STROKE_WIDTH = 2f

    //最高值
    private var maxValue = 0f

    //最低值
    private var minValue = 0f
    private var label //显示值
            : String? = null

    //下一个值
    private var nextValue = 0f
    private val mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mGradientPaint = Paint()
    private val mPointPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var viewHeight = 0f
    private var viewWidth = 0f

    //所有点的x坐标
    private var pointX = 0f

    //当前点的Y
    private var pointY = 0f
    private var pointSize = SMALL_RADIUS // 圆点大小
    private var drawRightLine = true //是否画右边的线
    private var showLabel = false
    private var labelAlpha = 0f
    private var startPosition = 0f
    private val baseLine = 20f
    private val mGradientPath = Path()
    fun setMaxValue(maxValue: Int) {
        this.maxValue = maxValue.toFloat()
    }

    fun setMinValue(minValue: Int) {
        this.minValue = minValue.toFloat()
    }

    fun measure(width: Float, height: Float) {
        viewHeight = height - mPaddingBottom - mPaddingTop
        viewWidth = width
        initPaint()
    }

    private fun initPaint() {
        mGradientPaint.shader = LinearGradient(0f, 0f, viewWidth, viewHeight, mContext.resources.getColor(R.color.dk_color_3300BFFF),
                mContext.resources.getColor(R.color.dk_color_33434352), Shader.TileMode.CLAMP)
        mLabelPaint.textSize = mContext.resources.getDimensionPixelSize(R.dimen.dk_font_size_10).toFloat()
        mLabelPaint.color = Color.WHITE
        mLabelPaint.textAlign = Paint.Align.CENTER
        mLinePaint.pathEffect = null
        mLinePaint.style = Paint.Style.FILL
        mLinePaint.color = mContext.resources.getColor(R.color.dk_color_4c00C9F4)
        mLinePaint.strokeWidth = GRAPH_STROKE_WIDTH.toFloat()
        mLinePaint.isAntiAlias = true
        val color = mContext.resources.getColor(R.color.dk_color_ff00C9F4)
        mPointPaint.color = color
        mPointPaint.strokeWidth = CIRCLE_STROKE_WIDTH
    }

    fun draw(canvas: Canvas) {
        drawGraph(canvas)
        drawGradient(canvas)
        drawPoint(canvas)
        drawLabel(canvas)
    }

    /**
     * 画数字
     *
     * @param canvas
     */
    private fun drawLabel(canvas: Canvas) {
        if (showLabel && !TextUtils.isEmpty(label)) {
            mLabelPaint.alpha = (labelAlpha * 0xff).toInt()
            canvas.drawText(label!!, startPosition, pointY - baseLine, mLabelPaint)
        }
    }

    fun setNextValue(nextValue: Float) {
        var nextValue = nextValue
        if (nextValue > maxValue) {
            nextValue = maxValue
        }
        if (nextValue < minValue) {
            nextValue = minValue
        }
        this.nextValue = (1 - nextValue / (maxValue - minValue)) * viewHeight + mPaddingTop
    }

    fun setCurrentValue(index: Int, currentValue: Float) {
        var currentValue = currentValue
        if (currentValue > maxValue) {
            currentValue = maxValue
        }
        if (currentValue < minValue) {
            currentValue = minValue
        }
        startPosition = index * viewWidth
        pointX = startPosition
        pointY = (1 - currentValue / (maxValue - minValue)) * viewHeight + mPaddingTop
    }

    /**
     * 画折线
     *
     * @param canvas
     */
    private fun drawGraph(canvas: Canvas) {
        if (drawRightLine) {
            val middleY = nextValue
            canvas.drawLine(startPosition, pointY, viewWidth + startPosition, middleY, mLinePaint)
        }
    }

    private fun drawGradient(canvas: Canvas) {
        if (drawRightLine) {
            mGradientPath.rewind()
            mGradientPath.moveTo(pointX, pointY)
            mGradientPath.lineTo(pointX, viewHeight + mPaddingTop)
            mGradientPath.lineTo(pointX + viewWidth, viewHeight + mPaddingTop)
            mGradientPath.lineTo(pointX + viewWidth, nextValue)
            canvas.drawPath(mGradientPath, mGradientPaint)
        }
    }

    /**
     * 画数字下面的小圆圈
     *
     * @param canvas
     */
    private fun drawPoint(canvas: Canvas) {
        canvas.drawCircle(pointX, pointY, pointSize, mPointPaint)
    }

    fun setDrawRightLine(drawRightLine: Boolean) {
        this.drawRightLine = drawRightLine
    }

    fun setPointSize(pointSize: Float) {
        if (pointSize != 0f) {
            this.pointSize = pointSize
        }
    }

    fun setLabel(label: String?) {
        this.label = label
    }

    fun setShowLabel(show: Boolean) {
        showLabel = show
    }

    fun setLabelAlpha(alpha: Float) {
        labelAlpha = alpha
    }

    init {
        mPaddingBottom = dp2px(2f)
    }
}