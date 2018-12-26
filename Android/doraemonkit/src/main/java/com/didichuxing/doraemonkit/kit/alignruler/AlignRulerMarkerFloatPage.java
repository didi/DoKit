package com.didichuxing.doraemonkit.kit.alignruler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.base.BaseFloatPage;
import com.didichuxing.doraemonkit.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglikun on 2018/9/20.
 */

public class AlignRulerMarkerFloatPage extends BaseFloatPage implements View.OnTouchListener {
    private List<OnAlignRulerMarkerPositionChangeListener> mPositionChangeListeners = new ArrayList<>();

    private float sdX, sdY;
    private float ldX, ldY;

    private WindowManager mWindowManager;

    @Override
    protected View onCreateView(Context context, ViewGroup view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_align_ruler_marker, null);
    }

    @Override
    protected void onViewCreated(View view) {
        super.onViewCreated(view);
        initView();
    }

    private void initView() {
        getRootView().setOnTouchListener(this);
    }

    @Override
    protected void onLayoutParamsCreated(WindowManager.LayoutParams params) {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.x = UIUtils.getWidthPixels(getContext()) / 2;
        params.y = UIUtils.getHeightPixels(getContext()) / 2;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                sdX = ldX = x;
                sdY = ldY = y;
                return false;
            case MotionEvent.ACTION_MOVE:
                getLayoutParams().x += (x - ldX + 0.5f);
                getLayoutParams().y += (y - ldY + 0.5f);
                ldX = x;
                ldY = y;
                mWindowManager.updateViewLayout(getRootView(), getLayoutParams());
                for (OnAlignRulerMarkerPositionChangeListener listener : mPositionChangeListeners) {
                    listener.onPositionChanged(getLayoutParams().x + getRootView().getWidth() / 2, getLayoutParams().y + getRootView().getHeight() / 2);
                }
                return false;
            case MotionEvent.ACTION_UP:
                int mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if (Math.abs(x - sdX) <= mTouchSlop && Math.abs(y - sdY) <= mTouchSlop) {
                    return false;
                }
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onCreate(Context context) {
        super.onCreate(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removePositionChangeListeners();
    }

    public interface OnAlignRulerMarkerPositionChangeListener {
        void onPositionChanged(int x, int y);
    }

    public void addPositionChangeListener(OnAlignRulerMarkerPositionChangeListener positionChangeListener) {
        mPositionChangeListeners.add(positionChangeListener);
    }

    public void removePositionChangeListener(OnAlignRulerMarkerPositionChangeListener positionChangeListener) {
        mPositionChangeListeners.remove(positionChangeListener);
    }

    public void removePositionChangeListeners() {
        mPositionChangeListeners.clear();
    }

    @Override
    public void onEnterForeground() {
        super.onEnterForeground();
        getRootView().setVisibility(View.VISIBLE);
    }

    @Override
    public void onEnterBackground() {
        super.onEnterBackground();
        getRootView().setVisibility(View.GONE);
    }
}
