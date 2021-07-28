package com.didichuxing.doraemonkit.widget.brvah.listener;

import androidx.annotation.Nullable;

/**
 * @author: limuyang
 *  2019-12-05
 * @Description:
 */
public interface DraggableListenerImp {

    void setOnItemDragListener(@Nullable OnItemDragListener onItemDragListener);

    void setOnItemSwipeListener(@Nullable OnItemSwipeListener onItemSwipeListener);
}
