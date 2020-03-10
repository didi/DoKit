package com.didichuxing.doraemonkit.ui.base;

import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-24-10:57
 * 描    述：dokitView的初始化位置
 * 修订历史：
 * ================================================
 */
public class DokitViewLayoutParams {

    /**
     * 只针对系统悬浮窗起作用 值基本上为以上2个
     */
    public int flags;
    /**
     * 只针对系统悬浮窗起作用 值基本上为Gravity
     */
    public int gravity;
    public int x;
    public int y;
    public int width;
    public int height;

    @Override
    public String toString() {
        return "DokitViewLayoutParams{" +
                "flags=" + flags +
                ", gravity=" + gravity +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
