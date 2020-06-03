package com.didichuxing.doraemonkit.widget.videoview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;

import com.didichuxing.doraemonkit.util.UIUtils;

/**
 * Created by wanglikun on 2019/4/16
 */
public class CustomVideoView extends VideoView implements View.OnTouchListener{

    private float lastX;
    private float lastY;
    private int thresold = 30;
    private Context mContext;
    private StateListener mStateListener;

    public interface StateListener{
        void changeVolumn(float detlaY);
        void changeBrightness(float detlaX);
        void hideHint();
    }

    public void setStateListener(StateListener stateListener) {
        this.mStateListener = stateListener;
    }

    public CustomVideoView(Context context) {
        this(context,null);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getDefaultSize(1920,widthMeasureSpec);
        int height = getDefaultSize(1080,heightMeasureSpec);
        setMeasuredDimension(width,height);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float detlaX = event.getX() - lastX;
                float detlaY = event.getY() - lastY;
                if (Math.abs(detlaX) < thresold && Math.abs(detlaY) > thresold) {
                    if (event.getX() < UIUtils.getWidthPixels() / 2) {
                        mStateListener.changeBrightness(detlaY);
                    } else {
                        mStateListener.changeVolumn(detlaY);
                    }
                }
                lastX = event.getX();
                lastY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                mStateListener.hideHint();
                break;
        }
        return true;
    }
}