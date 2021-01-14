package com.didichuxing.doraemonkit.widget.tableview.style;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathEffect;

import com.didichuxing.doraemonkit.widget.tableview.intface.IStyle;
import com.didichuxing.doraemonkit.widget.tableview.utils.DensityUtils;


public class LineStyle implements IStyle {

    private float width = -1;
    private int color = -1;
    private boolean isFill;
    private PathEffect effect = new PathEffect();
    private static float defaultLineSize = 2f;
    private static int defaultLineColor = Color.parseColor("#e6e6e6");

    public LineStyle() {

    }


    public LineStyle(float width, int color) {
        this.width = width;
        this.color = color;
    }

    public LineStyle(Context context, float dp, int color) {
        this.width = DensityUtils.dp2px(context, dp);
        this.color = color;
    }

    public static void setDefaultLineSize(float width) {
        defaultLineSize = width;
    }

    public static void setDefaultLineSize(Context context, float dp) {
        defaultLineSize = DensityUtils.dp2px(context, dp);
    }

    public static void setDefaultLineColor(int color) {
        defaultLineColor = color;
    }

    public float getWidth() {
        if (width == -1) {
            return defaultLineSize;
        }
        return width;
    }

    public LineStyle setWidth(float width) {
        this.width = width;
        return this;
    }

    public LineStyle setWidth(Context context, int dp) {
        this.width = DensityUtils.dp2px(context, dp);
        return this;
    }

    public int getColor() {
        if (color == -1) {
            return defaultLineColor;
        }
        return color;
    }

    public boolean isFill() {
        return isFill;
    }

    public LineStyle setFill(boolean fill) {
        isFill = fill;
        return this;
    }

    public LineStyle setColor(int color) {

        this.color = color;
        return this;
    }

    public LineStyle setEffect(PathEffect effect) {
        this.effect = effect;
        return this;
    }

    @Override
    public void fillPaint(Paint paint) {
        paint.setColor(getColor());
        paint.setStyle(isFill ? Paint.Style.FILL : Paint.Style.STROKE);
        paint.setStrokeWidth(getWidth());
        paint.setPathEffect(effect);

    }
}
