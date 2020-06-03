package com.didichuxing.doraemonkit.widget.tableview;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.didichuxing.doraemonkit.widget.tableview.bean.Cell;
import com.didichuxing.doraemonkit.widget.tableview.bean.CellInfo;
import com.didichuxing.doraemonkit.widget.tableview.bean.Column;
import com.didichuxing.doraemonkit.widget.tableview.bean.ColumnInfo;
import com.didichuxing.doraemonkit.widget.tableview.bean.TableData;
import com.didichuxing.doraemonkit.widget.tableview.bean.TableInfo;
import com.didichuxing.doraemonkit.widget.tableview.intface.ISelectFormat;
import com.didichuxing.doraemonkit.widget.tableview.listener.OnColumnClickListener;
import com.didichuxing.doraemonkit.widget.tableview.listener.TableClickObserver;
import com.didichuxing.doraemonkit.widget.tableview.utils.DrawUtils;

import java.util.List;
public class TableProvider<T> implements TableClickObserver {


    private Rect scaleRect;
    private Rect showRect;
    private TableConfig config;
    private PointF clickPoint;
    private ColumnInfo clickColumnInfo;
    private boolean isClickPoint;
    private OnColumnClickListener onColumnClickListener;
    /**
     * 选中格子格式化
     */
    private SelectionOperation operation;
    private TableData<T> tableData;
    private Rect clipRect;
    private Rect tempRect; //用于存储数据
    private PointF tipPoint = new PointF();
    private CellInfo cellInfo = new CellInfo();

    public TableProvider() {

        clickPoint = new PointF(-1, -1);
        clipRect = new Rect();
        tempRect = new Rect();
        operation = new SelectionOperation();
        config = TableConfig.getInstance();
    }

    /**
     * 绘制
     *
     * @param canvas    画布
     * @param scaleRect 缩放Rect
     * @param showRect  显示Rect
     * @param tableData 表格数据
     * @param config    配置
     */
    public void onDraw(Canvas canvas, Rect scaleRect, Rect showRect,
                       TableData<T> tableData) {
        setData(scaleRect, showRect, tableData);
        canvas.save();
        canvas.clipRect(this.showRect);
        drawColumnTitle(canvas);
        drawCount(canvas);
        drawContent(canvas);
        operation.draw(canvas, showRect, config);
        canvas.restore();
        if (isClickPoint && clickColumnInfo != null) {
            onColumnClickListener.onClick(clickColumnInfo);
        }
    }

    /**
     * 设置基本信息和清除数据
     *
     * @param scaleRect 缩放Rect
     * @param showRect  显示Rect
     * @param tableData 表格数据
     */
    private void setData(Rect scaleRect, Rect showRect, TableData<T> tableData) {
        isClickPoint = false;
        clickColumnInfo = null;
        operation.reset();
        this.scaleRect = scaleRect;
        this.showRect = showRect;
        this.tableData = tableData;
    }


    private void drawColumnTitle(Canvas canvas) {
        if (config.isShowColumnTitle()) {
            if (config.isFixedTitle()) {
                drawTitle(canvas);
                canvas.restore();
                canvas.save();
                canvas.clipRect(this.showRect);
            } else {
                drawTitle(canvas);
            }
        }
    }

    /**
     * 绘制统计行
     *
     * @param canvas 画布
     */
    private void drawCount(Canvas canvas) {

        float left = scaleRect.left;
        float bottom = config.isFixedCountRow() ? Math.min(scaleRect.bottom, showRect.bottom) : scaleRect.bottom;
        int countHeight = tableData.getTableInfo().getCountHeight();
        float top = bottom - countHeight;
        List<ColumnInfo> childColumnInfos = tableData.getChildColumnInfos();
        if (DrawUtils.isVerticalMixRect(showRect, (int) top, (int) bottom)) {
            List<Column> columns = tableData.getChildColumns();
            int columnSize = columns.size();
            boolean isPerColumnFixed = false;
            clipRect.set(showRect);
            int clipCount = 0;
            for (int i = 0; i < columnSize; i++) {
                Column column = columns.get(i);
                float tempLeft = left;
                float width = column.getComputeWidth() * config.getZoom();
                if (childColumnInfos.get(i).column.isFixed()) {
                    if (left < clipRect.left) {
                        left = clipRect.left;
                        clipRect.left += width;
                        isPerColumnFixed = true;
                    }
                } else if (isPerColumnFixed) {
                    canvas.save();
                    clipCount++;
                    canvas.clipRect(clipRect.left, showRect.bottom - countHeight,
                            showRect.right, showRect.bottom);
                }
                tempRect.set((int) left, (int) top, (int) (left + width), (int) bottom);
                left = tempLeft;
                left += width;
            }
            for (int i = 0; i < clipCount; i++) {
                canvas.restore();
            }
        }
    }

