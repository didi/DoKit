package com.didichuxing.doraemonkit.ui.viewcheck;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wanglikun on 2018/11/23.
 */

public class ViewCheckRectView extends View {
    private Paint mRectPaint;
    private Rect mRect;

    public ViewCheckRectView(Context context) {
        super(context);
        initView();
    }

    public ViewCheckRectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ViewCheckRectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mRectPaint = new Paint();
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setColor(Color.RED);
        mRectPaint.setStrokeWidth(2);
        mRectPaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mRect != null) {
            canvas.drawRect(mRect, mRectPaint);
        }
    }

    public void showViewRect(Rect rect) {
        if (rect == null) {
            mRect = null;
        } else {
            mRect = new Rect(rect);
        }
        invalidate();
    }
}