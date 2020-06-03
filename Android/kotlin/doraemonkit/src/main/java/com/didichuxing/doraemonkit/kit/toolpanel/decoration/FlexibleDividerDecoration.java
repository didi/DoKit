package com.didichuxing.doraemonkit.kit.toolpanel.decoration;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/30-16:05
 * 描    述：
 * 修订历史：
 * ================================================
 */

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * Created by yqritc on 2015/01/08.
 */
public abstract class FlexibleDividerDecoration extends RecyclerView.ItemDecoration {

    private static final int DEFAULT_SIZE = 2;
    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    protected enum DividerType {
        DRAWABLE, PAINT, COLOR, SPACE
    }

    protected DividerType mDividerType = DividerType.DRAWABLE;
    protected VisibilityProvider mVisibilityProvider;
    protected PaintProvider mPaintProvider;
    protected ColorProvider mColorProvider;
    protected DrawableProvider mDrawableProvider;
    protected SizeProvider mSizeProvider;
    protected SizeProvider mSpaceProvider;
    protected boolean mShowLastDivider;
    protected boolean mPositionInsideItem;
    private Paint mPaint;

    protected FlexibleDividerDecoration(Builder builder) {
        if (builder.mPaintProvider != null) {
            mDividerType = DividerType.PAINT;
            mPaintProvider = builder.mPaintProvider;
        } else if (builder.mColorProvider != null) {
            mDividerType = DividerType.COLOR;
            mColorProvider = builder.mColorProvider;
            mPaint = new Paint();
            setSizeProvider(builder);
        } else if (builder.mSpaceProvider != null) {
            mDividerType = DividerType.SPACE;
            mSpaceProvider = builder.mSpaceProvider;
        } else {
            mDividerType = DividerType.DRAWABLE;
            if (builder.mDrawableProvider == null) {
                TypedArray a = builder.mContext.obtainStyledAttributes(ATTRS);
                final Drawable divider = a.getDrawable(0);
                a.recycle();
                mDrawableProvider = new DrawableProvider() {
                    @Override
                    public Drawable drawableProvider(int position, RecyclerView parent) {
                        return divider;
                    }
                };
            } else {
                mDrawableProvider = builder.mDrawableProvider;
            }
            mSizeProvider = builder.mSizeProvider;
        }

        mVisibilityProvider = builder.mVisibilityProvider;
        mShowLastDivider = builder.mShowLastDivider;
        mPositionInsideItem = builder.mPositionInsideItem;
    }

    private void setSizeProvider(Builder builder) {
        mSizeProvider = builder.mSizeProvider;
        if (mSizeProvider == null) {
            mSizeProvider = new SizeProvider() {
                @Override
                public int dividerSize(int position, RecyclerView parent) {
                    return DEFAULT_SIZE;
                }
            };
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter == null) {
            return;
        }

        int validChildCount = parent.getChildCount();
        for (int i = 0; i < validChildCount; i++) {
            View child = parent.getChildAt(i);
            int childPosition = parent.getChildAdapterPosition(child);

            if (!hasDivider(parent, childPosition)) {
                continue;
            }

            if (mVisibilityProvider.shouldHideDivider(childPosition, parent)) {
                continue;
            }

            Rect bounds = getDividerBound(childPosition, parent, child);
            switch (mDividerType) {
                case DRAWABLE:
                    Drawable drawable = mDrawableProvider.drawableProvider(childPosition, parent);
                    drawable.setBounds(bounds);
                    drawable.draw(c);
                    break;
                case PAINT:
                    mPaint = mPaintProvider.dividerPaint(childPosition, parent);
                    c.drawLine(bounds.left, bounds.top, bounds.right, bounds.bottom, mPaint);
                    break;
                case COLOR:
                    mPaint.setColor(mColorProvider.dividerColor(childPosition, parent));
                    mPaint.setStrokeWidth(mSizeProvider.dividerSize(childPosition, parent));
                    c.drawLine(bounds.left, bounds.top, bounds.right, bounds.bottom, mPaint);
                    break;
                case SPACE:
                    break;
            }
        }
    }

    /**
     * Whether child has divider
     *
     * @param parent
     * @param childPosition
     * @return true if child has divider
     */
    public boolean hasDivider(RecyclerView parent, int childPosition) {
        if (mShowLastDivider) {
            return true;
        } else if (this instanceof VerticalDividerItemDecoration) {
            return hasVerticalDivider(parent, childPosition);
        } else if (this instanceof HorizontalDividerItemDecoration) {
            return hasHorizontalDivider(parent, childPosition);
        }
        return false;
    }

