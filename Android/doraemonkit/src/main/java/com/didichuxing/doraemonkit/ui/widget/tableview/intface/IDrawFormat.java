package com.didichuxing.doraemonkit.ui.widget.tableview.intface;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.didichuxing.doraemonkit.ui.widget.tableview.bean.CellInfo;
import com.didichuxing.doraemonkit.ui.widget.tableview.bean.Column;
import com.didichuxing.doraemonkit.ui.widget.tableview.TableConfig;


public interface IDrawFormat<T> {

    /**
     * 测量宽
     */
    int measureWidth(Column<T> column, int position);

    /**
     * 测量高
     */
    int measureHeight(Column<T> column, int position);


    void draw(Canvas c, Rect rect, CellInfo<T> cellInfo);


}
