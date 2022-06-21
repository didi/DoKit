package com.didichuxing.doraemondemo.dokit;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.didichuxing.doraemondemo.R;
import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView;
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams;

/**
 * @Author: changzuozhen
 * @Date: 2020-12-22
 * <p>
 * 悬浮窗，支持折叠
 * @see SimpleDoKitView
 * 启动工具函数
 */
public abstract class SimpleDoKitView extends AbsDoKitView {
    private static final String TAG = "SimpleBaseFloatPage";
    int mWidth;
    int mHeight;
    int mDp50InPx;
    private WindowManager mWindowManager;
    private FrameLayout mFloatContainer;
    private Switch mShowSwitch;
    private Context mContext;

    @Override
    public void onEnterForeground() {
        super.onEnterForeground();
        getParentView().setVisibility(View.VISIBLE);
    }

    @Override
    public void onEnterBackground() {
        super.onEnterBackground();
        getParentView().setVisibility(View.GONE);
    }

    public void showContainer(boolean isChecked) {
        mFloatContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        immInvalidate();
    }

    @Override
    public void onCreate(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(outMetrics);
        mDp50InPx = ConvertUtils.dp2px(50);
        mWidth = outMetrics.widthPixels - mDp50InPx;
        mHeight = outMetrics.heightPixels - mDp50InPx;
    }


    @Override
    public View onCreateView(Context context, FrameLayout rootView) {
        mContext = context;
        return LayoutInflater.from(context).inflate(R.layout.dk_layout_simple_dokit_float_view, rootView, false);
    }

    @Override
    public void onViewCreated(FrameLayout rootView) {
        mFloatContainer = findViewById(R.id.floatContainer);
        LayoutInflater.from(mContext).inflate(getLayoutId(), mFloatContainer);
        mShowSwitch = findViewById(R.id.showHideSwitch);
        TextView title = findViewById(R.id.floatPageTitle);
        ImageView close = findViewById(R.id.floatClose);
        close.setOnClickListener(v -> DoKit.removeFloating(this));
        title.setText(getTag());
        mShowSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showContainer(isChecked);
            }
        });
        initView();
    }

    protected abstract int getLayoutId();


    @Override
    public void initDokitViewLayoutParams(DoKitViewLayoutParams params) {
        params.width = DoKitViewLayoutParams.WRAP_CONTENT;
        params.height = DoKitViewLayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 200;
        params.y = 200;
    }

    @Override
    public boolean onBackPressed() {
        mShowSwitch.setChecked(false);
        return false;
    }

    @Override
    public boolean shouldDealBackKey() {
        return true;
    }

    protected void initView() {
    }


}
