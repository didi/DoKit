package com.didichuxing.doraemonkit.widget.tableview.format;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.didichuxing.doraemonkit.widget.tableview.bean.Column;
import com.didichuxing.doraemonkit.widget.tableview.utils.DrawUtils;
import com.didichuxing.doraemonkit.widget.tableview.TableConfig;
import com.didichuxing.doraemonkit.widget.tableview.intface.ITitleDrawFormat;


public class TitleDrawFormat implements ITitleDrawFormat {

    private boolean isDrawBg;

    @Override
    public int measureWidth(Column column, TableConfig config) {
        Paint paint = config.getPaint();
        config.columnTitleStyle.fillPaint(paint);
        return (int) (paint.measureText(column.getColumnName()));
    }


    @Override
    public int measureHeight(TableConfig config) {
        Paint paint = config.getPaint();
        config.columnTitleStyle.fillPaint(paint);
        return DrawUtils.getTextHeight(config.columnTitleStyle,config.getPaint());
    }

    @Override
    public void draw(Canvas c, Column column, Rect rect, TableConfig config) {
        Paint paint = config.getPaint();
        config.columnTitleStyle.fillPaint(paint);

        paint.setTextSize(paint.getTextSize()*config.getZoom());

        drawText(c, column, rect, paint);
    }

    private void drawText(Canvas c, Column column, Rect rect, Paint paint) {
        if(column.getTitleAlign() !=null) { //如果列设置Align ，则使用列的Align
            paint.setTextAlign(column.getTitleAlign());
        }
        c.drawText(column.getColumnName(), DrawUtils.getTextCenterX(rect.left,rect.right,paint), DrawUtils.getTextCenterY((rect.bottom+rect.top)/2,paint) ,paint);
    }

    public boolean isDrawBg() {
        return isDrawBg;
    }

    public void setDrawBg(boolean drawBg) {
        isDrawBg = drawBg;
    }
}
