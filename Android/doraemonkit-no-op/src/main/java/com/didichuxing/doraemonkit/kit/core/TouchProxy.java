package com.didichuxing.doraemonkit.kit.core;

import android.view.MotionEvent;
import android.view.View;


/**
 * @author wanglikun
 * touch 事件代理 解决点击和触摸事件的冲突
 */
public class TouchProxy {

    public TouchProxy(OnTouchEventListener eventListener) {
    }

    public void setEventListener(OnTouchEventListener eventListener) {
    }


    public boolean onTouchEvent(View v, MotionEvent event) {

        return true;
    }


    public interface OnTouchEventListener {
        void onMove(int x, int y, int dx, int dy);

        void onUp(int x, int y);

        void onDown(int x, int y);
    }
}
