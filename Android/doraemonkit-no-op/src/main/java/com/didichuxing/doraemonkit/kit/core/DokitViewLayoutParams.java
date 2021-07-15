package com.didichuxing.doraemonkit.kit.core;

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
     * 悬浮窗不能获取焦点
     */
    public static int FLAG_NOT_FOCUSABLE = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    public static int FLAG_NOT_TOUCHABLE = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
    /**
     * wiki:https://blog.csdn.net/hnlgzb/article/details/108520716
     * 是否允许超出屏幕
     */
    public static int FLAG_LAYOUT_NO_LIMITS = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
    /**
     * 悬浮窗不能获取焦点并且不相应触摸
     */
    public static int FLAG_NOT_FOCUSABLE_AND_NOT_TOUCHABLE = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

    public static int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    public static int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;


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
