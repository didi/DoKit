package com.didichuxing.doraemonkit.widget.tableview.listener;

import com.didichuxing.doraemonkit.widget.tableview.bean.Column;


public interface OnColumnItemClickListener<T> {

    void onClick(Column<T> column, String value, T t, int position);
}
