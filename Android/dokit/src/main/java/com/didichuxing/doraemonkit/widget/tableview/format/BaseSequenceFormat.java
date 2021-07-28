package com.didichuxing.doraemonkit.widget.tableview.format;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.didichuxing.doraemonkit.widget.tableview.utils.DrawUtils;
import com.didichuxing.doraemonkit.widget.tableview.TableConfig;
import com.didichuxing.doraemonkit.widget.tableview.intface.ISequenceFormat;

public abstract class BaseSequenceFormat implements ISequenceFormat {
    @Override
    public void draw(Canvas canvas, int sequence, Rect rect, TableConfig config) {
        //字体缩放
        Paint paint = config.getPaint();
        paint.setTextSize(paint.getTextSize() * (config.getZoom() > 1 ? 1 : config.getZoom()));
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(format(sequence + 1), rect.centerX(), DrawUtils.getTextCenterY(rect.centerY(), paint), paint);
    }
}
