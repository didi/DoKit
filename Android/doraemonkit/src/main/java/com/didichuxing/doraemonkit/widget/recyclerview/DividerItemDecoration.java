package com.didichuxing.doraemonkit.widget.recyclerview;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by jinliang on 2017/9/29.
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private static final String TAG = "TitleItem";
    private Drawable mDivider;
    private int mOrientation;
    private final Rect mBounds = new Rect();
    private boolean mShowHeaderDivider;
    private boolean mShowFooterDivider = true;

    public DividerItemDecoration(int orientation) {
        this.setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != 0 && orientation != 1) {
            throw new IllegalArgumentException("Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        } else {
            this.mOrientation = orientation;
        }
    }

    public void setDrawable(@NonNull Drawable drawable) {
        this.mDivider = drawable;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() != null && this.mDivider != null) {
            if (this.mOrientation == 1) {
                this.drawVertical(c, parent);
            } else {
                this.drawHorizontal(c, parent);
            }
        }
    }

    public void showHeaderDivider(boolean show) {
        mShowHeaderDivider = show;
    }

    public void showFooterDivider(boolean show) {
        mShowFooterDivider = show;
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        int left;
        int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            int top = parent.getPaddingTop();
            int bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(left, top, right, bottom);
        } else {
            left = 0;
            right = parent.getWidth();
        }

        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; ++i) {
            View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, this.mBounds);

            int top;
            int bottom;
            if (i == 0 && mShowHeaderDivider) {
                top = 0;
                this.mDivider.setBounds(left, top, right, top + this.mDivider.getIntrinsicHeight());
                this.mDivider.draw(canvas);
            }

            if (i != childCount - 1 || mShowFooterDivider) {
                bottom = this.mBounds.bottom + Math.round(child.getTranslationY());
                top = bottom - this.mDivider.getIntrinsicHeight();
                this.mDivider.setBounds(left, top, right, bottom);
                this.mDivider.draw(canvas);
            }
        }
        canvas.restore();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();
        int top;
        int bottom;
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top, parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }

        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; ++i) {
            View child = parent.getChildAt(i);
            parent.getLayoutManager().getDecoratedBoundsWithMargins(child, this.mBounds);
            int right = this.mBounds.right + Math.round(child.getTranslationX());
            int left = right - this.mDivider.getIntrinsicWidth();
            this.mDivider.setBounds(left, top, right, bottom);
            this.mDivider.draw(canvas);
        }

        canvas.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (this.mDivider == null) {
            outRect.set(0, 0, 0, 0);
        } else {
            if (this.mOrientation == 1) {
                outRect.set(0, 0, 0, this.mDivider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, this.mDivider.getIntrinsicWidth(), 0);
            }

        }
    }
}