    private boolean hasVerticalDivider(RecyclerView parent, int position) {
        RecyclerView.Adapter adapter = parent.getAdapter();
        int itemCount = adapter.getItemCount();
        int lastDividerOffset = getLastDividerOffset(parent);


        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager manager = (GridLayoutManager) parent.getLayoutManager();
            int spanCount = manager.getSpanCount();

            if (manager.getOrientation() == RecyclerView.VERTICAL) {
                return positionTotalSpanSize(manager, position) != spanCount;
            } else {
                if (manager.getReverseLayout()) {
                    return manager.getSpanSizeLookup().getSpanGroupIndex(position, spanCount) != 0;
                } else {
                    return position < itemCount - lastDividerOffset;
                }
            }
        } else if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) parent.getLayoutManager();
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) manager.findViewByPosition(position).getLayoutParams();
            int spanCount = manager.getSpanCount();
            int spanIndex = params.getSpanIndex();

            if (manager.getOrientation() == OrientationHelper.VERTICAL) {
                return spanIndex < spanCount - 1;
            } else {
                int[] lastPosition = null;
                if (manager.getReverseLayout()) {
                    lastPosition = manager.findFirstVisibleItemPositions(null);
                } else {
                    lastPosition = manager.findLastVisibleItemPositions(null);
                }
                boolean hasDirectionAlign = false;
                for (int p : lastPosition) {
                    if (p != position && p != -1) {
                        StaggeredGridLayoutManager.LayoutParams params1 = (StaggeredGridLayoutManager.LayoutParams) manager.findViewByPosition(p).getLayoutParams();
                        if (params1.getSpanIndex() == spanIndex) {
                            hasDirectionAlign = true;
                            break;
                        }
                    }
                }
                return hasDirectionAlign;
            }
        } else if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            if (((LinearLayoutManager) parent.getLayoutManager()).getReverseLayout()) {
                return position > 0;
            } else {
                return position < itemCount - 1;
            }
        }
        return false;
    }

    private boolean hasHorizontalDivider(RecyclerView parent, int position) {
        RecyclerView.Adapter adapter = parent.getAdapter();
        int itemCount = adapter.getItemCount();
        int lastDividerOffset = getLastDividerOffset(parent);

        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager manager = (GridLayoutManager) parent.getLayoutManager();
            int spanCount = manager.getSpanCount();
            // 最后一行没有
            if (manager.getOrientation() == RecyclerView.VERTICAL) {
                if (manager.getReverseLayout()) {
                    GridLayoutManager.SpanSizeLookup lookup = manager.getSpanSizeLookup();
                    return lookup.getSpanGroupIndex(position, spanCount) != 0;
                } else {
                    return position < itemCount - lastDividerOffset;
                }
            } else {
                return positionTotalSpanSize(manager, position) != spanCount;
            }
        } else if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) parent.getLayoutManager();
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) manager.findViewByPosition(position).getLayoutParams();
            int spanCount = manager.getSpanCount();
            int spanIndex = params.getSpanIndex();

            if (manager.getOrientation() == OrientationHelper.VERTICAL) {
                if (manager.getReverseLayout()) {
                    return position > spanCount - 1;
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
                    return hasBottom;
                }
            } else {
                return spanIndex < spanCount - 1;
            }
        } else if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            if (((LinearLayoutManager) parent.getLayoutManager()).getReverseLayout()) {
                return position > 0;
            } else {
                return position < itemCount - 1;
            }
        }
        return false;
    }

    /**
     * @param manager
     * @param position
     * @return
     */
    protected int positionTotalSpanSize(GridLayoutManager manager, int position) {
        int totalSpanSize = 0;
        GridLayoutManager.SpanSizeLookup spanSizeLookup = manager.getSpanSizeLookup();
        int spanCount = manager.getSpanCount();
        int groupIndex = spanSizeLookup.getSpanGroupIndex(position, spanCount);
        for (int i = position; i >= 0; i--) {
            int thisGroupIndex = spanSizeLookup.getSpanGroupIndex(i, spanCount);
            if (thisGroupIndex == groupIndex) {
                totalSpanSize += spanSizeLookup.getSpanSize(i);
            } else {
                break;
            }
        }
        return totalSpanSize;
    }

    @Override
    public void getItemOffsets(Rect rect, View v, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(v);
        if (!hasDivider(parent, position)) {
            return;
        }

        if (mVisibilityProvider.shouldHideDivider(position, parent)) {
            return;
        }

        setItemOffsets(rect, position, parent);
    }

    /**
     * In the case mShowLastDivider = false,
     * Returns offset for how many views we don't have to draw a divider for,
     * for LinearLayoutManager it is as simple as not drawing the last child divider,
     * but for a GridLayoutManager it needs to take the span count for the last items into account
     * until we use the span count configured for the grid.
     *
     * @param parent RecyclerView
     * @return offset for how many views we don't have to draw a divider or 1 if its a
     * LinearLayoutManager
     */
    private int getLastDividerOffset(RecyclerView parent) {
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
            GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
            int spanCount = layoutManager.getSpanCount();
            int itemCount = parent.getAdapter().getItemCount();
            for (int i = itemCount - 1; i >= 0; i--) {
                if (spanSizeLookup.getSpanIndex(i, spanCount) == 0) {
                    return itemCount - i;
                }
            }
        }

        return 1;
    }

    protected abstract Rect getDividerBound(int position, RecyclerView parent, View child);

    protected abstract void setItemOffsets(Rect outRect, int position, RecyclerView parent);

    /**
     * Interface for controlling divider visibility
     */
    public interface VisibilityProvider {

        /**
         * Returns true if divider should be hidden.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return True if the divider at position should be hidden
         */
        boolean shouldHideDivider(int position, RecyclerView parent);
    }

    /**
     * Interface for controlling paint instance for divider drawing
     */
    public interface PaintProvider {

        /**
         * Returns {@link android.graphics.Paint} for divider
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return Paint instance
         */
        Paint dividerPaint(int position, RecyclerView parent);
    }

    /**
     * Interface for controlling divider color
     */
    public interface ColorProvider {

        /**
         * Returns {@link android.graphics.Color} value of divider
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return Color value
         */
        int dividerColor(int position, RecyclerView parent);
    }

    /**
     * Interface for controlling drawable object for divider drawing
     */
    public interface DrawableProvider {

        /**
         * Returns drawable instance for divider
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return Drawable instance
         */
        Drawable drawableProvider(int position, RecyclerView parent);
    }

    /**
     * Interface for controlling divider size
     */
    public interface SizeProvider {

        /**
         * Returns size value of divider.
         * Height for horizontal divider, width for vertical divider
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return Size of divider
         */
        int dividerSize(int position, RecyclerView parent);
    }

    public static class Builder<T extends Builder> {

        private Context mContext;
        protected Resources mResources;
        private PaintProvider mPaintProvider;
        private ColorProvider mColorProvider;
        private DrawableProvider mDrawableProvider;
        private SizeProvider mSizeProvider;
        private SizeProvider mSpaceProvider;
        private VisibilityProvider mVisibilityProvider = new VisibilityProvider() {
            @Override
            public boolean shouldHideDivider(int position, RecyclerView parent) {
                return false;
            }
        };
        private boolean mShowLastDivider = false;
        private boolean mPositionInsideItem = false;

        public Builder(Context context) {
            mContext = context;
            mResources = context.getResources();
        }

        public T paint(final Paint paint) {
            return paintProvider(new PaintProvider() {
                @Override
                public Paint dividerPaint(int position, RecyclerView parent) {
                    return paint;
                }
            });
        }

        public T paintProvider(PaintProvider provider) {
            mPaintProvider = provider;
            return (T) this;
        }

        public T color(final int color) {
            return colorProvider(new ColorProvider() {
                @Override
                public int dividerColor(int position, RecyclerView parent) {
                    return color;
                }
            });
        }

        public T colorResId(@ColorRes int colorId) {
            return color(ContextCompat.getColor(mContext, colorId));
        }

        public T colorProvider(ColorProvider provider) {
            mColorProvider = provider;
            return (T) this;
        }

        public T drawable(@DrawableRes int id) {
            return drawable(ContextCompat.getDrawable(mContext, id));
        }

        public T drawable(final Drawable drawable) {
            return drawableProvider(new DrawableProvider() {
                @Override
                public Drawable drawableProvider(int position, RecyclerView parent) {
                    return drawable;
                }
            });
        }

        public T drawableProvider(DrawableProvider provider) {
            mDrawableProvider = provider;
            return (T) this;
        }

        public T size(final int size) {
            return sizeProvider(new SizeProvider() {
                @Override
                public int dividerSize(int position, RecyclerView parent) {
                    return size;
                }
            });
        }

        public T sizeResId(@DimenRes int sizeId) {
            return size(mResources.getDimensionPixelSize(sizeId));
        }

        public T sizeProvider(SizeProvider provider) {
            mSizeProvider = provider;
            return (T) this;
        }

        public T space(final int space) {
            return spaceProvider(new SizeProvider() {
                @Override
                public int dividerSize(int position, RecyclerView parent) {
                    return space;
                }
            });
        }

        public T spaceResId(@DimenRes int spaceId) {
            return space(mResources.getDimensionPixelSize(spaceId));
        }

        public T spaceProvider(SizeProvider provider) {
            mSpaceProvider = provider;
            return (T) this;
        }

        public T visibilityProvider(VisibilityProvider provider) {
            mVisibilityProvider = provider;
            return (T) this;
        }

        public T showLastDivider() {
            mShowLastDivider = true;
            return (T) this;
        }

        public T positionInsideItem(boolean positionInsideItem) {
            mPositionInsideItem = positionInsideItem;
            return (T) this;
        }

        protected void checkBuilderParams() {
            if (mPaintProvider != null) {
                if (mColorProvider != null) {
                    throw new IllegalArgumentException(
                            "Use setColor method of Paint class to specify line color. Do not provider ColorProvider if you set PaintProvider.");
                }
                if (mSizeProvider != null) {
                    throw new IllegalArgumentException(
                            "Use setStrokeWidth method of Paint class to specify line size. Do not provider SizeProvider if you set PaintProvider.");
                }
            }
        }
    }
}