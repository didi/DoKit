package com.didichuxing.doraemonkit.view.bravh.listener;

import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.view.bravh.listener.GridSpanSizeLookup;
import com.didichuxing.doraemonkit.view.bravh.listener.OnItemChildClickListener;
import com.didichuxing.doraemonkit.view.bravh.listener.OnItemChildLongClickListener;
import com.didichuxing.doraemonkit.view.bravh.listener.OnItemClickListener;
import com.didichuxing.doraemonkit.view.bravh.listener.OnItemLongClickListener;

/**
 * @author: limuyang
 *  2019-12-03
 * @Description: BaseQuickAdapter需要设置的接口。使用java定义，以兼容java写法
 */
public interface BaseListenerImp {
    /**
     * Register a callback to be invoked when an item in this RecyclerView has
     * been clicked.
     *
     * @param listener The callback that will be invoked.
     */
    void setOnItemClickListener(@Nullable OnItemClickListener listener);

    void setOnItemLongClickListener(@Nullable OnItemLongClickListener listener);

    void setOnItemChildClickListener(@Nullable OnItemChildClickListener listener);

    void setOnItemChildLongClickListener(@Nullable OnItemChildLongClickListener listener);

    void setGridSpanSizeLookup(@Nullable GridSpanSizeLookup spanSizeLookup);
}
