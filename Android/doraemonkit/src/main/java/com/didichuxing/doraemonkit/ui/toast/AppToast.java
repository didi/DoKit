package com.didichuxing.doraemonkit.ui.toast;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;

/**
 * App级toast
 */
public class AppToast {
    private Context mContext;

    private ViewGroup layout;
    private ViewGroup content;
    private TextView textView;

    private Animation startAnimation;
    private Animation centerAnimation;
    private Animation endAnimation;

    private DelayTask task;
    private boolean isShow;

    private WindowManager.LayoutParams params;

    /**
     * APP级别Toast
     */
    public AppToast(Context context) {
        mContext = context;

        layout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dk_app_toast, null);
        content = (ViewGroup) layout.getChildAt(0);
        textView = (TextView) layout.findViewById(R.id.app_toast_text);
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.format = PixelFormat.TRANSLUCENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).addView(layout, params);
        layout.setVisibility(View.GONE);

        // 开始动画
        startAnimation = new AlphaAnimation(0, 1);
        startAnimation.setDuration(500);

        // 中间动画
        centerAnimation = new AlphaAnimation(0.92f, 1);
        centerAnimation.setDuration(500);

        // 结束动画
        endAnimation = new AlphaAnimation(1, 0);
        endAnimation.setDuration(500);
        endAnimation.setInterpolator(new AccelerateInterpolator());

        // 结束动画监听
        endAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * 显示Toast
     */
    public void show(String s) {
        show(s, 1500);
    }

    /**
     * 显示Toast
     */
    public void show(String s, int delay) {
        textView.setText(s);
        start();
        if (task != null) {
            task.stop();
        }
        task = new DelayTask(delay) {
            @Override
            public void logic() {
                end();
            }
        };
        task.start();
    }

    /**
     * 开始
     */
    private void start() {
        if (!isShow) {
            layout.setVisibility(View.VISIBLE);
            content.startAnimation(startAnimation);
            isShow = true;
        } else {
            content.startAnimation(centerAnimation);
        }
    }

    /**
     * 结束
     */
    private void end() {
        content.startAnimation(endAnimation);
        isShow = false;
    }
}