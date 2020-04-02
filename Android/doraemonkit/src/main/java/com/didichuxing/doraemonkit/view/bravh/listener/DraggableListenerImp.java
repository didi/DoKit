package com.didichuxing.doraemonkit.view.bravh.listener;

import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.view.bravh.listener.OnItemDragListener;
import com.didichuxing.doraemonkit.view.bravh.listener.OnItemSwipeListener;

/**
 * @author: limuyang
 * @date: 2019-12-05
 * @Description:
 */
public interface DraggableListenerImp {

    void setOnItemDragListener(@Nullable OnItemDragListener onItemDragListener);

    void setOnItemSwipeListener(@Nullable OnItemSwipeListener onItemSwipeListener);
}
