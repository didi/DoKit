package com.didichuxing.doraemonkit.widget.tableview.intface;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.didichuxing.doraemonkit.widget.tableview.TableConfig;

public interface ISelectFormat {

    void draw(Canvas canvas, Rect rect, Rect showRect, TableConfig config);
}
