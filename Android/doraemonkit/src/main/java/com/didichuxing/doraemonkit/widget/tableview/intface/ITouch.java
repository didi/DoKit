package com.didichuxing.doraemonkit.widget.tableview.intface;

import android.view.MotionEvent;
import android.view.View;


public interface ITouch {
    /**
     * 用于判断是否请求不拦截事件
     * 解决手势冲突问题
     *
     * @param view
     * @param event
     */
    void onDisallowInterceptEvent(View view, MotionEvent event);

    /**
     * 处理touchEvent
     *
     * @param event
     */
    boolean handlerTouchEvent(MotionEvent event);
}
