package com.didichuxing.doraemonkit.kit.performance.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.kit.performance.datasource.IDataSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @desc: 实现平滑移动的折线图
 */
public class CardiogramView extends View implements Runnable {
    /**
     * 一屏幕可现实的item个数，默认是12个
     */
    private static final float DEFAULT_ITEM_COUNT = 12;

    /**
     * 数组内数据最大值,+2的原因是因为最多有DEFAULT_ITEM_COUNT+1个item被显示（左右各一半），而第13个item必须依赖第14个item的点,才能画出填充区域
     */
    private static final float MAX_ITEM_COUNT = DEFAULT_ITEM_COUNT + 2;
    /**
     * The default amount of time in ms between animation frames.
     */
    private static final int DEFAULT_FRAME_DELAY = 32;
    private static final int DEFAULT_FRAME_COUNT = 2000 / DEFAULT_FRAME_DELAY;
    private float mItemWidth;
    /**
     * frame count to move an item
     */
    private int mTotalFrameCount = DEFAULT_FRAME_COUNT;
    /**
     * current frame count while moving an item ,when it get to mTotalFrameCount,call mList.detach(0)
     */
    private int mCurrentFrameCount = 0;
    private LineRender mRender;
    private List<LineData> mList = Collections.synchronizedList(new ArrayList<LineData>());
    private IDataSource mDataSource;

    private Handler mHandler = new Handler();

    public CardiogramView(Context context) {
        super(context);
        init(context);
    }

    public CardiogramView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mRender = new LineRender(context);
        mRender.setMaxValue(100);
        mRender.setMinValue(0);
        mRender.setPointSize(5);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mItemWidth = w / DEFAULT_ITEM_COUNT;
        mRender.measure(mItemWidth, h);
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        float translateX = getCanvasTranslate();
        canvas.translate(translateX, 0);
        drawLine(canvas);
        checkFirstItemBound();
        canvas.restore();
    }

    private void checkFirstItemBound() {
        mCurrentFrameCount++;
        if (mCurrentFrameCount >= mTotalFrameCount) {
            mCurrentFrameCount = 0;
            if (mDataSource != null) {
                mList.add(mDataSource.createData());
            }
            if (mList.size() > MAX_ITEM_COUNT) {
                mList.remove(0).release();
            }
        }
    }

    private float getCanvasTranslate() {
        return -mItemWidth * (mCurrentFrameCount / (float) mTotalFrameCount) + mItemWidth * (MAX_ITEM_COUNT - mList.size());
    }

    private void drawLine(Canvas canvas) {
        // 画波动折线
        for (int index = 0; index < Math.min(mList.size(), DEFAULT_ITEM_COUNT + 1); index++) {
            mRender.setCurrentValue(index, mList.get(index).value);
            // 倒数第二个item显示文字
            if (index == mList.size() - 2) {
                mRender.setShowLabel(true);
                mRender.setLabelAlpha(1f);
                mRender.setLabel(mList.get(index).label);
            } else if (index == mList.size() - 3) {
                //倒数第三个item显示渐变文字
                mRender.setLabel(mList.get(index).label);
                mRender.setLabelAlpha((1 - mCurrentFrameCount / (float) mTotalFrameCount));
                mRender.setShowLabel(true);
            } else {
                mRender.setLabel(mList.get(index).label);
                mRender.setShowLabel(false);
            }
            if (index == mList.size() - 1) {
                mRender.setNextValue(0);
                mRender.setDrawRightLine(false);
            } else {
                mRender.setDrawRightLine(true);
                mRender.setNextValue((mList.get(index + 1).value));
            }
            mRender.draw(canvas);
        }
    }

    public void startMove() {
        mHandler.removeCallbacks(this);
        mHandler.post(this);
    }

    public void stopMove() {
        mHandler.removeCallbacks(this);
    }


    /**
     * 设置滚动间隔
     *
     * @param milliSecond 滚动一个item距离需要的时间，单位毫秒
     */
    public void setInterval(int milliSecond) {
        mTotalFrameCount = milliSecond / DEFAULT_FRAME_DELAY;
    }

    public void setDataSource(@NonNull IDataSource dataSource) {
        mDataSource = dataSource;
        mList.clear();
        mList.add(dataSource.createData());
    }

    @Override
    public void run() {
        invalidate();
        mHandler.postDelayed(this, DEFAULT_FRAME_DELAY);
    }
}