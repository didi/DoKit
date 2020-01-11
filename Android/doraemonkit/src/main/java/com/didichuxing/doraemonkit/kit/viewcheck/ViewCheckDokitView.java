package com.didichuxing.doraemonkit.kit.viewcheck;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ActivityUtils;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.base.AbsDokitView;
import com.didichuxing.doraemonkit.ui.base.DokitViewLayoutParams;
import com.didichuxing.doraemonkit.ui.layoutborder.ViewBorderFrameLayout;
import com.didichuxing.doraemonkit.util.LifecycleListenerUtil;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jintai on 2019/09/26.
 */

public class ViewCheckDokitView extends AbsDokitView {
    private static final String TAG = "ViewCheckFloatPage";


    private List<OnViewSelectListener> mViewSelectListeners = new ArrayList<>();

    private Activity mResumedActivity;

    /**
     * 监听每个悬浮窗的activity生命周期
     */
    private LifecycleListenerUtil.LifecycleListener mLifecycleListener = new LifecycleListenerUtil.LifecycleListener() {
        @Override
        public void onActivityResumed(Activity activity) {
            mResumedActivity = activity;
            View selectView;
            if (isNormalMode()) {
                selectView = findSelectView(getNormalLayoutParams().leftMargin + getRootView().getWidth() / 2, getNormalLayoutParams().topMargin + getRootView().getHeight() / 2);
            } else {
                selectView = findSelectView(getSystemLayoutParams().x + getRootView().getWidth() / 2, getSystemLayoutParams().y + getRootView().getHeight() / 2);

            }
            onViewSelected(selectView);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onFragmentAttached(Fragment f) {

        }

        @Override
        public void onFragmentDetached(Fragment f) {

        }
    };

    @Override
    public void onCreate(Context context) {
        mResumedActivity = ActivityUtils.getTopActivity();
        LifecycleListenerUtil.registerListener(mLifecycleListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LifecycleListenerUtil.unRegisterListener(mLifecycleListener);
    }

    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_view_check, null);
    }


    @Override
    public void initDokitViewLayoutParams(DokitViewLayoutParams params) {
        params.x = UIUtils.getWidthPixels(getContext()) / 2;
        params.y = UIUtils.getHeightPixels(getContext()) / 2;
        params.height = DokitViewLayoutParams.WRAP_CONTENT;
        params.width = DokitViewLayoutParams.WRAP_CONTENT;
    }

    @Override
    public void onViewCreated(FrameLayout view) {

    }

    private View findSelectView(int x, int y) {
        if (mResumedActivity == null) {
            return null;
        }
        if (mResumedActivity.getWindow() == null) {
            return null;
        }
        if (isNormalMode()) {
            //LogHelper.d(TAG, "x: " + x + ", y: " + y);
            return traverseViews(UIUtils.getDokitAppContentView(mResumedActivity), x, y);
        } else {
            return traverseViews(mResumedActivity.getWindow().getDecorView(), x, y);
        }

    }

    private View traverseViews(View view, int x, int y) {
        if (view == null) {
            return null;
        }
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
            if (left < x && x < right && top < y && y < bottom) {
                return view;
            } else {
                return null;
            }
        } else {
            if (view != null) {
//                LogHelper.i(TAG, "class: " + view.getClass() + ", left: " + left
//                        + ", right: " + right + ", top: " + top + ", bottom: " + bottom);
            }

            if (left < x && x < right && top < y && y < bottom) {
                return view;
            } else {
                return null;
            }
        }
    }

    public void setViewSelectListener(OnViewSelectListener viewSelectListener) {
        mViewSelectListeners.add(viewSelectListener);
        //每次新增监听的时候开始查找
        View selectView = null;
        if (isNormalMode()) {
            selectView = findSelectView(getNormalLayoutParams().leftMargin + getRootView().getWidth() / 2, getNormalLayoutParams().topMargin + getRootView().getHeight() / 2);
        } else {
            selectView = findSelectView(getSystemLayoutParams().x + getRootView().getWidth() / 2, getSystemLayoutParams().y + getRootView().getHeight() / 2);

        }
        onViewSelected(selectView);
    }

    public void removeViewSelectListener(OnViewSelectListener viewSelectListener) {
        mViewSelectListeners.remove(viewSelectListener);
    }


    @Override
    public void onUp(int x, int y) {
        super.onUp(x, y);
        View selectView = null;
        if (isNormalMode()) {
            selectView = findSelectView(getNormalLayoutParams().leftMargin + getRootView().getWidth() / 2, getNormalLayoutParams().topMargin + getRootView().getHeight() / 2);
        } else {
            selectView = findSelectView(getSystemLayoutParams().x + getRootView().getWidth() / 2, getSystemLayoutParams().y + getRootView().getHeight() / 2);

        }
        onViewSelected(selectView);
    }

    @Override
    public void onDown(int x, int y) {

    }

    private void onViewSelected(View view) {
        for (OnViewSelectListener listener : mViewSelectListeners) {
            listener.onViewSelected(view);
        }
    }

    public interface OnViewSelectListener {
        void onViewSelected(View view);
    }

}