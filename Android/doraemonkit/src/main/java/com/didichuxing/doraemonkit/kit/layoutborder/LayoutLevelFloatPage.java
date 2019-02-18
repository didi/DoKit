package com.didichuxing.doraemonkit.kit.layoutborder;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.LayoutBorderConfig;
import com.didichuxing.doraemonkit.ui.UniversalActivity;
import com.didichuxing.doraemonkit.ui.base.BaseFloatPage;
import com.didichuxing.doraemonkit.ui.base.FloatPageManager;
import com.didichuxing.doraemonkit.ui.base.TouchProxy;
import com.didichuxing.doraemonkit.ui.layoutborder.ScalpelFrameLayout;
import com.didichuxing.doraemonkit.util.UIUtils;

/**
 * Created by wanglikun on 2019/1/9
 */
public class LayoutLevelFloatPage extends BaseFloatPage implements TouchProxy.OnTouchEventListener {
    private WindowManager mWindowManager;

    private CheckBox mSwitchButton;
    private View mClose;

    private TouchProxy mTouchProxy = new TouchProxy(this);

    private ScalpelFrameLayout mScalpelFrameLayout;

    private boolean mIsCheck;
    private DoraemonKit.ActivityLifecycleListener mLifecycleListener = new DoraemonKit.ActivityLifecycleListener() {
        @Override
        public void onActivityResumed(Activity activity) {
            resolveActivity(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {

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
        mScalpelFrameLayout = new ScalpelFrameLayout(root.getContext());
        while (root.getChildCount() != 0) {
            View child = root.getChildAt(0);
            if (child instanceof ScalpelFrameLayout) {
                mScalpelFrameLayout = (ScalpelFrameLayout) child;
                return;
            }
            root.removeView(child);
            mScalpelFrameLayout.addView(child);
        }
        mScalpelFrameLayout.setLayerInteractionEnabled(mIsCheck);
        root.addView(mScalpelFrameLayout);
    }

    @Override
    protected View onCreateView(Context context, ViewGroup view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_layout_level, view, false);
    }

    @Override
    protected void onViewCreated(View view) {
        super.onViewCreated(view);
        mSwitchButton = findViewById(R.id.switch_btn);
        mSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mScalpelFrameLayout != null) {
                        mScalpelFrameLayout.setLayerInteractionEnabled(true);
                    }
                } else {
                    if (mScalpelFrameLayout != null) {
                        mScalpelFrameLayout.setLayerInteractionEnabled(false);
                    }
                }
                mIsCheck = isChecked;
            }
        });
        mClose = findViewById(R.id.close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mScalpelFrameLayout != null) {
                    mScalpelFrameLayout.setLayerInteractionEnabled(false);
                }
                FloatPageManager.getInstance().removeAll(LayoutLevelFloatPage.class);
                LayoutBorderConfig.setLayoutLevelOpen(false);

                LayoutBorderConfig.setLayoutBorderOpen(false);
                LayoutBorderManager.getInstance().stop();
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
    protected void onLayoutParamsCreated(WindowManager.LayoutParams params) {
        params.x = 0;
        params.y = UIUtils.getHeightPixels(getContext()) - UIUtils.dp2px(getContext(), 125);
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    }

    @Override
    protected void onCreate(Context context) {
        super.onCreate(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        resolveActivity(DoraemonKit.getCurrentResumedActivity());
        DoraemonKit.registerListener(mLifecycleListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mScalpelFrameLayout != null) {
            mScalpelFrameLayout.setLayerInteractionEnabled(false);
            mScalpelFrameLayout = null;
        }
        DoraemonKit.unRegisterListener(mLifecycleListener);
    }

    @Override
    public void onMove(int x, int y, int dx, int dy) {
        getLayoutParams().x += dx;
        getLayoutParams().y += dy;
        mWindowManager.updateViewLayout(getRootView(), getLayoutParams());
    }

    @Override
    public void onUp(int x, int y) {

    }

    @Override
    public void onDown(int x, int y) {

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
