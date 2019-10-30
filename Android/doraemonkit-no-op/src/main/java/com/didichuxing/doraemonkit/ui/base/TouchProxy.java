package com.didichuxing.doraemonkit.ui.base;

import android.view.MotionEvent;
import android.view.View;


/**
 * @author wanglikun
 * touch 事件代理 解决点击和触摸事件的冲突
 */
public class TouchProxy {
    private static final int MIN_DISTANCE_MOVE = 4;
    private static final int MIN_TAP_TIME = 1000;

    private OnTouchEventListener mEventListener;
    private int mLastX;
    private int mLastY;
    private int mStartX;
    private int mStartY;
    private TouchState mState = TouchState.STATE_STOP;

    public TouchProxy(OnTouchEventListener eventListener) {
        mEventListener = eventListener;
    }

    public void setEventListener(OnTouchEventListener eventListener) {
        mEventListener = eventListener;
    }

    private enum TouchState {
        STATE_MOVE,
        STATE_STOP
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
