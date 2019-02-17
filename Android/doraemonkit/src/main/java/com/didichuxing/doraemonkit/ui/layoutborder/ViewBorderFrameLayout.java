package com.didichuxing.doraemonkit.ui.layoutborder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.didichuxing.doraemonkit.config.LayoutBorderConfig;

/**
 * Created by wanglikun on 2019/1/12
 */
public class ViewBorderFrameLayout extends FrameLayout {
    public ViewBorderFrameLayout(@NonNull Context context) {
        super(context);
    }

    public ViewBorderFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewBorderFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (LayoutBorderConfig.isLayoutBorderOpen(getContext())) {
            traverseChild(this);
        }
    }

    private void traverseChild(View view) {
        if (view instanceof ViewGroup) {
            replaceDrawable(view);
            int childCount = ((ViewGroup) view).getChildCount();
            if (childCount != 0) {
                for (int index = 0; index < childCount; index++) {
                    traverseChild(((ViewGroup) view).getChildAt(index));
                }
            }
        } else {
            replaceDrawable(view);
        }
    }

    private void replaceDrawable(View view) {
        LayerDrawable newDrawable;
        if (view.getBackground() != null) {
            Drawable oldDrawable = view.getBackground();
            if (oldDrawable instanceof LayerDrawable) {
                for (int i = 0; i < ((LayerDrawable) oldDrawable).getNumberOfLayers(); i++) {
                    if (((LayerDrawable) oldDrawable).getDrawable(i) instanceof ViewBorderDrawable) {
                        // already replace
                        return;
                    }
                }
                newDrawable = new LayerDrawable(new Drawable[] {
                        oldDrawable,
                        new ViewBorderDrawable(view)
                });
            } else {
                newDrawable = new LayerDrawable(new Drawable[] {
                        oldDrawable,
                        new ViewBorderDrawable(view)
                });
            }
        } else {
            newDrawable = new LayerDrawable(new Drawable[] {
                    new ViewBorderDrawable(view)
            });
        }
        view.setBackground(newDrawable);
    }
}