    /**
     * 绘制列标题
     *
     * @param canvas 画布
     */
    private void drawTitle(Canvas canvas) {
        int dis = showRect.top - scaleRect.top;
        TableInfo tableInfo = tableData.getTableInfo();
        int titleHeight = tableInfo.getTitleHeight() * tableInfo.getMaxLevel();
        int clipHeight = config.isFixedTitle() ? titleHeight : Math.max(0, titleHeight - dis);

        clipRect.set(showRect);
        List<ColumnInfo> columnInfoList = tableData.getColumnInfos();
        float zoom = config.getZoom();
        boolean isPerColumnFixed = false;
        int clipCount = 0;
        ColumnInfo parentColumnInfo = null;
        for (ColumnInfo info : columnInfoList) {
            int left = (int) (info.left * zoom + scaleRect.left);
            //根据top ==0是根部，根据最根部的Title判断是否需要固定
            if (info.top == 0 && info.column.isFixed()) {
                if (left < clipRect.left) {
                    parentColumnInfo = info;
                    left = clipRect.left;
                    fillColumnTitle(canvas, info, left);
                    clipRect.left += info.width * zoom;
                    isPerColumnFixed = true;
                    continue;
                }
                //根部需要固定，同时固定所有子类
            } else if (isPerColumnFixed && info.top != 0) {
                left = (int) (clipRect.left - info.width * zoom);
                left += (info.left - parentColumnInfo.left);
            } else if (isPerColumnFixed) {
                canvas.save();
                canvas.clipRect(clipRect.left, showRect.top, showRect.right,
                        showRect.top + clipHeight);
                isPerColumnFixed = false;
                clipCount++;
            }
            fillColumnTitle(canvas, info, left);
        }
        for (int i = 0; i < clipCount; i++) {
            canvas.restore();
        }
        if (config.isFixedTitle()) {
            scaleRect.top += titleHeight;
            showRect.top += titleHeight;
        } else {
            showRect.top += clipHeight;
            scaleRect.top += titleHeight;
        }

    }

    /**
     * 填充列标题
     *
     * @param canvas 画布
     * @param info   列信息
     * @param left   左边
     */
    private void fillColumnTitle(Canvas canvas, ColumnInfo info, int left) {

        int top = (int) (info.top * config.getZoom())
                + (config.isFixedTitle() ? showRect.top : scaleRect.top);
        int right = (int) (left + info.width * config.getZoom());
        int bottom = (int) (top + info.height * config.getZoom());
        if (DrawUtils.isMixRect(showRect, left, top, right, bottom)) {
            if (!isClickPoint && onColumnClickListener != null) {
                if (DrawUtils.isClick(left, top, right, bottom, clickPoint)) {
                    isClickPoint = true;
                    clickColumnInfo = info;
                    clickPoint.set(-1, -1);
                }
            }

            Paint paint = config.getPaint();
            tempRect.set(left, top, right, bottom);
            config.columnTitleGridStyle.fillPaint(paint);
            canvas.drawRect(tempRect, paint);

            tableData.getTitleDrawFormat().draw(canvas, info.column, tempRect, config);

        }
    }

    /**
     * 绘制内容
     *
     * @param canvas 画布
     */
    private void drawContent(Canvas canvas) {
        float top;
        float left = scaleRect.left;
        List<Column> columns = tableData.getChildColumns();
        clipRect.set(showRect);
        TableInfo info = tableData.getTableInfo();
        int columnSize = columns.size();

        if (config.isFixedCountRow()) {
            canvas.save();
            canvas.clipRect(showRect.left, showRect.top, showRect.right, showRect.bottom - info.getCountHeight());
        }
        List<ColumnInfo> childColumnInfo = tableData.getChildColumnInfos();
        boolean isPerFixed = false;
        int clipCount = 0;
        Rect correctCellRect;
        for (int i = 0; i < columnSize; i++) {
            top = scaleRect.top;
            Column column = columns.get(i);
            float width = column.getComputeWidth() * config.getZoom();
            float tempLeft = left;
            //根据根部标题是否固定
            Column topColumn = childColumnInfo.get(i).column;
            if (topColumn.isFixed()) {
                isPerFixed = false;
                if (tempLeft < clipRect.left) {
                    left = clipRect.left;
                    clipRect.left += width;
                    isPerFixed = true;
                }
            } else if (isPerFixed) {
                canvas.save();
                canvas.clipRect(clipRect);
                isPerFixed = false;
                clipCount++;
            }
            float right = left + width;

            if (left < showRect.right) {
                int size = column.getDatas().size();
                int realPosition = 0;
                for (int j = 0; j < size; j++) {
                    String value = column.format(j);
                    int totalLineHeight = 0;
                    for (int k = realPosition; k < realPosition + 1; k++) {
                        totalLineHeight += info.getLineHeightArray()[k];
                    }
                    realPosition += 1;
                    float bottom = top + totalLineHeight * config.getZoom();
                    tempRect.set((int) left, (int) top, (int) right, (int) bottom);
                    correctCellRect = correctCellRect(j, i, tempRect, config.getZoom()); //矫正格子的大小
                    if (correctCellRect != null) {
                        if (correctCellRect.top < showRect.bottom) {
                            if (correctCellRect.right > showRect.left && correctCellRect.bottom > showRect.top) {
                                Object data = column.getDatas().get(j);
                                if (DrawUtils.isClick(correctCellRect, clickPoint)) {
                                    operation.setSelectionRect(i, j, correctCellRect);
                                    tipPoint.x = (left + right) / 2;
                                    tipPoint.y = (top + bottom) / 2;
                                    clickColumn(column, j, value, data);
                                    isClickPoint = true;
                                    clickPoint.set(-Integer.MAX_VALUE, -Integer.MAX_VALUE);
                                }
                                operation.checkSelectedPoint(i, j, correctCellRect);
                                cellInfo.set(column, data, value, i, j);
                                drawContentCell(canvas, cellInfo, correctCellRect);

                            }
                        } else {
                            break;
                        }
                    }
                    top = bottom;
                }
                left = tempLeft + width;
            } else {
                break;
            }
        }
        for (int i = 0; i < clipCount; i++) {
            canvas.restore();
        }
        if (config.isFixedCountRow()) {
            canvas.restore();
        }
    }

