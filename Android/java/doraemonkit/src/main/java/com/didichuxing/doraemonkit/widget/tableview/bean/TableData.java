package com.didichuxing.doraemonkit.widget.tableview.bean;

import com.didichuxing.doraemonkit.widget.tableview.format.NumberSequenceFormat;
import com.didichuxing.doraemonkit.widget.tableview.format.TitleDrawFormat;
import com.didichuxing.doraemonkit.widget.tableview.intface.ISequenceFormat;
import com.didichuxing.doraemonkit.widget.tableview.intface.ITitleDrawFormat;
import com.didichuxing.doraemonkit.widget.tableview.listener.OnColumnItemClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TableData<T> {

    private String tableName;
    private List<Column> columns;
    private List<T> t;
    private List<Column> childColumns;
    private TableInfo tableInfo = new TableInfo();
    private List<ColumnInfo> columnInfos;
    private List<ColumnInfo> childColumnInfos;
    private Column sortColumn;
    private ITitleDrawFormat titleDrawFormat;
    private ISequenceFormat YSequenceFormat;

    private OnItemClickListener onItemClickListener;
    private OnRowClickListener<T> onRowClickListener;
    private OnColumnClickListener<?> onColumnClickListener;

    /**
     * @param tableName 表名
     * @param t         数据
     * @param columns   列列表
     */
    public TableData(String tableName, List<T> t, List<Column> columns) {
        this(tableName, t, columns, null);

    }

    /**
     * @param tableName 表名
     * @param t         数据
     * @param columns   列列表
     */
    public TableData(String tableName, List<T> t, Column... columns) {
        this(tableName, t, Arrays.asList(columns));
    }

    /**
     * @param tableName       表名
     * @param t               数据
     * @param columns         列列表
     * @param titleDrawFormat 列标题绘制格式化
     */
    public TableData(String tableName, List<T> t, List<Column> columns, ITitleDrawFormat titleDrawFormat) {
        this.tableName = tableName;
        this.columns = columns;
        this.t = t;
        tableInfo.setLineSize(t.size());
        childColumns = new ArrayList<>();
        columnInfos = new ArrayList<>();
        childColumnInfos = new ArrayList<>();
        this.titleDrawFormat = titleDrawFormat == null ? new TitleDrawFormat() : titleDrawFormat;
    }


    /**
     * 获取表名
     *
     * @return 表名
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * 设置表名
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * 获取所有列
     *
     * @return 所有列
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * 设置新列列表
     */
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    /**
     * 获取解析数据
     *
     * @return 解析数据
     */
    public List<T> getT() {
        return t;
    }

    /**
     * 设置解析数据
     */
    public void setT(List<T> t) {
        this.t = t;
        tableInfo.setLineSize(t.size());
    }


    /**
     * 获取所有需要显示列数据的列
     * isParent true的列不包含
     *
     * @return 所有需要显示列数据的列
     */
    public List<Column> getChildColumns() {
        return childColumns;
    }

    /**
     * 获取表格信息
     *
     * @return 表格信息tableInfo
     */
    public TableInfo getTableInfo() {
        return tableInfo;
    }

    /**
     * 设置表格信息
     * 一般情况下不会使用到这个方法
     */
    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    /**
     * 获取列信息列表
     *
     * @return 列信息列表
     */
    public List<ColumnInfo> getColumnInfos() {
        return columnInfos;
    }

    /**
     * 获取isParent false列(子列)信息列表
     *
     * @return 子列信息列表
     */
    public List<ColumnInfo> getChildColumnInfos() {
        return childColumnInfos;
    }

    /**
     * 设置子列信息列表
     */
    public void setChildColumnInfos(List<ColumnInfo> childColumnInfos) {
        this.childColumnInfos = childColumnInfos;
    }

    /**
     * 设置列信息列表
     */
    public void setColumnInfos(List<ColumnInfo> columnInfos) {
        this.columnInfos = columnInfos;
    }

    /**
     * 设置子列
     */
    public void setChildColumns(List<Column> childColumns) {
        this.childColumns = childColumns;
    }

    /**
     * 获取需要根据排序的列
     *
     * @return 排序的列
     */
    public Column getSortColumn() {
        return sortColumn;
    }

    /**
     * 设置需要根据排序的列
     */
    public void setSortColumn(Column sortColumn) {
        this.sortColumn = sortColumn;
    }


    /**
     * 获取列标题绘制格式化
     *
     * @return 列标题绘制格式化
     */
    public ITitleDrawFormat getTitleDrawFormat() {
        return titleDrawFormat;
    }

    /**
     * 设置列标题绘制格式化
     * 通过这个方法可以对列名进行格式化
     */
    public void setTitleDrawFormat(ITitleDrawFormat titleDrawFormat) {
        this.titleDrawFormat = titleDrawFormat;
    }

    /**
     * 获取Y序号列文字格式化
     *
     * @return Y序号列文字格式化
     */
    public ISequenceFormat getYSequenceFormat() {
        if (YSequenceFormat == null) {
            YSequenceFormat = new NumberSequenceFormat();
        }
        return YSequenceFormat;
    }

    /**
     * 设置Y序号列文字格式化
     */
    public void setYSequenceFormat(ISequenceFormat YSequenceFormat) {
        this.YSequenceFormat = YSequenceFormat;
    }

    /**
     * 获取包含ID的子列
     *
     * @param id 列ID
     * @return 包含ID的子列
     */
    public Column getColumnByID(int id) {
        List<Column> columns = getChildColumns();
        for (Column column : columns) {
            if (column.getId() == id) {
                return column;
            }
        }
        return null;
    }


    /**
     * 获取行数
     *
     * @return 行数
     */
    public int getLineSize() {
        return tableInfo.getLineHeightArray().length;
    }

    public void clear() {
        if (t != null) {
            t.clear();
            t = null;
        }
        if (childColumns != null) {
            childColumns.clear();
            childColumns = null;
        }
        if (columns != null) {
            columns = null;
        }
        if (childColumnInfos != null) {
            childColumnInfos.clear();
            childColumnInfos = null;
        }
  /*      if(cellRangeAddresses !=null){
            cellRangeAddresses.clear();
            cellRangeAddresses =null;
        }*/

        if (tableInfo != null) {
            tableInfo.clear();
            tableInfo = null;
        }
        sortColumn = null;
        titleDrawFormat = null;
        YSequenceFormat = null;

    }

    /**
     * 获取表格单元格Cell点击事件
     */
    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;

    }

    /**
     * 设置表格单元格Cell点击事件
     *
     * @param onItemClickListener 点击事件
     */
    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        for (Column column : columns) {
            column.setOnColumnItemClickListener(new OnColumnItemClickListener() {
                @Override
                public void onClick(Column column, String value, Object t, int position) {
                    if (onItemClickListener != null) {
                        int index = childColumns.indexOf(column);
                        TableData.this.onItemClickListener.onClick(column, value, t, index, position);
                    }
                }
            });
        }
    }


    /**
     * 设置表格行点击事件
     *
     * @param onRowClickListener 行点击事件
     */
    public void setOnRowClickListener(final OnRowClickListener<T> onRowClickListener) {
        this.onRowClickListener = onRowClickListener;
        if (this.onRowClickListener != null) {
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onClick(Column column, String value, Object o, int col, int row) {
                    TableData.this.onRowClickListener.onClick(column, t.get(row), col, row);
                }
            });
        }

    }


    /**
     * 设置表格列点击事件
     */
    public void setOnColumnClickListener(final OnColumnClickListener onColumnClickListener) {
        this.onColumnClickListener = onColumnClickListener;
        if (this.onRowClickListener != null) {
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onClick(Column column, String value, Object o, int col, int row) {
                    TableData.this.onColumnClickListener.onClick(column, column.getDatas(), col, row);
                }
            });
        }
    }


    public OnRowClickListener getOnRowClickListener() {
        return onRowClickListener;
    }

    /**
     * 表格单元格Cell点击事件接口
     */
    public interface OnItemClickListener<T> {
        void onClick(Column<T> column, String value, T t, int col, int row);
    }

    /**
     * 表格行点击事件接口
     */
    public interface OnRowClickListener<T> {
        void onClick(Column column, T t, int col, int row);
    }

    public interface OnColumnClickListener<T> {
        void onClick(Column column, List<T> t, int col, int row);
    }
}
