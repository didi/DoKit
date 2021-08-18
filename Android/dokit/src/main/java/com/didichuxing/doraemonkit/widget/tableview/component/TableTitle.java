package com.didichuxing.doraemonkit.widget.tableview.component;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import com.didichuxing.doraemonkit.widget.tableview.TableConfig;
import com.didichuxing.doraemonkit.widget.tableview.intface.ITableTitle;
import com.didichuxing.doraemonkit.widget.tableview.utils.DrawUtils;

public class TableTitle implements ITableTitle {

    private int size = 100;
    private Rect rect = new Rect();
    protected int direction;


    @Override
    public void onDraw(Canvas canvas, Rect showRect, String tableName, TableConfig config) {
        Paint paint = config.getPaint();
        config.tableTitleStyle.fillPaint(paint);
        Rect rect = getRect();
        int startX = rect.centerX();
        Path path = new Path();
        switch (direction) {
            case TOP:
            case BOTTOM:
                DrawUtils.drawMultiText(canvas, paint, rect, tableName.split("\n"));
                break;
            case LEFT:
            case RIGHT:
                int textWidth = (int) paint.measureText(tableName);
                path.moveTo(startX, rect.top);
                path.lineTo(startX, rect.bottom);
                canvas.drawTextOnPath(tableName, path, textWidth / 2, 0, paint);
                break;
        }
    }

    @Override
    public void onMeasure(Rect scaleRect, Rect showRect, TableConfig config) {
        rect.left = showRect.left;
        rect.right = showRect.right;
        rect.top = showRect.top;
        rect.bottom = Math.min(showRect.bottom, scaleRect.bottom);
        int h = size;
        int w = size;
        switch (direction) {
            case TOP:
                rect.bottom = rect.top + h;
                scaleRect.top += h;
                showRect.top += h;
                break;
            case LEFT:
                rect.right = rect.left + w;
                scaleRect.left += w;
                showRect.left += w;
                break;
            case RIGHT:
                rect.left = rect.right - w;
                scaleRect.right -= w;
                showRect.right -= w;
                break;
            case BOTTOM:
                rect.top = rect.bottom - h;
                scaleRect.bottom -= h;
                showRect.bottom -= h;
                break;
        }
    }


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
