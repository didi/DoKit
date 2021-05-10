package com.didichuxing.doraemonkit.kit.viewcheck;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.R;

/**
 * Created by wanglikun on 2018/12/3.
 */

public class AimCircleView extends View {
    private Paint mPaint;

    public AimCircleView(Context context) {
        super(context);
        init();
    }

    public AimCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AimCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);
    }

    private void drawCircle(Canvas canvas) {
        float cx = getWidth() / 2;
        float cy = getWidth() / 2;
        float radius = getWidth() / 2;

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.dk_color_FFFFFF));
        mPaint.setAlpha(100);
        canvas.drawCircle(cx, cy, radius, mPaint);

        radius = getResources().getDimensionPixelSize(R.dimen.dk_dp_40) / 2;
        mPaint.setColor(getResources().getColor(R.color.dk_color_30CC3A4B));
        mPaint.setAlpha(50);
        canvas.drawCircle(cx, cy, radius, mPaint);

        radius = getResources().getDimensionPixelSize(R.dimen.dk_dp_5) / 2;
        mPaint.setColor(getResources().getColor(R.color.dk_color_CC3A4B));
        canvas.drawCircle(cx, cy, radius, mPaint);

        radius = getWidth() / 2;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setColor(getResources().getColor(R.color.dk_color_337CC4));
        mPaint.setAlpha(100);
        canvas.drawCircle(cx, cy, radius - 2, mPaint);
    }
}