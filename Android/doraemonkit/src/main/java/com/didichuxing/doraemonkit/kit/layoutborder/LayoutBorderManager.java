package com.didichuxing.doraemonkit.kit.layoutborder;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.ui.UniversalActivity;
import com.didichuxing.doraemonkit.ui.layoutborder.ViewBorderFrameLayout;

/**
 * Created by wanglikun on 2019/1/9
 */
public class LayoutBorderManager {
    private boolean isRunning;

    private ViewBorderFrameLayout mViewBorderFrameLayout;

    private DoraemonKit.ActivityLifecycleListener mLifecycleListener = new DoraemonKit.ActivityLifecycleListener() {
        @Override
        public void onActivityResumed(Activity activity) {
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

        @Override
        public void onActivityPaused(Activity activity) {

        }
    };

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
        DoraemonKit.registerListener(mLifecycleListener);
    }

    public void stop() {
        isRunning = false;
        DoraemonKit.unRegisterListener(mLifecycleListener);
    }

    public boolean isRunning() {
        return isRunning;
    }
}