    /**
     * 绘制内容格子
     *
     * @param c        画布
     * @param cellInfo 格子信息
     * @param rect     方位
     * @param config   表格配置
     */
    protected void drawContentCell(Canvas c, CellInfo<T> cellInfo, Rect rect) {

        config.contentGridStyle.fillPaint(config.getPaint());
        c.drawRect(rect, config.getPaint());

        rect.left += config.getTextLeftOffset();
        cellInfo.column.getDrawFormat().draw(c, rect, cellInfo);
    }

    /**
     * 点击格子
     *
     * @param column   列
     * @param position 位置
     * @param value    值
     * @param data     数据
     */
    private void clickColumn(Column column, int position, String value, Object data) {
        if (!isClickPoint && column.getOnColumnItemClickListener() != null) {
            column.getOnColumnItemClickListener().onClick(column, value, data, position);
        }
    }

    @Override
    public void onClick(float x, float y) {
        clickPoint.x = x;
        clickPoint.y = y;
    }

    public OnColumnClickListener getOnColumnClickListener() {
        return onColumnClickListener;
    }

    public void setOnColumnClickListener(OnColumnClickListener onColumnClickListener) {
        this.onColumnClickListener = onColumnClickListener;
    }

    public void setSelectFormat(ISelectFormat selectFormat) {
        this.operation.setSelectFormat(selectFormat);
    }

    /**
     * 计算任何point在View的位置
     *
     * @param row 列
     * @param col 行
     * @return
     */
    public int[] getPointLocation(double row, double col) {
        List<Column> childColumns = tableData.getChildColumns();
        int[] lineHeights = tableData.getTableInfo().getLineHeightArray();
        int x = 0, y = 0;
        int columnSize = childColumns.size();
        for (int i = 0; i <= (columnSize > col + 1 ? col + 1 : columnSize - 1); i++) {
            int w = childColumns.get(i).getComputeWidth();
            if (i == (int) col + 1) {
                x += w * (col - (int) col);
            } else {
                x += w;
            }
        }
        for (int i = 0; i <= (lineHeights.length > row + 1 ? row + 1 : lineHeights.length - 1); i++) {
            int h = lineHeights[i];
            if (i == (int) row + 1) {
                y += h * (row - (int) row);
            } else {
                y += h;
            }
        }
        x *= config.getZoom();
        y *= config.getZoom();
        x += scaleRect.left;
        y += scaleRect.top;
        return new int[]{x, y};

    }

    /**
     * 计算任何point在View的大小
     *
     * @param row 列
     * @param col 行
     * @return
     */
    public int[] getPointSize(int row, int col) {
        List<Column> childColumns = tableData.getChildColumns();
        int[] lineHeights = tableData.getTableInfo().getLineHeightArray();
        col = col < childColumns.size() ? col : childColumns.size() - 1;//列
        row = row < lineHeights.length ? row : lineHeights.length;//行
        col = col < 0 ? 0 : col;
        row = row < 0 ? 0 : row;
        return new int[]{(int) (childColumns.get(col).getComputeWidth() * config.getZoom()),
                (int) (lineHeights[row] * config.getZoom())};

    }

    private Rect correctCellRect(int row, int col, Rect rect, float zoom) {
        Cell[][] rangePoints = tableData.getTableInfo().getRangeCells();
        if (rangePoints != null && rangePoints.length > row) {
            Cell point = rangePoints[row][col];
            if (point != null) {
                if (point.col != Cell.INVALID && point.row != Cell.INVALID) {
                    List<Column> childColumns = tableData.getChildColumns();
                    int[] lineHeights = tableData.getTableInfo().getLineHeightArray();
                    int width = 0, height = 0;
                    for (int i = col; i < Math.min(childColumns.size(), col + point.col); i++) {
                        width += childColumns.get(i).getComputeWidth();
                    }
                    for (int i = row; i < Math.min(lineHeights.length, row + point.row); i++) {
                        height += lineHeights[i];
                    }
                    rect.right = (int) (rect.left + width * zoom);
                    rect.bottom = (int) (rect.top + height * zoom);
                    return rect;
                }
                return null;
            }
        }
        return rect;
    }

    public SelectionOperation getOperation() {
        return operation;
    }

}
