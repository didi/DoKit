package com.didichuxing.doraemonkit.widget.tableview.format;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.didichuxing.doraemonkit.widget.tableview.bean.Column;
import com.didichuxing.doraemonkit.widget.tableview.utils.DrawUtils;
import com.didichuxing.doraemonkit.widget.tableview.TableConfig;

import java.util.HashMap;


public class FastTextDrawFormat<T> extends TextDrawFormat<T> {


    private String HEIGHT_KEY = "dk_height";
    private HashMap<String, Integer> cacheMap = new HashMap<>();

    @Override

    public int measureWidth(Column<T> column, int position) {
        int width = 0;
        String value = column.format(position);
        Integer maxLengthValue = cacheMap.get(column.getColumnName());
        if (maxLengthValue == null) {
            width = comperLength(column, value);
        } else if (value.length() > maxLengthValue) {
            width = comperLength(column, value);
        }
        return width;
    }

    private int comperLength(Column<T> column, String value) {
        TableConfig config = TableConfig.getInstance();
        Paint paint = config.getPaint();
        config.contentStyle.fillPaint(paint);
        cacheMap.put(column.getColumnName(), value.length());
        return (int) paint.measureText(value);
    }

    @Override
    public int measureHeight(Column<T> column, int position) {
        int height;
        if (cacheMap.get(HEIGHT_KEY) == null) {
            TableConfig config = TableConfig.getInstance();
            Paint paint = config.getPaint();
            config.contentStyle.fillPaint(paint);
            cacheMap.put(HEIGHT_KEY, DrawUtils.getTextHeight(paint));
        }
        height = cacheMap.get(HEIGHT_KEY);
        return height;
    }


    protected void drawText(Canvas c, String value, Rect rect, Paint paint) {
        DrawUtils.drawSingleText(c, paint, rect, value);
    }


}
