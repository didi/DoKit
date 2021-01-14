package com.didichuxing.doraemonkit.widget.tableview;

import android.graphics.Paint;
import android.graphics.Rect;


import com.didichuxing.doraemonkit.widget.tableview.bean.Cell;
import com.didichuxing.doraemonkit.widget.tableview.bean.Column;
import com.didichuxing.doraemonkit.widget.tableview.bean.ColumnInfo;
import com.didichuxing.doraemonkit.widget.tableview.bean.TableData;
import com.didichuxing.doraemonkit.widget.tableview.bean.TableInfo;
import com.didichuxing.doraemonkit.widget.tableview.intface.IComponent;
import com.didichuxing.doraemonkit.widget.tableview.intface.ITableTitle;

import java.util.List;

public class TableMeasurer<T> {

    private boolean isReMeasure; //是否重新计算

    public TableInfo measure(TableData<T> tableData, int width, int height) {
        isReMeasure = true;
        TableInfo tableInfo = tableData.getTableInfo();

        width = Math.max(getTableWidth(tableData), width);
        height = Math.max(getTableHeight(tableData), height);

        tableInfo.setTableRect(new Rect(0, 0, width, height));
        measureColumnSize(tableData);
        return tableInfo;
    }


    public void measureTableTitle(TableData<T> tableData, ITableTitle tableTitle, Rect showRect) {
        TableInfo tableInfo = tableData.getTableInfo();
        Rect tableRect = tableInfo.getTableRect();
        if (isReMeasure) {
            isReMeasure = false;
            int size = tableTitle.getSize();
            tableInfo.setTitleDirection(tableTitle.getDirection());
            tableInfo.setTableTitleSize(size);
            if (tableTitle.getDirection() == IComponent.TOP ||
                    tableTitle.getDirection() == IComponent.BOTTOM) {
                int height = size;
                tableRect.bottom += height;
                reSetShowRect(showRect, tableRect);
            } else {
                int width = size;
                tableRect.right += width;
                reSetShowRect(showRect, tableRect);
            }
        } else {
            reSetShowRect(showRect, tableRect);
        }

    }

    /**
     * 重新计算显示大小
     *
     * @param showRect
     * @param tableRect
     */
    public void reSetShowRect(Rect showRect, Rect tableRect) {
        if (showRect.bottom > tableRect.bottom) {
            showRect.bottom = tableRect.bottom;
        }
        if (showRect.right > tableRect.right) {
            showRect.right = tableRect.right;
        }
    }

    /**
     * 添加table高度
     *
     * @param tableData
     * @return
     */
    public void addTableHeight(TableData<T> tableData) {
//        TableInfo tableInfo = tableData.getTableInfo();
//        Rect tableRect = tableInfo.getTableRect();
//        int[] lineArray = tableInfo.getLineHeightArray();
//        for(int i = startPosition;i<lineArray.length;i++){
//           tableRect.bottom+= lineArray[i];
//        }
        TableInfo tableInfo = tableData.getTableInfo();
        int width = getTableWidth(tableData);
        int height = getTableHeight(tableData);
        tableInfo.setTableRect(new Rect(0, 0, width, height));
    }


    /**
     * 计算table高度
     *
     * @param tableData
     * @param config
     * @return
     */
    private int getTableHeight(TableData<T> tableData) {
        TableConfig config = TableConfig.getInstance();
        int topHeight = 0;
        int titleHeight = config.isShowColumnTitle() ? (tableData.getTitleDrawFormat().measureHeight(config)
                + 2 * config.getColumnTitleVerticalPadding()) : 0;
        TableInfo tableInfo = tableData.getTableInfo();
        tableInfo.setTitleHeight(titleHeight);
        tableInfo.setTopHeight(topHeight);
        int totalContentHeight = 0;
        for (int height : tableInfo.getLineHeightArray()) {
            totalContentHeight += height;
        }
        int totalTitleHeight = titleHeight * tableInfo.getMaxLevel();
        int totalHeight = topHeight + totalTitleHeight + totalContentHeight;

        return totalHeight;
    }

