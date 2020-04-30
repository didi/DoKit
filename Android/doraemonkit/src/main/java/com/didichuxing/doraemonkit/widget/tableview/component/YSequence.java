package com.didichuxing.doraemonkit.widget.tableview.component;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.didichuxing.doraemonkit.widget.tableview.TableConfig;
import com.didichuxing.doraemonkit.widget.tableview.bean.TableData;
import com.didichuxing.doraemonkit.widget.tableview.bean.TableInfo;
import com.didichuxing.doraemonkit.widget.tableview.intface.IComponent;
import com.didichuxing.doraemonkit.widget.tableview.intface.ISequenceFormat;
import com.didichuxing.doraemonkit.widget.tableview.utils.DrawUtils;

public class YSequence<T> implements IComponent<TableData<T>> {

    private Rect rect;
    private int width;
    private int clipWidth;
    private Rect scaleRect;
    private ISequenceFormat format;
    private Rect tempRect; //临时使用

    public YSequence() {
        rect = new Rect();
        tempRect = new Rect();
    }

    @Override
    public void onMeasure(Rect scaleRect, Rect showRect, TableConfig config) {
        this.scaleRect = scaleRect;
        int scaleWidth = (int) (width * (config.getZoom() > 1 ? 1 : config.getZoom()));
        boolean fixed = config.isFixedYSequence();
        rect.top = scaleRect.top;
        rect.bottom = scaleRect.bottom;
        rect.left = fixed ? showRect.left : scaleRect.left;
        rect.right = rect.left + scaleWidth;
        if (fixed) {
            scaleRect.left += scaleWidth;
            showRect.left += scaleWidth;
            clipWidth = scaleWidth;
        } else {
            int disX = showRect.left - scaleRect.left;
            clipWidth = Math.max(0, scaleWidth - disX);
            showRect.left += clipWidth;
            scaleRect.left += scaleWidth;
        }
    }

    @Override
    public void onDraw(Canvas canvas, Rect showRect, TableData<T> tableData, TableConfig config) {
        format = tableData.getYSequenceFormat();
        float hZoom = (config.getZoom() > 1 ? 1 : config.getZoom());
        int totalSize = tableData.getLineSize();
        TableInfo info = tableData.getTableInfo();
        int topHeight = info.getTopHeight(hZoom);
        float top = rect.top + topHeight;
        int showLeft = showRect.left - clipWidth;
        boolean isFixTop = config.isFixedXSequence();
        int showTop = isFixTop ? (showRect.top + topHeight) : showRect.top;
        float tempTop = top;
        boolean isFixedTitle = config.isFixedTitle();
        boolean isFixedCount = config.isFixedCountRow();
        if (isFixedTitle) {
            int clipHeight;
            if (isFixTop) {
                clipHeight = info.getTopHeight(hZoom);
            } else {
                int disY = showRect.top - scaleRect.top;
                clipHeight = Math.max(0, topHeight - disY);
            }
            tempTop = showRect.top + clipHeight;
        }

        tempRect.set(showLeft, (int) tempTop - topHeight, showRect.left, (int) tempTop);
        drawLeftAndTop(canvas, showRect, tempRect, config);
        canvas.save();
        canvas.clipRect(showLeft, showTop,
                showRect.left, showRect.bottom);
        if (config.isShowColumnTitle()) {
            for (int i = 0; i < info.getMaxLevel(); i++) {
                float bottom = tempTop + info.getTitleHeight();
                if (DrawUtils.isVerticalMixRect(showRect, (int) top, (int) bottom)) {
                    tempRect.set(rect.left, (int) tempTop, rect.right, (int) bottom);
                    drawRect(canvas, tempRect, config);
                }
                tempTop = bottom;
                top += info.getTitleHeight();
            }
        }
        int tempBottom = showRect.bottom;
        if (isFixedTitle || isFixedCount) {
            canvas.save();
            canvas.clipRect(showLeft, tempTop, showRect.left, tempBottom);
        }
        int num = 0;
        for (int i = 0; i < totalSize; i++) {
            num++;
            float bottom = top + info.getLineHeightArray()[i] * config.getZoom();
            if (showRect.bottom >= rect.top) {
                if (DrawUtils.isVerticalMixRect(showRect, (int) top, (int) bottom)) {
                    tempRect.set(rect.left, (int) top, rect.right, (int) bottom);
                    draw(canvas, tempRect, num, config);
                }
            } else {
                break;
            }
            top = bottom;
        }
        if (isFixedTitle || isFixedCount) {
            canvas.restore();
        }
        canvas.restore();

    }

    /**
     * 绘制左上角空隙
     *
     * @param canvas
     * @param rect
     * @param config
     */
    private void drawLeftAndTop(Canvas canvas, Rect showRect, Rect rect, TableConfig config) {
        canvas.save();
        canvas.clipRect(Math.max(this.rect.left, rect.left), showRect.top,
                showRect.left, rect.bottom);
        Paint paint = config.getPaint();
        if (config.getLeftAndTopBackgroundColor() != 0) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(config.getLeftAndTopBackgroundColor());
            canvas.drawRect(rect, paint);
        }

        config.SequenceGridStyle.fillPaint(paint);
        canvas.drawRect(rect, paint);

        canvas.restore();
    }

    private void draw(Canvas canvas, Rect rect, int position, TableConfig config) {
        drawRect(canvas, rect, config);
        format.draw(canvas, position - 1, rect, config);
    }

    private void drawRect(Canvas canvas, Rect rect, TableConfig config) {
        Paint paint = config.getPaint();
        int textColor = TableConfig.INVALID_COLOR;

        config.SequenceGridStyle.fillPaint(paint);
        canvas.drawRect(rect, paint);

        config.YSequenceStyle.fillPaint(paint);

        if (textColor != TableConfig.INVALID_COLOR) {
            paint.setColor(textColor);
        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Rect getRect() {
        return rect;
    }


}
