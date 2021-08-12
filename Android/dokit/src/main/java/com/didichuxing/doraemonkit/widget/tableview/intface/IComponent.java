package com.didichuxing.doraemonkit.widget.tableview.intface;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.didichuxing.doraemonkit.widget.tableview.TableConfig;

public interface IComponent<T> {

    int LEFT = 0;
    int TOP = 1;
    int RIGHT = 2;
    int BOTTOM = 3;

    /**
     * 计算组件Rect
     */
    void onMeasure(Rect scaleRect, Rect showRect, TableConfig config);

    /**
     * 绘制组件
     *
     * @param canvas 画布
     * @param t      数据
     */
    void onDraw(Canvas canvas, Rect showRect, T t, TableConfig config);


}
