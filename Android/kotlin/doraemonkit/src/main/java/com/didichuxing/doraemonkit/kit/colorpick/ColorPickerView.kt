package com.didichuxing.doraemonkit.kit.colorpick

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.util.ColorUtil

/**
 * 用于拾色的view
 * 强制根据宽度设置成正方形
 * @author Donald Yan
 * @date 2020/6/5
 */
class ColorPickerView : View {

    private var mRingPaint: Paint = Paint()
    private var mBitmapPaint: Paint = Paint()
    private var mFocusPaint: Paint = Paint()
    private var mGridPaint: Paint = Paint()
    private var mGridShadowPaint: Paint
    private var mTextPaint: TextPaint = TextPaint()
    private var mClipPath = Path()
    private var mBitmapMatrix = Matrix()
    private var mGridRect = Rect()
    private var mCircleBitmap: Bitmap? = null
    private var mText: String? = null
    private lateinit var mGridDrawable: RoundedBitmapDrawable

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    init {
        mRingPaint.isAntiAlias = true
        mRingPaint.color = Color.WHITE
        mRingPaint.style = Paint.Style.STROKE

        mFocusPaint.isAntiAlias = true
        mFocusPaint.style = Paint.Style.STROKE
        mFocusPaint.strokeWidth = 3F
        mFocusPaint.color = Color.BLACK

        mBitmapPaint.isFilterBitmap = false

        //设置线宽。单位为1像素
        mGridPaint.strokeWidth = 1F
        mGridPaint.style = Paint.Style.STROKE
        //画笔颜色
        mGridShadowPaint = Paint(mGridPaint)
        mGridShadowPaint.color = -12303292

        mTextPaint.isAntiAlias = true
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.typeface = Typeface.MONOSPACE
        mTextPaint.textSize = resources.getDimensionPixelSize(R.dimen.dk_font_size_12).toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mClipPath.rewind()
        mClipPath.moveTo(0F, 0F)
        val center = width / 2F
        mClipPath.addCircle(center, center, center, Path.Direction.CW)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec))
        val newMeasureSpec = MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY)
        super.onMeasure(newMeasureSpec, newMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBitmap(canvas!!)
        drawGrid(canvas)
        drawRing(canvas)
        drawText(canvas)
        drawFocus(canvas)
    }

    private fun drawBitmap(canvas: Canvas) {
        if (mCircleBitmap == null || mCircleBitmap!!.isRecycled) {
            return
        }
        canvas.save()
        canvas.clipPath(mClipPath)
        mBitmapMatrix.reset()
        mBitmapMatrix.postScale(width / mCircleBitmap!!.width.toFloat(), height / mCircleBitmap!!.height.toFloat())
        canvas.drawBitmap(mCircleBitmap!!, mBitmapMatrix, mBitmapPaint)
        canvas.restore()
    }

    /**
     * 绘制环
     */
    private fun drawRing(canvas: Canvas) {
        // 从canvas层面去除绘制时锯齿
        canvas.drawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val ringWidth = ColorPickConstants.PIX_INTERVAL * 2F + 4F
        mRingPaint.strokeWidth = ringWidth
        val w = width / 2F
        canvas.drawCircle(w, w, w - ringWidth / 2, mRingPaint)
        //绘制环的内外边界
        mRingPaint.color = resources.getColor(R.color.dk_color_333333)
        mRingPaint.strokeWidth = 0.5F
        canvas.drawCircle(w, w, w, mRingPaint)
        canvas.drawCircle(w, w, w - ringWidth, mRingPaint)
    }

    /**
     * 绘制网格
     */
    private fun drawGrid(canvas: Canvas) {
        if (!this::mGridDrawable.isInitialized) {
            val gridBitmap = createGridBitmap(ColorPickConstants.PIX_INTERVAL, canvas)
            mGridDrawable = RoundedBitmapDrawableFactory.create(resources, gridBitmap)
            mGridDrawable.setBounds(0, 0, right, bottom)
            mGridDrawable.isCircular = true
        }
        mGridDrawable.draw(canvas)
    }

    /**
     * 绘制格子
     * @param pixInterval 间隔
     * @param canvas      画布
     */
    private fun createGridBitmap(pixInterval: Int, canvas: Canvas): Bitmap {
        canvas.getClipBounds(mGridRect)
        val gridBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val gridCanvas = Canvas(gridBitmap)
        if (pixInterval > 4) {
            val alpha = (pixInterval * 36).coerceAtMost(255)
            mGridPaint.alpha = alpha
            mGridShadowPaint.alpha = alpha
            var value: Float
            var end: Float
            gridCanvas.save()

            for (index in 0..width step pixInterval) {
                value = (index - 1).toFloat()
                end = height.toFloat()
                //画竖线
                gridCanvas.drawLine(value, 0F, value, end, mGridPaint)
                //画横线
                gridCanvas.drawLine(0F, value, end, value, mGridPaint)

                value = index.toFloat()
                gridCanvas.drawLine(value, 0F, value, end, mGridShadowPaint)
                gridCanvas.drawLine(0F, value, end, value, mGridShadowPaint)
            }
            gridCanvas.restore()
        }
        return gridBitmap
    }

    private fun drawText(canvas: Canvas) {
        if (!TextUtils.isEmpty(mText)) {
            val ringWidth = ColorPickConstants.PIX_INTERVAL * 2 + 4
            val hOffset = (width * Math.PI * (90 * 1.0 / 360)).toFloat()
            val wOffset = (ringWidth - 5).toFloat()
            canvas.drawTextOnPath(mText!!, mClipPath, hOffset, wOffset, mTextPaint)
            canvas.drawFilter = null
        }
    }

    private fun drawFocus(canvas: Canvas) {
        val focusWidth = ColorPickConstants.PIX_INTERVAL + 4F
        val center = width / 2F
        val end = center + focusWidth - 2
        canvas.drawRect(center, center, end, end, mFocusPaint)
    }

    fun setBitmap(bitmap: Bitmap, @ColorInt color: Int, x: Int, y: Int) {
        mCircleBitmap = bitmap
        mRingPaint.color = color
        mText = "${ColorUtil.parseColorInt(color)}   ${x + ColorPickConstants.PIX_INTERVAL}, ${y + ColorPickConstants.PIX_INTERVAL}"
        if (ColorUtil.isColdColor(color)) {
            mFocusPaint.color = Color.WHITE
            mTextPaint.color = Color.WHITE
        }else {
            mFocusPaint.color = Color.BLACK
            mTextPaint.color = Color.BLACK
        }
        invalidate()
    }
}