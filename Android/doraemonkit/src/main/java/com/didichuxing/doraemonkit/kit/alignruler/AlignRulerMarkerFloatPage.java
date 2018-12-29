package com.didichuxing.doraemonkit.kit.alignruler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.base.BaseFloatPage;
import com.didichuxing.doraemonkit.ui.base.TouchProxy;
import com.didichuxing.doraemonkit.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglikun on 2018/9/20.
 */

public class AlignRulerMarkerFloatPage extends BaseFloatPage implements TouchProxy.OnTouchEventListener {
    private List<OnAlignRulerMarkerPositionChangeListener> mPositionChangeListeners = new ArrayList<>();

    private TouchProxy mTouchProxy = new TouchProxy(this);

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
        getRootView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mTouchProxy.onTouchEvent(v, event);
            }
        });
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
    protected void onCreate(Context context) {
        super.onCreate(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removePositionChangeListeners();
    }

    @Override
    public void onMove(int x, int y, int dx, int dy) {
        getLayoutParams().x += dx;
        getLayoutParams().y += dy;
        mWindowManager.updateViewLayout(getRootView(), getLayoutParams());
        for (OnAlignRulerMarkerPositionChangeListener listener : mPositionChangeListeners) {
            listener.onPositionChanged(getLayoutParams().x + getRootView().getWidth() / 2, getLayoutParams().y + getRootView().getHeight() / 2);
        }
    }

    @Override
    public void onUp(int x, int y) {

    }

    @Override
    public void onDown(int x, int y) {

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
