package com.didichuxing.doraemonkit.kit.viewcheck;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.model.ViewInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglikun on 2018/11/23.
 */

public class LayoutBorderView extends View {
    private static final  String TAG1 = "LayoutBorderView";
    private Paint mRectPaint;
    private List<ViewInfo> mViewInfos = new ArrayList<>();

    public LayoutBorderView(Context context) {
        this(context, null);
    }

    public LayoutBorderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LayoutBorderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);

    }

    private void initView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LayoutBorderView);
        boolean fill = a.getBoolean(R.styleable.LayoutBorderView_dkFill, false);
        mRectPaint = new Paint();
        if (fill) {
            mRectPaint.setStyle(Paint.Style.FILL);
            mRectPaint.setColor(Color.RED);
        } else {
            mRectPaint.setStyle(Paint.Style.STROKE);
            mRectPaint.setStrokeWidth(4);
            mRectPaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
            mRectPaint.setColor(Color.RED);
        }
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (ViewInfo viewInfo : mViewInfos) {
            if (mRectPaint.getStyle() == Paint.Style.FILL) {
                mRectPaint.setAlpha(viewInfo.getDrawTimeLevel() * 255);
            }
            canvas.drawRect(viewInfo.viewRect, mRectPaint);
        }
    }

    public void showViewLayoutBorder(ViewInfo info) {
        mViewInfos.clear();
        if (info != null) {
            mViewInfos.add(info);
        }
        invalidate();
    }

    public void showViewLayoutBorder(List<ViewInfo> viewInfos) {
        if (viewInfos == null) {
            return;
        }
        mViewInfos.clear();
        mViewInfos.addAll(viewInfos);
        invalidate();
    }
}