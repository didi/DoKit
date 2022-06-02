package com.didichuxing.doraemondemo.module;

/**
 * didi Create on 2022/5/27 .
 * <p>
 * Copyright (c) 2022/5/27 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/5/27 11:41 上午
 * @Description 用一句话说明文件功能
 */


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

public class ShadowDrawable extends Drawable {
    private Paint mPaint;
    private int mShadowRadius;
    private int mShape;
    private int mShapeRadius;
    private int mOffsetX;
    private int mOffsetY;
    private int[] mBgColor;
    private RectF mRect;
    public static final int SHAPE_ROUND = 1;
    public static final int SHAPE_CIRCLE = 2;

    private ShadowDrawable(int shape, int[] bgColor, int shapeRadius, int shadowColor, int shadowRadius, int offsetX, int offsetY) {
        this.mShape = shape;
        this.mBgColor = bgColor;
        this.mShapeRadius = shapeRadius;
        this.mShadowRadius = shadowRadius;
        this.mOffsetX = offsetX;
        this.mOffsetY = offsetY;
        this.mPaint = new Paint();
        this.mPaint.setColor(0);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setShadowLayer((float) shadowRadius, (float) offsetX, (float) offsetY, shadowColor);
        this.mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_ATOP));
    }

    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        this.mRect = new RectF((float) (left + this.mShadowRadius - this.mOffsetX), (float) (top + this.mShadowRadius - this.mOffsetY), (float) (right - this.mShadowRadius - this.mOffsetX), (float) (bottom - this.mShadowRadius - this.mOffsetY));
    }

    public void draw(@NonNull Canvas canvas) {
        Paint newPaint = new Paint();
        if (this.mBgColor != null) {
            if (this.mBgColor.length == 1) {
                newPaint.setColor(this.mBgColor[0]);
            } else {
                newPaint.setShader(new LinearGradient(this.mRect.left, this.mRect.height() / 2.0F, this.mRect.right, this.mRect.height() / 2.0F, this.mBgColor, (float[]) null, TileMode.CLAMP));
            }
        }

        newPaint.setAntiAlias(true);
        if (this.mShape == 1) {
            canvas.drawRoundRect(this.mRect, (float) this.mShapeRadius, (float) this.mShapeRadius, this.mPaint);
            canvas.drawRoundRect(this.mRect, (float) this.mShapeRadius, (float) this.mShapeRadius, newPaint);
        } else {
            canvas.drawCircle(this.mRect.centerX(), this.mRect.centerY(), Math.min(this.mRect.width(), this.mRect.height()) / 2.0F, this.mPaint);
            canvas.drawCircle(this.mRect.centerX(), this.mRect.centerY(), Math.min(this.mRect.width(), this.mRect.height()) / 2.0F, newPaint);
        }

    }

    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
    }

    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;

    }

    public static void setShadowDrawable(View view, Drawable drawable) {
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, (Paint) null);
        ViewCompat.setBackground(view, drawable);
    }

    public static void setShadowDrawable(View view, int shapeRadius, int shadowColor, int shadowRadius, int offsetX, int offsetY) {
        ShadowDrawable drawable = (new ShadowDrawable.Builder()).setShapeRadius(shapeRadius).setShadowColor(shadowColor).setShadowRadius(shadowRadius).setOffsetX(offsetX).setOffsetY(offsetY).builder();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, (Paint) null);
        ViewCompat.setBackground(view, drawable);
    }

    public static void setShadowDrawable(View view, int bgColor, int shapeRadius, int shadowColor, int shadowRadius, int offsetX, int offsetY) {
        ShadowDrawable drawable = (new ShadowDrawable.Builder()).setBgColor(bgColor).setShapeRadius(shapeRadius).setShadowColor(shadowColor).setShadowRadius(shadowRadius).setOffsetX(offsetX).setOffsetY(offsetY).builder();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, (Paint) null);
        ViewCompat.setBackground(view, drawable);
    }

    public static void setShadowDrawable(View view, int shape, int bgColor, int shapeRadius, int shadowColor, int shadowRadius, int offsetX, int offsetY) {
        ShadowDrawable drawable = (new ShadowDrawable.Builder()).setShape(shape).setBgColor(bgColor).setShapeRadius(shapeRadius).setShadowColor(shadowColor).setShadowRadius(shadowRadius).setOffsetX(offsetX).setOffsetY(offsetY).builder();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, (Paint) null);
        ViewCompat.setBackground(view, drawable);
    }

    public static void setShadowDrawable(View view, int[] bgColor, int shapeRadius, int shadowColor, int shadowRadius, int offsetX, int offsetY) {
        ShadowDrawable drawable = (new ShadowDrawable.Builder()).setBgColor(bgColor).setShapeRadius(shapeRadius).setShadowColor(shadowColor).setShadowRadius(shadowRadius).setOffsetX(offsetX).setOffsetY(offsetY).builder();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, (Paint) null);
        ViewCompat.setBackground(view, drawable);
    }

    public static class Builder {
        private int mShape = 1;
        private int mShapeRadius = 12;
        private int mShadowColor = Color.parseColor("#4d000000");
        private int mShadowRadius = 18;
        private int mOffsetX = 0;
        private int mOffsetY = 0;
        private int[] mBgColor;

        public Builder() {
            this.mOffsetX = 0;
            this.mOffsetY = 0;
            this.mBgColor = new int[1];
            this.mBgColor[0] = 0;
        }

        public ShadowDrawable.Builder setShape(int mShape) {
            this.mShape = mShape;
            return this;
        }

        public ShadowDrawable.Builder setShapeRadius(int ShapeRadius) {
            this.mShapeRadius = ShapeRadius;
            return this;
        }

        public ShadowDrawable.Builder setShadowColor(int shadowColor) {
            this.mShadowColor = shadowColor;
            return this;
        }

        public ShadowDrawable.Builder setShadowRadius(int shadowRadius) {
            this.mShadowRadius = shadowRadius;
            return this;
        }

        public ShadowDrawable.Builder setOffsetX(int OffsetX) {
            this.mOffsetX = OffsetX;
            return this;
        }

        public ShadowDrawable.Builder setOffsetY(int OffsetY) {
            this.mOffsetY = OffsetY;
            return this;
        }

        public ShadowDrawable.Builder setBgColor(int BgColor) {
            this.mBgColor[0] = BgColor;
            return this;
        }

        public ShadowDrawable.Builder setBgColor(int[] BgColor) {
            this.mBgColor = BgColor;
            return this;
        }

        public ShadowDrawable builder() {
            return new ShadowDrawable(this.mShape, this.mBgColor, this.mShapeRadius, this.mShadowColor, this.mShadowRadius, this.mOffsetX, this.mOffsetY);
        }
    }
}