    /**
     * 计算table宽度
     *
     * @param tableData
     * @param config
     * @return
     */
    private int getTableWidth(TableData<T> tableData) {
        int totalWidth = 0;
        TableConfig config = TableConfig.getInstance();
        Paint paint = config.getPaint();
        config.YSequenceStyle.fillPaint(paint);
        int totalSize = tableData.getLineSize();
        //计算Y轴宽度距离
        if (config.isShowYSequence()) {
            int yAxisWidth = (int) paint.measureText(tableData.getYSequenceFormat().format(totalSize)
                    + 2 * config.getSequenceHorizontalPadding());
            tableData.getTableInfo().setyAxisWidth(yAxisWidth);
            totalWidth += yAxisWidth;
        }
        int columnPos = 0;
        int contentWidth = 0;
        int[] lineHeightArray = tableData.getTableInfo().getLineHeightArray();
        int currentPosition, size;
        for (Column column : tableData.getChildColumns()) {
            float columnNameWidth = tableData.getTitleDrawFormat().measureWidth(column, config) + config.getColumnTitleHorizontalPadding() * 2;
            int columnWidth = 0;
            size = column.getDatas().size();
            currentPosition = 0;
            Cell[][] rangeCells = tableData.getTableInfo().getRangeCells();
            for (int position = 0; position < size; position++) {
                int width = column.getDrawFormat().measureWidth(column, position);
                measureRowHeight(lineHeightArray, column, currentPosition, position);
                currentPosition += 1;
                //为了解决合并单元宽度过大问题
                if (rangeCells != null) {
                    Cell cell = rangeCells[position][columnPos];
                    if (cell != null) {
                        if (cell.row != Cell.INVALID && cell.col != Cell.INVALID) {
                            cell.width = width;
                            width = width / cell.col;
                        } else if (cell.realCell != null) {
                            width = cell.realCell.width / cell.realCell.col;
                        }

                    }
                }
                if (columnWidth < width) {
                    columnWidth = width;
                }
            }
            int width = (int) (Math.max(columnNameWidth, columnWidth + 2 * config.getHorizontalPadding()));
            width = Math.max(column.getMinWidth(), width);
            column.setComputeWidth(width);
            contentWidth += width;
            columnPos++;
        }
        int minWidth = config.getMinTableWidth();
        //计算出来的宽度大于最小宽度
        if (minWidth == -1 || minWidth - totalWidth < contentWidth) {
            totalWidth += contentWidth;
        } else {
            minWidth -= totalWidth;
            float widthScale = ((float) minWidth) / contentWidth;
            for (Column column : tableData.getChildColumns()) {
                column.setComputeWidth((int) (widthScale * column.getComputeWidth()));
            }
            totalWidth += minWidth;
        }
        return totalWidth;
    }


    /**
     * 测量行高
     *
     * @param config
     * @param lineHeightArray
     * @param column
     * @param position
     */
    private void measureRowHeight(int[] lineHeightArray, Column column, int currentPosition, int position) {
        TableConfig config = TableConfig.getInstance();

        int height = 0;
        if (height == 0) {
            height = column.getDrawFormat().measureHeight(column, position) +
                    2 * config.getVerticalPadding();
        }
        height = Math.max(column.getMinHeight(), height);
        if (height > lineHeightArray[currentPosition]) {
            lineHeightArray[currentPosition] = height;
        }
    }

    /**
     * 测量列的Rect
     *
     * @param tableData
     */
    private void measureColumnSize(TableData<T> tableData) {
        List<Column> columnList = tableData.getColumns();
        int left = 0;
        int maxLevel = tableData.getTableInfo().getMaxLevel();
        tableData.getColumnInfos().clear();
        tableData.getChildColumnInfos().clear();
        for (int i = 0; i < columnList.size(); i++) {
            int top = 0;
            Column column = columnList.get(i);
            ColumnInfo columnInfo = getColumnInfo(tableData, column, null, left, top, maxLevel);
            left += columnInfo.width;
        }
    }

    public ColumnInfo getColumnInfo(TableData<T> tableData, Column column, ColumnInfo parent, int left, int top, int overLevel) {
        TableInfo tableInfo = tableData.getTableInfo();
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.value = column.getColumnName();
        columnInfo.column = column;
        columnInfo.width = column.getComputeWidth();
        columnInfo.top = top;
        columnInfo.height = tableInfo.getTitleHeight() * overLevel;
        columnInfo.left = left;

        tableData.getChildColumnInfos().add(columnInfo);
        tableData.getColumnInfos().add(columnInfo);


        return columnInfo;
    }


}
