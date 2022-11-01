package com.didichuxing.doraemonkit.kit.core;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-08-17:10
 * 描    述：自定义FrameLayout 用来区分原生FrameLayout
 * 修订历史：
 * ================================================
 */
public class DoKitFrameLayout extends FrameLayout implements DoKitViewInterface {
    public static final int DoKitFrameLayoutFlag_ROOT = 100;
    public static final int DoKitFrameLayoutFlag_CHILD = 200;

    private int mFlag = DoKitFrameLayoutFlag_ROOT;
    private String mTitle;

    public DoKitFrameLayout(@NonNull Context context, int flag) {
        super(context);
        this.mFlag = flag;
    }

    public DoKitFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DoKitFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }
}
