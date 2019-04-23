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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglikun on 2018/11/23.
 */

public class LayoutBorderView extends View {
    private Paint mRectPaint;
    private List<Rect> mRects = new ArrayList<>();

    public LayoutBorderView(Context context) {
        super(context);
        initView();
    }

    public LayoutBorderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LayoutBorderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        for (Rect rect : mRects) {
            canvas.drawRect(rect, mRectPaint);
        }
    }

    public void showViewLayoutBorder(Rect rect) {
        mRects.clear();
        if (rect != null) {
            mRects.add(rect);
        }

        invalidate();
    }

    public void showViewLayoutBorders(List<Rect> rects) {
        if (rects == null) {
            return;
        }
        mRects.clear();
        mRects.addAll(rects);
        invalidate();
    }
}