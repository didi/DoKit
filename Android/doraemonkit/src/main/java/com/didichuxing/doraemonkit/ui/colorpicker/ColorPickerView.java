package com.didichuxing.doraemonkit.ui.colorpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.AttributeSet;
import android.view.View;

import com.didichuxing.doraemonkit.R;

/**
 * Created by wanglikun on 2018/12/1.
 */

public class ColorPickerView extends View {
    private Drawable mDrawable;
    private Paint mRingPaint;
    private Paint mFocusPaint;

    public ColorPickerView(Context context) {
        super(context);
        init();
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        mRingPaint.setStyle(Paint.Style.STROKE);

        mFocusPaint = new Paint();
        mFocusPaint.setAntiAlias(true);
        mFocusPaint.setColor(getResources().getColor(R.color.dk_color_333333));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBitmap(canvas);
        drawRing(canvas);
        drawFocus(canvas);
    }

    private void drawFocus(Canvas canvas) {
        float focusWidth = getResources().getDimensionPixelSize(R.dimen.dk_dp_6);
        canvas.drawRect(getWidth() / 2 - focusWidth / 2,
                getWidth() / 2 - focusWidth / 2,
                getWidth() / 2 + focusWidth / 2,
                getWidth() / 2 + focusWidth / 2,
                mFocusPaint);
    }

    private void drawRing(Canvas canvas) {
        float ringWidth = getResources().getDimensionPixelSize(R.dimen.dk_dp_5);

        mRingPaint.setColor(getResources().getColor(R.color.dk_color_FFFFFF));
        mRingPaint.setStrokeWidth(ringWidth);
        canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2 - ringWidth / 2, mRingPaint);

        mRingPaint.setColor(getResources().getColor(R.color.dk_color_333333));
        mRingPaint.setStrokeWidth(1);
        canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2, mRingPaint);
        canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2 - ringWidth, mRingPaint);
    }

    private void drawBitmap(Canvas canvas) {
        if (mDrawable == null) {
            return;
        }
        mDrawable.draw(canvas);
    }

    public void setBitmap(Bitmap bitmap) {
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        drawable.setBounds(0, 0, getRight(), getBottom());
        drawable.setCircular(true);
        mDrawable = drawable;
        invalidate();
    }
}