package com.didichuxing.doraemonkit.widget.tableview;

import android.graphics.Paint;

import com.didichuxing.doraemonkit.widget.tableview.style.FontStyle;
import com.didichuxing.doraemonkit.widget.tableview.style.LineStyle;


public class TableConfig {
    /**
     * 默认字体样式
     */
    private static final FontStyle defaultFontStyle = new FontStyle();
    /**
     * 默认网格样式
     */
    private static final LineStyle defaultGridStyle = new LineStyle();
    /**
     * 无效值
     */
    public static final int INVALID_COLOR = 0;
    /**
     * 内容字体样式
     */
    public FontStyle contentStyle = defaultFontStyle;
    /**
     * 左侧序号列字体样式
     */
    public FontStyle YSequenceStyle = defaultFontStyle;
    /**
     * 列标题字体样式
     */
    public FontStyle columnTitleStyle = defaultFontStyle;
    /**
     * 表格标题字体样式
     */
    public FontStyle tableTitleStyle = defaultFontStyle;
    /**
     * 列标题网格样式
     */
    public LineStyle columnTitleGridStyle = defaultGridStyle;
    /**
     * 序列网格样式
     */
    public LineStyle SequenceGridStyle = defaultGridStyle;
    /**
     * 表格网格
     */
    public LineStyle contentGridStyle = defaultGridStyle;
    /**
     * 上下padding(为了表格的美观，暂只支持统一的padding)
     */
    private int verticalPadding = 10;
    /**
     * 文字左边偏移
     */
    private int textLeftOffset = 0;
    /**
     * 增加列序列左右padding
     */
    private int sequenceHorizontalPadding = 40;

    /**
     * 增加列标题上下padding
     */
    private int columnTitleVerticalPadding = 10;
    /**
     * 增加列标题左右padding
     */
    private int columnTitleHorizontalPadding = 40;
    /**
     * 左右padding(为了表格的美观，暂只支持统一的padding)
     */
    private int horizontalPadding = 40;

    /**
     * 是否显示左侧序号列
     */
    private boolean isShowYSequence = true;
    /**
     * 是否显示表格标题
     */
    private boolean isShowTableTitle = true;
    /**
     * 是否显示列标题
     */
    private boolean isShowColumnTitle = true;
    /**
     * 是否固定左侧
     */
    private boolean fixedYSequence = false;
    /**
     * 固定顶部
     */
    private boolean fixedXSequence = false;
    /**
     * 固定标题
     */
    private boolean fixedTitle = false;
    /**
     * 是否固定统计行
     */
    private boolean fixedCountRow = true;
    /**
     * 左上角空隙背景颜色
     */
    private int leftAndTopBackgroundColor;


    private int minTableWidth = -1;
    /**
     * 画笔
     */
    private Paint paint;
    /**
     * 缩放值
     */
    private float zoom = 1;
    private static TableConfig tableConfig;

    public static TableConfig getInstance() {
        if (tableConfig == null) {
            tableConfig = new TableConfig();
        }
        return tableConfig;
    }

    private TableConfig() {

    }

    public int getVerticalPadding() {
        return verticalPadding;
    }

    public TableConfig setVerticalPadding(int verticalPadding) {
        this.verticalPadding = verticalPadding;
        return this;
    }

    public int getHorizontalPadding() {
        return horizontalPadding;
    }

    public TableConfig setHorizontalPadding(int horizontalPadding) {
        this.horizontalPadding = horizontalPadding;
        return this;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }


    public boolean isFixedYSequence() {
        return fixedYSequence;
    }

    public TableConfig setFixedYSequence(boolean fixedYSequence) {
        this.fixedYSequence = fixedYSequence;
        return this;
    }

    public boolean isFixedXSequence() {
        return fixedXSequence;
    }

    public TableConfig setFixedXSequence(boolean fixedXSequence) {
        this.fixedXSequence = fixedXSequence;
        return this;
    }

    public boolean isFixedTitle() {
        return fixedTitle;
    }

    public boolean isFixedCountRow() {
        return fixedCountRow;
    }

    public TableConfig setFixedCountRow(boolean fixedCountRow) {
        this.fixedCountRow = fixedCountRow;
        return this;
    }

    public boolean isShowYSequence() {
        return isShowYSequence;
    }

    public TableConfig setShowYSequence(boolean showYSequence) {
        isShowYSequence = showYSequence;
        return this;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public int getColumnTitleHorizontalPadding() {
        return columnTitleHorizontalPadding;
    }

    public TableConfig setColumnTitleHorizontalPadding(int columnTitleHorizontalPadding) {
        this.columnTitleHorizontalPadding = columnTitleHorizontalPadding;
        return this;
    }

    public boolean isShowTableTitle() {
        return isShowTableTitle;
    }

    public TableConfig setShowTableTitle(boolean showTableTitle) {
        isShowTableTitle = showTableTitle;
        return this;
    }

    public boolean isShowColumnTitle() {

        return isShowColumnTitle;
    }

    public int getLeftAndTopBackgroundColor() {
        return leftAndTopBackgroundColor;
    }

    public TableConfig setLeftAndTopBackgroundColor(int leftAndTopBackgroundColor) {
        this.leftAndTopBackgroundColor = leftAndTopBackgroundColor;
        return this;
    }

    public TableConfig setShowColumnTitle(boolean showColumnTitle) {
        isShowColumnTitle = showColumnTitle;
        return this;
    }

    public TableConfig setMinTableWidth(int minTableWidth) {
        this.minTableWidth = minTableWidth;
        return this;
    }

    public int getMinTableWidth() {
        return minTableWidth;
    }


    public int getColumnTitleVerticalPadding() {
        return columnTitleVerticalPadding;
    }

    public TableConfig setColumnTitleVerticalPadding(int columnTitleVerticalPadding) {
        this.columnTitleVerticalPadding = columnTitleVerticalPadding;
        return this;
    }


    public int getSequenceHorizontalPadding() {
        return sequenceHorizontalPadding;
    }

    public TableConfig setSequenceHorizontalPadding(int sequenceHorizontalPadding) {
        this.sequenceHorizontalPadding = sequenceHorizontalPadding;
        return this;
    }

    public int getTextLeftOffset() {
        return textLeftOffset;
    }

    public TableConfig setTextLeftOffset(int textLeftOffset) {
        this.textLeftOffset = textLeftOffset;
        return this;
    }
}
