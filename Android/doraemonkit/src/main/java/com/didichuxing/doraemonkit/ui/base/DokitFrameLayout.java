package com.didichuxing.doraemonkit.ui.base;

import android.content.Context;
import android.util.AttributeSet;
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
public class DokitFrameLayout extends FrameLayout implements DokitViewInterface {
    public DokitFrameLayout(@NonNull Context context) {
        super(context);
    }

    public DokitFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DokitFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
