package com.didichuxing.doraemonkit.widget.tableview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.didichuxing.doraemonkit.widget.tableview.intface.ISelectFormat;
import com.didichuxing.doraemonkit.widget.tableview.utils.MatrixHelper;


public class SelectionOperation implements MatrixHelper.OnInterceptListener {
    /**
     * 选中区域
     */
    private static final int INVALID = -1; //无效坐标
    private Rect selectionRect;
    private ISelectFormat selectFormat;
    private int selectRow = INVALID;
    private int selectColumn = INVALID;
    private boolean isShow;

    public void reset() {
        isShow = false;
    }

    public SelectionOperation() {
        this.selectionRect = new Rect();
    }

    public void setSelectionRect(int selectColumn, int selectRow, Rect rect) {
        this.selectRow = selectRow;
        this.selectColumn = selectColumn;
        selectionRect.set(rect);
        isShow = true;
    }

    public boolean isSelectedPoint(int selectColumn, int selectRow) {

        return selectRow == this.selectRow && selectColumn == this.selectColumn;
    }

    public void checkSelectedPoint(int selectColumn, int selectRow, Rect rect) {

        if (isSelectedPoint(selectColumn, selectRow)) {

            selectionRect.set(rect);
            isShow = true;
        }
    }


    public void draw(Canvas canvas, Rect showRect, TableConfig config) {

        if (selectFormat != null && isShow) {
            selectFormat.draw(canvas, selectionRect, showRect, config);
        }
    }

    public ISelectFormat getSelectFormat() {
        return selectFormat;
    }

    public void setSelectFormat(ISelectFormat selectFormat) {
        this.selectFormat = selectFormat;
    }

    @Override
    public boolean isIntercept(MotionEvent e1, float distanceX, float distanceY) {
        return false;
    }


}
