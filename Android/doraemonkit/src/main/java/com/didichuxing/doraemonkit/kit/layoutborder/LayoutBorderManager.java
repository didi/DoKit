package com.didichuxing.doraemonkit.kit.layoutborder;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.UniversalActivity;
import com.didichuxing.doraemonkit.ui.layoutborder.ViewBorderFrameLayout;
import com.didichuxing.doraemonkit.util.UIUtils;

/**
 * Created by wanglikun on 2019/1/9
 */
public class LayoutBorderManager {
    private boolean isRunning;

    private ViewBorderFrameLayout mViewBorderFrameLayout;

    private DoraemonKit.LifecycleListener mLifecycleListener = new DoraemonKit.LifecycleListener() {
        @Override
        public void onActivityResumed(Activity activity) {
            //UIUtils.getDokitAppContentView(activity).setId(R.id.dokit_app_contentview_id);
            resolveActivity(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onFragmentAttached(Fragment f) {

        }

        @Override
        public void onFragmentDetached(Fragment f) {
            if (mViewBorderFrameLayout != null) {
                mViewBorderFrameLayout = null;
            }
        }
    };

    private void resolveActivity(Activity activity) {
        if (activity == null || (activity instanceof UniversalActivity)) {
            return;
        }
        Window window = activity.getWindow();
        if (window == null) {
            return;
        }
        final ViewGroup root = (ViewGroup) window.getDecorView();
        if (root == null) {
            return;
        }
        mViewBorderFrameLayout = new ViewBorderFrameLayout(root.getContext());
        while (root.getChildCount() != 0) {
            View child = root.getChildAt(0);
            if (child instanceof ViewBorderFrameLayout) {
                mViewBorderFrameLayout = (ViewBorderFrameLayout) child;
                return;
            }
            root.removeView(child);
            mViewBorderFrameLayout.addView(child);
        }
        root.addView(mViewBorderFrameLayout);
    }

    private static class Holder {
        private static LayoutBorderManager INSTANCE = new LayoutBorderManager();
    }

    private LayoutBorderManager() {
    }

    public static LayoutBorderManager getInstance() {
        return Holder.INSTANCE;
    }

    public void start() {
        isRunning = true;
        resolveActivity(DoraemonKit.getCurrentResumedActivity());
        DoraemonKit.registerListener(mLifecycleListener);
    }

    public void stop() {
        isRunning = false;
        if (mViewBorderFrameLayout != null) {
            mViewBorderFrameLayout.requestLayout();
        }
        mViewBorderFrameLayout = null;
        DoraemonKit.unRegisterListener(mLifecycleListener);
    }

    public boolean isRunning() {
        return isRunning;
    }
}