package com.didichuxing.doraemonkit.widget.brvah.dragswipe;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.toolpanel.KitWrapItem;
import com.didichuxing.doraemonkit.widget.brvah.BaseQuickAdapter;
import com.didichuxing.doraemonkit.widget.brvah.module.BaseDraggableModule;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE;


/**
 * @author luoxw
 * @date 2016/6/20
 */
public class DragAndSwipeCallback extends ItemTouchHelper.Callback {
    private static final String TAG = "DragAndSwipeCallback";

    private BaseDraggableModule mDraggableModule;
    private float mMoveThreshold = 0.1f;
    private float mSwipeThreshold = 0.7f;


    private int mDragMoveFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    private int mSwipeMoveFlags = ItemTouchHelper.END;


    public DragAndSwipeCallback(BaseDraggableModule draggableModule) {
        mDraggableModule = draggableModule;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        if (mDraggableModule != null) {
            return mDraggableModule.isDragEnabled() && !mDraggableModule.hasToggleView();
        }
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        if (mDraggableModule != null) {
            return mDraggableModule.isSwipeEnabled();
        }
        return false;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG
                && !isViewCreateByAdapter(viewHolder)) {
            if (mDraggableModule != null) {
                mDraggableModule.onItemDragStart(viewHolder);
            }
            viewHolder.itemView.setTag(R.id.dokit_baseQuickAdapter_dragging_support, true);
        } else if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE
                && !isViewCreateByAdapter(viewHolder)) {
            if (mDraggableModule != null) {
                mDraggableModule.onItemSwipeStart(viewHolder);
            }
            viewHolder.itemView.setTag(R.id.dokit_baseQuickAdapter_swiping_support, true);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (isViewCreateByAdapter(viewHolder)) {
            return;
        }

        if (viewHolder.itemView.getTag(R.id.dokit_baseQuickAdapter_dragging_support) != null
                && (Boolean) viewHolder.itemView.getTag(R.id.dokit_baseQuickAdapter_dragging_support)) {
            if (mDraggableModule != null) {
                mDraggableModule.onItemDragEnd(viewHolder);
            }
            viewHolder.itemView.setTag(R.id.dokit_baseQuickAdapter_dragging_support, false);
        }
        if (viewHolder.itemView.getTag(R.id.dokit_baseQuickAdapter_swiping_support) != null
                && (Boolean) viewHolder.itemView.getTag(R.id.dokit_baseQuickAdapter_swiping_support)) {
            if (mDraggableModule != null) {
                mDraggableModule.onItemSwipeClear(viewHolder);
            }
            viewHolder.itemView.setTag(R.id.dokit_baseQuickAdapter_swiping_support, false);
        }
    }

    /**
     * 这个方法用于让RecyclerView拦截向上滑动，向下滑动，想左滑动
     * makeMovementFlags(dragFlags, swipeFlags);dragFlags是上下方向的滑动 swipeFlags是左右方向上的滑动
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        //设置指定item 不可拖动
        if (viewHolder.getItemViewType() == KitWrapItem.TYPE_TITLE) {
            return makeMovementFlags(ACTION_STATE_IDLE, ACTION_STATE_IDLE);
        }

        if (isViewCreateByAdapter(viewHolder)) {
            return makeMovementFlags(ACTION_STATE_IDLE, ACTION_STATE_IDLE);
        }

        return makeMovementFlags(mDragMoveFlags, mSwipeMoveFlags);
    }

    /**
     * drag状态下，在canDropOver()返回true时，会调用该方法让我们拖动换位置的逻辑(需要自己处理变换位置的逻辑)
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
        return source.getItemViewType() == target.getItemViewType();
    }

    /**
     * 针对drag状态，当前target对应的item是否允许移动
     * 我们一般用drag来做一些换位置的操作，就是当前对应的target对应的Item可以移动
     */
    @Override
    public boolean canDropOver(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder current, @NonNull RecyclerView.ViewHolder target) {
        return mDraggableModule.canDropOver(recyclerView, current, target);
    }

    @Override
    public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
        super.onMoved(recyclerView, source, fromPos, target, toPos, x, y);
        if (mDraggableModule != null) {
            mDraggableModule.onItemDragMoving(source, target);
        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (!isViewCreateByAdapter(viewHolder)) {
            if (mDraggableModule != null) {
                mDraggableModule.onItemSwiped(viewHolder);
            }
        }
    }

    @Override
    public float getMoveThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return mMoveThreshold;
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return mSwipeThreshold;
    }

    /**
     * Set the fraction that the user should move the View to be considered as swiped.
     * The fraction is calculated with respect to RecyclerView's bounds.
     * <p>
     * Default value is .5f, which means, to swipe a View, user must move the View at least
     * half of RecyclerView's width or height, depending on the swipe direction.
     *
     * @param swipeThreshold A float value that denotes the fraction of the View size. Default value
     *                       is .8f .
     */
    public void setSwipeThreshold(float swipeThreshold) {
        mSwipeThreshold = swipeThreshold;
    }


    /**
     * Set the fraction that the user should move the View to be considered as it is
     * dragged. After a view is moved this amount, ItemTouchHelper starts checking for Views
     * below it for a possible drop.
     *
     * @param moveThreshold A float value that denotes the fraction of the View size. Default value is
     *                      .1f .
     */
    public void setMoveThreshold(float moveThreshold) {
        mMoveThreshold = moveThreshold;
    }

    /**
     * <p>Set the drag movement direction.</p>
     * <p>The value should be ItemTouchHelper.UP, ItemTouchHelper.DOWN, ItemTouchHelper.LEFT, ItemTouchHelper.RIGHT or their combination.</p>
     * You can combine them like ItemTouchHelper.UP | ItemTouchHelper.DOWN, it means that the item could only move up and down when dragged.
     *
     * @param dragMoveFlags the drag movement direction. Default value is ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT.
     */

    public void setDragMoveFlags(int dragMoveFlags) {
        mDragMoveFlags = dragMoveFlags;
    }

    /**
     * <p>Set the swipe movement direction.</p>
     * <p>The value should be ItemTouchHelper.START, ItemTouchHelper.END or their combination.</p>
     * You can combine them like ItemTouchHelper.START | ItemTouchHelper.END, it means that the item could swipe to both left or right.
     *
     * @param swipeMoveFlags the swipe movement direction. Default value is ItemTouchHelper.END.
     */
    public void setSwipeMoveFlags(int swipeMoveFlags) {
        mSwipeMoveFlags = swipeMoveFlags;
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE
                && !isViewCreateByAdapter(viewHolder)) {
            View itemView = viewHolder.itemView;

            c.save();
            if (dX > 0) {
                c.clipRect(itemView.getLeft(), itemView.getTop(),
                        itemView.getLeft() + dX, itemView.getBottom());
                c.translate(itemView.getLeft(), itemView.getTop());
            } else {
                c.clipRect(itemView.getRight() + dX, itemView.getTop(),
                        itemView.getRight(), itemView.getBottom());
                c.translate(itemView.getRight() + dX, itemView.getTop());
            }
            if (mDraggableModule != null) {
                mDraggableModule.onItemSwiping(c, viewHolder, dX, dY, isCurrentlyActive);
            }
            c.restore();

        }
    }

    private boolean isViewCreateByAdapter(@NonNull RecyclerView.ViewHolder viewHolder) {
        int type = viewHolder.getItemViewType();
        return type == BaseQuickAdapter.HEADER_VIEW || type == BaseQuickAdapter.LOAD_MORE_VIEW
                || type == BaseQuickAdapter.FOOTER_VIEW || type == BaseQuickAdapter.EMPTY_VIEW;
    }
}
