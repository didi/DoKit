package com.didichuxing.doraemonkit.widget.chart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.didichuxing.doraemonkit.util.UIUtils.dp2px

/**
 * @desc: 流量监控圆环view, 使用drawPath方式实现了等间空隙. 调用方式：[PieChart.setData]}
 */
class PieChart : View {
    private var mRenderPaint: Paint? = null
    protected var mTransparentCirclePaint: Paint? = null

    /**
     * View自身的宽和高
     */
    private var mHeight = 0
    private var mWidth = 0

    /**
     * 圆环宽度
     */
    private var mRingWidth = 0f
    private var mSliceSpace = 0f
    private var mPieData: List<PieData>? = null
    private val mPath = Path()
    private val FDEG2RAD = Math.PI.toFloat() / 180f
    private val DEG2RAD = Math.PI / 180.0

    constructor(context: Context?) : super(context) {
        setRingWidth(DEFAULT_RING_WIDTH)
        setSliceSpace(DEFAULT_SLICE_SPACE)
        //初始化画笔
        initPaint()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setRingWidth(DEFAULT_RING_WIDTH)
        setSliceSpace(DEFAULT_SLICE_SPACE)
        //初始化画笔
        initPaint()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mPieData == null) {
            return
        }
        //画圆形
        drawPie(canvas)
        //画镂空透明区域
        drawHolo(canvas)
    }

    /**
     * 初始化画笔
     */
    private fun initPaint() {
        mRenderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mRenderPaint!!.style = Paint.Style.FILL
        mTransparentCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTransparentCirclePaint!!.color = Color.WHITE
        mTransparentCirclePaint!!.style = Paint.Style.FILL
    }

    /**
     * 画圆环
     *
     * @param canvas
     */
    private fun drawPie(canvas: Canvas) {
        val rectF = RectF(0f, 0f, mWidth.toFloat(), mHeight.toFloat())
        val center = Center()
        center.x = mWidth / 2f
        center.y = mHeight / 2f
        val radius = mWidth / 2f
        val sliceSpace = mSliceSpace
        val sliceSpaceAngleOuter = if (mPieData!!.size == 1) 0f else sliceSpace / (FDEG2RAD * radius)
        var sliceAngle: Float
        var angle = 0f
        var startAngleOuter: Float
        var sweepAngleOuter: Float
        for (i in mPieData!!.indices) {
            val data = mPieData!![i]
            mRenderPaint!!.color = data.color
            mPath.reset()
            sliceAngle = data.angel
            startAngleOuter = angle + sliceSpaceAngleOuter / 2f - 90
            sweepAngleOuter = sliceAngle - sliceSpaceAngleOuter
            if (sweepAngleOuter < 0f) {
                sweepAngleOuter = 0f
            }
            angle += sliceAngle
            if (sweepAngleOuter >= 360) {
                mPath.addCircle(center.x, center.y, radius, Path.Direction.CW)
            } else {
                mPath.arcTo(rectF, startAngleOuter, sweepAngleOuter)
                val angleMiddle = startAngleOuter + sweepAngleOuter / 2f
                val arcStartPointX = center.x + radius * Math.cos(startAngleOuter * FDEG2RAD.toDouble()).toFloat()
                val arcStartPointY = center.y + radius * Math.sin(startAngleOuter * FDEG2RAD.toDouble()).toFloat()
                val sliceSpaceOffset = calculateMinimumRadiusForSpacedSlice(
                        center,
                        radius,
                        sliceAngle,
                        arcStartPointX,
                        arcStartPointY,
                        startAngleOuter,
                        sweepAngleOuter)
                val arcEndPointX = center.x +
                        sliceSpaceOffset * Math.cos(angleMiddle * FDEG2RAD.toDouble()).toFloat()
                val arcEndPointY = center.y +
                        sliceSpaceOffset * Math.sin(angleMiddle * FDEG2RAD.toDouble()).toFloat()
                mPath.lineTo(
                        arcEndPointX,
                        arcEndPointY)
            }
            canvas.drawPath(mPath, mRenderPaint!!)
        }
    }

    private fun drawHolo(canvas: Canvas) {
        val holoRadius = mWidth / 2f - mRingWidth
        canvas.drawCircle(mWidth / 2f, mHeight / 2f, holoRadius, mTransparentCirclePaint!!)
    }

    protected fun calculateMinimumRadiusForSpacedSlice(
            center: Center,
            radius: Float,
            angle: Float,
            arcStartPointX: Float,
            arcStartPointY: Float,
            startAngle: Float,
            sweepAngle: Float): Float {
        val angleMiddle = startAngle + sweepAngle / 2f

        // Other point of the arc
        val arcEndPointX = center.x + radius * Math.cos((startAngle + sweepAngle) * FDEG2RAD.toDouble()).toFloat()
        val arcEndPointY = center.y + radius * Math.sin((startAngle + sweepAngle) * FDEG2RAD.toDouble()).toFloat()

        // Middle point on the arc
        val arcMidPointX = center.x + radius * Math.cos(angleMiddle * FDEG2RAD.toDouble()).toFloat()
        val arcMidPointY = center.y + radius * Math.sin(angleMiddle * FDEG2RAD.toDouble()).toFloat()

        // This is the base of the contained triangle
        val basePointsDistance = Math.sqrt(
                Math.pow(arcEndPointX - arcStartPointX.toDouble(), 2.0) +
                        Math.pow(arcEndPointY - arcStartPointY.toDouble(), 2.0))

        // After reducing space from both sides of the "slice",
        //   the angle of the contained triangle should stay the same.
        // So let's find out the height of that triangle.
        val containedTriangleHeight = (basePointsDistance / 2.0 *
                Math.tan((180.0 - angle) / 2.0 * DEG2RAD)).toFloat()

        // Now we subtract that from the radius
        var spacedRadius = radius - containedTriangleHeight

        // And now subtract the height of the arc that's between the triangle and the outer circle
        spacedRadius -= Math.sqrt(
                Math.pow(arcMidPointX - (arcEndPointX + arcStartPointX) / 2f.toDouble(), 2.0) +
                        Math.pow(arcMidPointY - (arcEndPointY + arcStartPointY) / 2f.toDouble(), 2.0)).toFloat()
        return spacedRadius
    }

    /**
     * 设置圆环每个item间隔大小，单位dp
     *
     * @param space
     */
    fun setSliceSpace(space: Float) {
        mSliceSpace = dp2px(space).toFloat()
    }

    /**
     * 设置圆环宽度，单位DP
     *
     * @param ringWidth
     */
    fun setRingWidth(ringWidth: Float) {
        mRingWidth = dp2px(ringWidth).toFloat()
    }

    /**
     * 设置圆环数据
     *
     * @param pieData
     */
    fun setData(pieData: List<PieData>) {
        mPieData = pieData
        var weightSum = 0
        for (data in pieData) {
            weightSum += data.weight.toInt()
        }
        for (data in pieData) {
            if (weightSum == 0) {
                data.angel = 360 / pieData.size.toFloat()
            } else {
                data.angel = data.weight * 360f / weightSum
            }
        }
    }

    class PieData(val color: Int,
                  /**
                   * 比重，角度根据比重算出,angel=(weight/weightSum)*360
                   */
                  val weight: Long) {

        var angel = 0f

    }

    inner class Center {
        var x = 0f
        var y = 0f
    }

    companion object {
        private const val DEFAULT_RING_WIDTH = 19.5f
        private const val DEFAULT_SLICE_SPACE = 2f
    }
}