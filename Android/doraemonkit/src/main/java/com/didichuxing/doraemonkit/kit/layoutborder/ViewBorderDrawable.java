package com.didichuxing.doraemonkit.kit.layoutborder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import com.didichuxing.doraemonkit.config.LayoutBorderConfig;

/**
 * Created by wanglikun on 2019/1/11
 */
public class ViewBorderDrawable extends Drawable {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Rect rect;

    private final Context context;

    public ViewBorderDrawable(View view) {
        rect = new Rect(0, 0, view.getWidth(), view.getHeight());
        context= view.getContext();

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(4);
        paint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (LayoutBorderConfig.isLayoutBorderOpen()) {
            canvas.drawRect(rect, paint);
        }
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        // to be safe
        return PixelFormat.TRANSLUCENT;
    }
}