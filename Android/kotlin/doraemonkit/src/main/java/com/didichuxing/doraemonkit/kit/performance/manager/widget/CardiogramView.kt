package com.didichuxing.doraemonkit.kit.performance.manager.widget

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import com.didichuxing.doraemonkit.kit.performance.manager.datasource.IDataSource
import java.util.*

/**
 * @desc: 实现平滑移动的折线图
 */
class CardiogramView : View, Runnable {
    private var mItemWidth = 0f

    /**
     * frame count to move an item
     */
    private var mTotalFrameCount = DEFAULT_FRAME_COUNT

    /**
     * current frame count while moving an item ,when it get to mTotalFrameCount,call mList.detach(0)
     */
    private var mCurrentFrameCount = 0
    private var mRender: LineRender? = null
    private val mList = Collections.synchronizedList(ArrayList<LineData>())
    private var mDataSource: IDataSource? = null
    private val mHandler = Handler()

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        mRender = LineRender(context)
        mRender!!.setMaxValue(100)
        mRender!!.setMinValue(0)
        mRender!!.setPointSize(5f)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mItemWidth = w / DEFAULT_ITEM_COUNT
        mRender!!.measure(mItemWidth, h.toFloat())
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        val translateX = canvasTranslate
        canvas.translate(translateX, 0f)
        drawLine(canvas)
        checkFirstItemBound()
        canvas.restore()
    }

    private fun checkFirstItemBound() {
        mCurrentFrameCount++
        if (mCurrentFrameCount >= mTotalFrameCount) {
            mCurrentFrameCount = 0
            if (mDataSource != null) {
                mList.add(mDataSource!!.createData())
            }
            if (mList.size > MAX_ITEM_COUNT) {
                mList.removeAt(0)!!.release()
            }
        }
    }

    private val canvasTranslate= -mItemWidth * (mCurrentFrameCount / mTotalFrameCount.toFloat()) + mItemWidth * (MAX_ITEM_COUNT - mList.size)

    private fun drawLine(canvas: Canvas) {
        // 画波动折线
        var index = 0
        while (index < mList.size.toFloat().coerceAtMost(DEFAULT_ITEM_COUNT + 1)) {
            mRender!!.setCurrentValue(index, mList[index]!!.value)
            // 倒数第二个item显示文字
            when (index) {
                mList.size - 2 -> {
                    mRender!!.setShowLabel(true)
                    mRender!!.setLabelAlpha(1f)
                    mRender!!.setLabel(mList[index]!!.label)
                }
                mList.size - 3 -> {
                    //倒数第三个item显示渐变文字
                    mRender!!.setLabel(mList[index]!!.label)
                    mRender!!.setLabelAlpha(1 - mCurrentFrameCount / mTotalFrameCount.toFloat())
                    mRender!!.setShowLabel(true)
                }
                else -> {
                    mRender!!.setLabel(mList[index]!!.label)
                    mRender!!.setShowLabel(false)
                }
            }
            if (index == mList.size - 1) {
                mRender!!.setNextValue(0f)
                mRender!!.setDrawRightLine(false)
            } else {
                mRender!!.setDrawRightLine(true)
                mRender!!.setNextValue(mList[index + 1]!!.value)
            }
            mRender!!.draw(canvas)
            index++
        }
    }

    fun startMove() {
        mHandler.removeCallbacks(this)
        mHandler.post(this)
    }

    fun stopMove() {
        mHandler.removeCallbacks(this)
    }

    /**
     * 设置滚动间隔
     *
     * @param milliSecond 滚动一个item距离需要的时间，单位毫秒
     */
    fun setInterval(milliSecond: Int) {
        mTotalFrameCount = milliSecond / DEFAULT_FRAME_DELAY
    }

    fun setDataSource(dataSource: IDataSource) {
        mDataSource = dataSource
        mList.clear()
        mList.add(dataSource.createData())
    }

    override fun run() {
        invalidate()
        mHandler.postDelayed(this, DEFAULT_FRAME_DELAY.toLong())
    }

    companion object {
        /**
         * 一屏幕可现实的item个数，默认是12个
         */
        private const val DEFAULT_ITEM_COUNT = 12f

        /**
         * 数组内数据最大值,+2的原因是因为最多有DEFAULT_ITEM_COUNT+1个item被显示（左右各一半），而第13个item必须依赖第14个item的点,才能画出填充区域
         */
        private const val MAX_ITEM_COUNT = DEFAULT_ITEM_COUNT + 2

        /**
         * The default amount of time in ms between animation frames.
         */
        private const val DEFAULT_FRAME_DELAY = 32
        private const val DEFAULT_FRAME_COUNT = 2000 / DEFAULT_FRAME_DELAY
    }
}