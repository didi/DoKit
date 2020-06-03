package com.didichuxing.doraemonkit.widget.tableview.bean;

import android.graphics.Paint;

import com.didichuxing.doraemonkit.widget.tableview.format.FastTextDrawFormat;
import com.didichuxing.doraemonkit.widget.tableview.intface.IDrawFormat;
import com.didichuxing.doraemonkit.widget.tableview.intface.IFormat;
import com.didichuxing.doraemonkit.widget.tableview.listener.OnColumnItemClickListener;

import java.util.ArrayList;
import java.util.List;


public class Column<T> implements Comparable<Column> {

    public static final String INVAL_VALUE = "";
    /**
     * 列名
     */
    private String columnName;

    private IFormat<T> format;
    private IDrawFormat<T> drawFormat = new FastTextDrawFormat<T>();
    private List<T> datas;
    private boolean isFixed;
    private int computeWidth;
    private OnColumnItemClickListener<T> onColumnItemClickListener;
    private Paint.Align textAlign;
    private Paint.Align titleAlign;
    private int id;
    private int minWidth;
    private int minHeight;
    private int width;


    /**
     * 列构造方法
     * 用于构造子列
     *
     * @param columnName 列名
     * @param fieldName  需要解析的反射字段
     * @param format     文字格式化
     * @param drawFormat 绘制格式化
     */
    public Column(String columnName, IFormat<T> format, IDrawFormat<T> drawFormat) {
        this.columnName = columnName;
        this.format = format;
        if (drawFormat != null) {
            this.drawFormat = drawFormat;
        }
        datas = new ArrayList<>();
    }


    /**
     * 获取列名
     *
     * @return 列名
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * 设置列名
     *
     * @param columnName 列名
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * 获取文字格式化
     *
     * @return 文字格式化
     */
    public IFormat<T> getFormat() {
        return format;
    }

    /**
     * 设置文字格式化
     */
    public void setFormat(IFormat<T> format) {
        this.format = format;
    }

    /**
     * 获取绘制格式化
     *
     * @return 绘制格式化
     */
    public IDrawFormat<T> getDrawFormat() {
        return drawFormat;
    }

    /**
     * 设置绘制格式化
     */
    public void setDrawFormat(IDrawFormat<T> drawFormat) {
        this.drawFormat = drawFormat;
    }

    /**
     * 获取需要解析的数据
     *
     * @return 数据
     */
    public List<T> getDatas() {
        return datas;
    }

    /**
     * 设置需要解析的数据
     * 直接设置数据，不需要反射获取值
     */
    public void setDatas(List<T> datas) {
        this.datas = datas;
    }


    public String format(int position) {
        if (position >= 0 && position < datas.size()) {
            return format(datas.get(position));
        }
        return INVAL_VALUE;
    }

    public String format(T t) {
        String value;
        if (format != null) {
            value = format.format(t);
        } else {
            value = t == null ? INVAL_VALUE : t.toString();
        }
        return value;
    }

    /**
     * 获取列的计算的宽度
     *
     * @return 宽度
     */
    public int getComputeWidth() {
        return computeWidth;
    }

    /**
     * 设置列的计算宽度
     */
    public void setComputeWidth(int computeWidth) {
        this.computeWidth = computeWidth;
    }

    /**
     * 获取列ID
     *
     * @return ID
     */
    public int getId() {
        return id;
    }

    /**
     * 设置列ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 比较
     */
    @Override
    public int compareTo(Column o) {
        return this.id - o.getId();
    }


    /**
     * 获取点击列监听
     *
     * @return 点击列监听
     */
    public OnColumnItemClickListener<T> getOnColumnItemClickListener() {
        return onColumnItemClickListener;
    }

    /**
     * 设置点击列监听
     */
    public void setOnColumnItemClickListener(OnColumnItemClickListener<T> onColumnItemClickListener) {
        this.onColumnItemClickListener = onColumnItemClickListener;
    }


    /**
     * 判断是否固定
     *
     * @return 是否固定
     */
    public boolean isFixed() {
        return isFixed;
    }

    /**
     * 设置是否固定
     */
    public void setFixed(boolean fixed) {
        isFixed = fixed;
    }

    /**
     * 获取字体位置
     *
     * @return Align
     */
    public Paint.Align getTextAlign() {
        return textAlign;
    }

    /**
     * 设置字体位置
     */
    public void setTextAlign(Paint.Align textAlign) {
        this.textAlign = textAlign;
    }

    public int getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(int minWidth) {
        this.minWidth = minWidth;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }


    public Paint.Align getTitleAlign() {
        return titleAlign;
    }

    /**
     * 设置标题对齐方式
     *
     * @param titleAlign
     */
    public void setTitleAlign(Paint.Align titleAlign) {
        this.titleAlign = titleAlign;
    }

    /**
     * 设置列的宽度
     *
     * @param width
     */
    public void setWidth(int width) {
        if (width > 0) {
            this.width = width;
            this.setDrawFormat(new FastTextDrawFormat<T>());
        }
    }

    /**
     * 获取列的宽度
     *
     * @param
     */
    public int getWidth() {
        if (width == 0) {
            return computeWidth;
        }
        return width;
    }
}
