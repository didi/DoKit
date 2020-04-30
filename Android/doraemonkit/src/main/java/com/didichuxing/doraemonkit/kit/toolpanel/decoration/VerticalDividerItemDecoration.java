package com.didichuxing.doraemonkit.kit.toolpanel.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.DimenRes;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/30-16:09
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class VerticalDividerItemDecoration extends FlexibleDividerDecoration {

    private MarginProvider mMarginProvider;

    protected VerticalDividerItemDecoration(Builder builder) {
        super(builder);
        mMarginProvider = builder.mMarginProvider;
    }

    @Override
    protected Rect getDividerBound(int position, RecyclerView parent, View child) {
        Rect bounds = new Rect(0, 0, 0, 0);
        int transitionX = (int) ViewCompat.getTranslationX(child);
        int transitionY = (int) ViewCompat.getTranslationY(child);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        bounds.top = child.getTop() + transitionY;
        bounds.bottom = child.getBottom() + transitionY;

        int dividerSize = getDividerSize(position, parent);
        if (mDividerType == DividerType.DRAWABLE || mDividerType == DividerType.SPACE) {
            if (alignTopEdge(parent, position)) {
                bounds.top += mMarginProvider.dividerTopMargin(position, parent);
            }
            if (alignBottomEdge(parent, position)) {
                bounds.bottom -= mMarginProvider.dividerBottomMargin(position, parent);
            }

            bounds.left = child.getRight() + params.rightMargin + transitionX;
            bounds.right = bounds.left + dividerSize;
        } else {
            // set center point of divider
            int halfSize = dividerSize / 2;
            bounds.left = child.getRight() + params.rightMargin + halfSize + transitionX;
            bounds.right = bounds.left;
        }

        if (mPositionInsideItem) {
            bounds.left -= dividerSize;
            bounds.right -= dividerSize;
        }

        return bounds;
    }

    private boolean alignTopEdge(RecyclerView parent, int position) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager manager = (GridLayoutManager) layoutManager;
            GridLayoutManager.SpanSizeLookup lookup = manager.getSpanSizeLookup();
            int spanCount = manager.getSpanCount();
            if (manager.getOrientation() == GridLayoutManager.VERTICAL) // 垂直布局
            {
                if (manager.getReverseLayout()) {
                    if (lookup.getSpanGroupIndex(position, spanCount) ==
                            lookup.getSpanGroupIndex(parent.getAdapter().getItemCount() - 1, spanCount)) // 第一行
                    {
                        return true;
                    }
                } else {
                    if (lookup.getSpanGroupIndex(position, spanCount) == 0) // 第一行
                    {
                        return true;
                    }
                }
            } else // 水平布局
            {
                return lookup.getSpanIndex(position, spanCount) == 0;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) layoutManager;
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) manager.findViewByPosition(position).getLayoutParams();
            int spanCount = manager.getSpanCount();
            int spanIndex = params.getSpanIndex();

            if (manager.getOrientation() == StaggeredGridLayoutManager.VERTICAL) // 垂直布局
            {
                if (manager.getReverseLayout()) {
                    int[] lastPosition = manager.findLastVisibleItemPositions(null);

                    boolean hasTop = false;
                    for (int p : lastPosition) {
                        if (p != position && p != -1) {
                            StaggeredGridLayoutManager.LayoutParams params1 = (StaggeredGridLayoutManager.LayoutParams) manager.findViewByPosition(p).getLayoutParams();
                            if (params1.getSpanIndex() == spanIndex) {
                                hasTop = true;
                                break;
                            }
                        }
                    }
                    return !hasTop;
                } else {
                    return position < spanCount;
                }
            } else // 水平布局
            {
                return params.getSpanIndex() == 0;
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            return true;
        }
        return false;
    }

    private boolean alignBottomEdge(RecyclerView parent, int position) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager manager = (GridLayoutManager) layoutManager;
            GridLayoutManager.SpanSizeLookup lookup = manager.getSpanSizeLookup();
            int spanCount = manager.getSpanCount();
            int itemCount = parent.getAdapter().getItemCount();
            if (manager.getOrientation() == GridLayoutManager.VERTICAL) // 垂直布局
            {
                if (manager.getReverseLayout()) {
                    return lookup.getSpanGroupIndex(position, spanCount) == 0;
                } else {
                    int lastRowFirstPosition = 0;
                    for (int i = itemCount - 1; i >= 0; i--) {
                        if (lookup.getSpanIndex(i, spanCount) == 0) {
                            lastRowFirstPosition = i;
                            break;
                        }
                    }
                    if (position >= lastRowFirstPosition) {
                        return true;
                    }
                }
            } else // 水平布局
            {
                return positionTotalSpanSize(manager, position) == spanCount;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) layoutManager;
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) manager.findViewByPosition(position).getLayoutParams();
            int spanCount = manager.getSpanCount();
            int spanIndex = params.getSpanIndex();

            if (manager.getOrientation() == StaggeredGridLayoutManager.VERTICAL) // 垂直布局
            {
                if (manager.getReverseLayout()) {
                    return position < spanCount;
                } else {
                    int[] lastPosition = manager.findLastVisibleItemPositions(null);

                    boolean hasBottom = false;
                    for (int p : lastPosition) {
                        if (p != position && p != -1) {
                            StaggeredGridLayoutManager.LayoutParams params1 = (StaggeredGridLayoutManager.LayoutParams) manager.findViewByPosition(p).getLayoutParams();
                            if (params1.getSpanIndex() == spanIndex) {
                                hasBottom = true;
                                break;
                            }
                        }
                    }
                    return !hasBottom;
                }
            } else // 水平布局
            {
                return spanIndex == spanCount - 1;
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            return true;
        }
        return false;
    }

    @Override
    protected void setItemOffsets(Rect outRect, int position, RecyclerView parent) {
        if (mPositionInsideItem) {
            outRect.set(0, 0, 0, 0);
            return;
        }
        outRect.set(0, 0, getDividerSize(position, parent), 0);
    }

    private int getDividerSize(int position, RecyclerView parent) {
        if (mPaintProvider != null) {
            return (int) mPaintProvider.dividerPaint(position, parent).getStrokeWidth();
        } else if (mSizeProvider != null) {
            return mSizeProvider.dividerSize(position, parent);
        } else if (mDrawableProvider != null) {
            Drawable drawable = mDrawableProvider.drawableProvider(position, parent);
            return drawable.getIntrinsicWidth();
        }
        throw new RuntimeException("failed to get size");
    }

    /**
     * Interface for controlling divider margin
     */
    public interface MarginProvider {

        /**
         * Returns top margin of divider.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return top margin
         */
        int dividerTopMargin(int position, RecyclerView parent);

        /**
         * Returns bottom margin of divider.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return bottom margin
         */
        int dividerBottomMargin(int position, RecyclerView parent);
    }

    public static class Builder extends FlexibleDividerDecoration.Builder<Builder> {

        private MarginProvider mMarginProvider = new MarginProvider() {
            @Override
            public int dividerTopMargin(int position, RecyclerView parent) {
                return 0;
            }

            @Override
            public int dividerBottomMargin(int position, RecyclerView parent) {
                return 0;
            }
        };

        public Builder(Context context) {
            super(context);
        }

        public Builder margin(final int topMargin, final int bottomMargin) {
            return marginProvider(new MarginProvider() {
                @Override
                public int dividerTopMargin(int position, RecyclerView parent) {
                    return topMargin;
                }

                @Override
                public int dividerBottomMargin(int position, RecyclerView parent) {
                    return bottomMargin;
                }
            });
        }

        public Builder margin(int verticalMargin) {
            return margin(verticalMargin, verticalMargin);
        }

        public Builder marginResId(@DimenRes int topMarginId, @DimenRes int bottomMarginId) {
            return margin(mResources.getDimensionPixelSize(topMarginId),
                    mResources.getDimensionPixelSize(bottomMarginId));
        }

        public Builder marginResId(@DimenRes int verticalMarginId) {
            return marginResId(verticalMarginId, verticalMarginId);
        }

        public Builder marginProvider(MarginProvider provider) {
            mMarginProvider = provider;
            return this;
        }

        public VerticalDividerItemDecoration build() {
            checkBuilderParams();
            return new VerticalDividerItemDecoration(this);
        }
    }
}