package com.didichuxing.doraemonkit.kit.viewcheck;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.base.BaseFloatPage;
import com.didichuxing.doraemonkit.ui.base.FloatPageManager;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglikun on 2018/11/20.
 */

public class ViewCheckFloatPage extends BaseFloatPage implements View.OnTouchListener {
    private static final String TAG = "ViewCheckFloatPage";

    private float sdX, sdY;
    private float ldX, ldY;

    protected WindowManager mWindowManager;

    private List<OnViewSelectListener> mViewSelectListeners = new ArrayList<>();

    @Override
    protected void onCreate(Context context) {
        super.onCreate(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    protected View onCreateView(Context context, ViewGroup view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_view_check, null);
    }

    @Override
    protected void onLayoutParamsCreated(WindowManager.LayoutParams params) {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.x = UIUtils.getWidthPixels(getContext()) / 2;
        params.y = UIUtils.getHeightPixels(getContext()) / 2;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected void onViewCreated(View view) {
        super.onViewCreated(view);
        getRootView().setOnTouchListener(this);
    }

    private View findSelectView(int x, int y) {
        Activity resumedActivity = FloatPageManager.getInstance().getResumedActivity();
        if (resumedActivity == null) {
            return null;
        }
        if (resumedActivity.getWindow() == null) {
            return null;
        }
        LogHelper.d(TAG, "x: " + x + ", y: " + y);
        y += UIUtils.getStatusBarHeight(getContext());
        return traverseViews(resumedActivity.getWindow().getDecorView(), x, y);
    }

    private View traverseViews(View view, int x, int y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getWidth();
        int bottom = top + view.getHeight();
        if (view instanceof ViewGroup) {
            int childCount = ((ViewGroup) view).getChildCount();
            if (childCount != 0) {
                for (int index = childCount - 1; index >= 0; index--) {
                    View v = traverseViews(((ViewGroup) view).getChildAt(index), x, y);
                    if (v != null) {
                        return v;
                    }
                }
            }
            if (left < x &&  x < right && top < y &&  y < bottom) {
                return view;
            } else {
                return null;
            }
        } else {
            LogHelper.d(TAG, "class: " + view.getClass() + ", left: " + left
                    + ", right: " + right + ", top: " + top + ", bottom: " + bottom);
            if (left < x &&  x < right && top < y &&  y < bottom) {
                return view;
            } else {
                return null;
            }
        }
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
                int mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if (Math.abs(x - sdX) <= mTouchSlop && Math.abs(y - sdY) <= mTouchSlop) {
                    return false;
                }
                View selectView = findSelectView(getLayoutParams().x + getRootView().getWidth() / 2, getLayoutParams().y + getRootView().getHeight() / 2);
                for (OnViewSelectListener listener : mViewSelectListeners) {
                    listener.onViewSelected(selectView);
                }
                return true;
            default:
                break;
        }
        return false;
    }

    public void setViewSelectListener(OnViewSelectListener viewSelectListener) {
        mViewSelectListeners.add(viewSelectListener);
    }

    public void removeViewSelectListener(OnViewSelectListener viewSelectListener) {
        mViewSelectListeners.remove(viewSelectListener);
    }

    public interface OnViewSelectListener {
        void onViewSelected(View view);
    }
}