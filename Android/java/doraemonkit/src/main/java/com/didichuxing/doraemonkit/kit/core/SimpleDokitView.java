package com.didichuxing.doraemonkit.kit.core;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.widget.tableview.utils.DensityUtils;

/**
 * @Author: changzuozhen
 * @Date: 2020-12-22
 * <p>
 * 悬浮窗，支持折叠
 * @see com.didichuxing.doraemonkit.kit.core.SimpleDokitView
 * 启动工具函数
 * @see com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter.startFloating
 */
public abstract class SimpleDokitView extends AbsDokitView {
    private static final String TAG = "SimpleBaseFloatPage";
    int mWidth;
    int mHeight;
    int mDp50InPx;
    private WindowManager mWindowManager;
    private FrameLayout mFloatContainer;
    private Switch mShowSwitch;

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

    public void showContainer(boolean isChecked) {
        mFloatContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        invalidate();
    }

    @Override
    public void onCreate(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(outMetrics);
        mDp50InPx = DensityUtils.dp2px(context, 50);
        mWidth = outMetrics.widthPixels - mDp50InPx;
        mHeight = outMetrics.heightPixels - mDp50InPx;
    }


    @Override
    public View onCreateView(Context context, FrameLayout rootView) {
        ConstraintLayout root = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.dk_layout_simple_dokit_float_view, rootView, false);
        mFloatContainer = root.findViewById(R.id.floatContainer);
        mShowSwitch = root.findViewById(R.id.showHideSwitch);
        TextView title = root.findViewById(R.id.floatPageTitle);
        ImageButton close = root.findViewById(R.id.floatClose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DokitViewManager.getInstance().detach(SimpleDokitView.this);
            }
        });
        title.setText(getTag());
        mShowSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showContainer(isChecked);
            }
        });
        LayoutInflater.from(context).inflate(getLayoutId(), mFloatContainer);
        return root;
    }

    @Override
    public void onViewCreated(FrameLayout rootView) {
        initView();
    }

    protected abstract int getLayoutId();


    @Override
    public void initDokitViewLayoutParams(DokitViewLayoutParams params) {
        params.width = DokitViewLayoutParams.WRAP_CONTENT;
        params.height = DokitViewLayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 200;
        params.y = 200;
    }

    @Override
    public boolean onBackPressed() {
        mShowSwitch.setChecked(false);
        return super.onBackPressed();
    }

    @Override
    public boolean shouldDealBackKey() {
        return true;
    }

    protected void initView() {
    }

    @Override
    public void invalidate() {
        if (getDoKitView() == null) {
            return;
        }
        if (isNormalMode()) {
            FrameLayout.LayoutParams layoutParams = getNormalLayoutParams();
            if (layoutParams == null) {
                return;
            }
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            getDoKitView().setLayoutParams(layoutParams);
        } else {
            WindowManager.LayoutParams layoutParams = getSystemLayoutParams();
            if (layoutParams == null) {
                return;
            }
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mWindowManager.updateViewLayout(getDoKitView(), layoutParams);
        }
    }
}