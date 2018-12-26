package com.didichuxing.doraemonkit.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.FloatIconConfig;
import com.didichuxing.doraemonkit.ui.base.BaseFloatPage;
import com.didichuxing.doraemonkit.ui.base.FloatPageManager;
import com.didichuxing.doraemonkit.ui.base.PageIntent;

/**
 * 悬浮按钮
 * Created by zhangweida on 2018/6/22.
 */

public class FloatIconPage extends BaseFloatPage implements View.OnTouchListener {
    protected WindowManager mWindowManager;

    private float sdX, sdY;
    private float ldX, ldY;

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
        getRootView().setOnTouchListener(this);
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
                return false;
            case MotionEvent.ACTION_UP:
                FloatIconConfig.saveLastPosX(getContext(), getLayoutParams().x);
                FloatIconConfig.saveLastPosY(getContext(), getLayoutParams().y);
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
