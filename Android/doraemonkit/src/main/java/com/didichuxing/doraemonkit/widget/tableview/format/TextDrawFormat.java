package com.didichuxing.doraemonkit.widget.tableview.format;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;


import com.didichuxing.doraemonkit.widget.tableview.bean.CellInfo;
import com.didichuxing.doraemonkit.widget.tableview.bean.Column;
import com.didichuxing.doraemonkit.widget.tableview.utils.DrawUtils;
import com.didichuxing.doraemonkit.widget.tableview.TableConfig;
import com.didichuxing.doraemonkit.widget.tableview.intface.IDrawFormat;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;


public class TextDrawFormat<T> implements IDrawFormat<T> {


    private Map<String, SoftReference<String[]>> valueMap; //避免产生大量对象

    public TextDrawFormat() {
        valueMap = new HashMap<>();
    }

    @Override
    public int measureWidth(Column<T> column, int position) {

        TableConfig config = TableConfig.getInstance();
        Paint paint = config.getPaint();
        config.contentStyle.fillPaint(paint);
        return DrawUtils.getMultiTextWidth(paint, getSplitString(column.format(position)));
    }


    @Override
    public int measureHeight(Column<T> column, int position) {
        TableConfig config = TableConfig.getInstance();
        Paint paint = config.getPaint();
        config.contentStyle.fillPaint(paint);
        return DrawUtils.getMultiTextHeight(paint, getSplitString(column.format(position)));
    }

    @Override
    public void draw(Canvas c, Rect rect, CellInfo<T> cellInfo) {
        TableConfig config = TableConfig.getInstance();
        Paint paint = config.getPaint();
        setTextPaint(config, cellInfo, paint);
        if (cellInfo.column.getTextAlign() != null) {
            paint.setTextAlign(cellInfo.column.getTextAlign());
        }
        drawText(c, cellInfo.value, rect, paint);
    }

    protected void drawText(Canvas c, String value, Rect rect, Paint paint) {
        DrawUtils.drawMultiText(c, paint, rect, getSplitString(value));
    }


    public void setTextPaint(TableConfig config, CellInfo<T> cellInfo, Paint paint) {
        config.contentStyle.fillPaint(paint);
        paint.setTextSize(paint.getTextSize() * config.getZoom());

    }

    protected String[] getSplitString(String val) {
        String[] values = null;
        if (valueMap.get(val) != null) {
            values = valueMap.get(val).get();
        }
        if (values == null) {
            values = val.split("\n");

            valueMap.put(val, new SoftReference<>(values));
        }
        return values;
    }
}
