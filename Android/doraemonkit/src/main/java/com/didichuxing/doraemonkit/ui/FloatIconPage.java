package com.didichuxing.doraemonkit.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.FloatIconConfig;
import com.didichuxing.doraemonkit.ui.base.BaseFloatPage;
import com.didichuxing.doraemonkit.ui.base.FloatPageManager;
import com.didichuxing.doraemonkit.ui.base.PageIntent;
import com.didichuxing.doraemonkit.ui.base.TouchProxy;

/**
 * 悬浮按钮
 * Created by zhangweida on 2018/6/22.
 */

public class FloatIconPage extends BaseFloatPage implements TouchProxy.OnTouchEventListener {
    protected WindowManager mWindowManager;

    private TouchProxy mTouchProxy = new TouchProxy(this);

    @Override
    protected void onViewCreated(View view) {
        getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageIntent pageIntent = new PageIntent(KitFloatPage.class);
                pageIntent.mode = PageIntent.MODE_SINGLE_INSTANCE;
                FloatPageManager.getInstance().add(pageIntent);
            }
        });
        getRootView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mTouchProxy.onTouchEvent(v, event);
            }
        });
    }

    @Override
    protected View onCreateView(Context context, ViewGroup view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_launch_icon, view, false);
    }

    @Override
    protected void onLayoutParamsCreated(WindowManager.LayoutParams params) {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.x = FloatIconConfig.getLastPosX(getContext());
        params.y = FloatIconConfig.getLastPosY(getContext());
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected void onCreate(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
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

    @Override
    public void onMove(int x, int y, int dx, int dy) {
        getLayoutParams().x += dx;
        getLayoutParams().y += dy;
        mWindowManager.updateViewLayout(getRootView(), getLayoutParams());
    }

    @Override
    public void onUp(int x, int y) {
        FloatIconConfig.saveLastPosX(getContext(), getLayoutParams().x);
        FloatIconConfig.saveLastPosY(getContext(), getLayoutParams().y);
    }

    @Override
    public void onDown(int x, int y) {

    }
}
