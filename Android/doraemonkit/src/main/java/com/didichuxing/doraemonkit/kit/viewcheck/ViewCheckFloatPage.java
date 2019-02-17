package com.didichuxing.doraemonkit.kit.viewcheck;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.base.BaseFloatPage;
import com.didichuxing.doraemonkit.ui.base.TouchProxy;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglikun on 2018/11/20.
 */

public class ViewCheckFloatPage extends BaseFloatPage implements TouchProxy.OnTouchEventListener {
    private static final String TAG = "ViewCheckFloatPage";

    private TouchProxy mTouchProxy = new TouchProxy(this);

    protected WindowManager mWindowManager;

    private List<OnViewSelectListener> mViewSelectListeners = new ArrayList<>();

    private Activity mResumedActivity;

    private DoraemonKit.ActivityLifecycleListener mLifecycleListener = new DoraemonKit.ActivityLifecycleListener() {
        @Override
        public void onActivityResumed(Activity activity) {
            mResumedActivity = activity;
            onViewSelected(null);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }
    };

    @Override
    protected void onCreate(Context context) {
        super.onCreate(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DoraemonKit.registerListener(mLifecycleListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DoraemonKit.unRegisterListener(mLifecycleListener);
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
        getRootView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mTouchProxy.onTouchEvent(v, event);
            }
        });
    }

    private View findSelectView(int x, int y) {
        if (mResumedActivity == null) {
            return null;
        }
        if (mResumedActivity.getWindow() == null) {
            return null;
        }
        LogHelper.d(TAG, "x: " + x + ", y: " + y);
        return traverseViews(mResumedActivity.getWindow().getDecorView(), x, y);
    }

    private View traverseViews(View view, int x, int y) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
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

    public void setViewSelectListener(OnViewSelectListener viewSelectListener) {
        mViewSelectListeners.add(viewSelectListener);
    }

    public void removeViewSelectListener(OnViewSelectListener viewSelectListener) {
        mViewSelectListeners.remove(viewSelectListener);
    }

    @Override
    public void onMove(int x, int y, int dx, int dy) {
        getLayoutParams().x += dx;
        getLayoutParams().y += dy;
        mWindowManager.updateViewLayout(getRootView(), getLayoutParams());
    }

    @Override
    public void onUp(int x, int y) {
        View selectView = findSelectView(getLayoutParams().x + getRootView().getWidth() / 2, getLayoutParams().y + getRootView().getHeight() / 2);
        onViewSelected(selectView);
    }

    @Override
    public void onDown(int x, int y) {

    }

    private void  onViewSelected(View view) {
        for (OnViewSelectListener listener : mViewSelectListeners) {
            listener.onViewSelected(view);
        }
    }

    public interface OnViewSelectListener {
        void onViewSelected(View view);
    }
}