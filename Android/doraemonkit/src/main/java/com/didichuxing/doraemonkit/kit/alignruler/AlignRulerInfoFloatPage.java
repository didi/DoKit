package com.didichuxing.doraemonkit.kit.alignruler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.AlignRulerConfig;
import com.didichuxing.doraemonkit.constant.PageTag;
import com.didichuxing.doraemonkit.ui.base.BaseFloatPage;
import com.didichuxing.doraemonkit.ui.base.FloatPageManager;
import com.didichuxing.doraemonkit.ui.base.TouchProxy;
import com.didichuxing.doraemonkit.util.UIUtils;

/**
 * Created by wanglikun on 2018/12/3.
 */

public class AlignRulerInfoFloatPage extends BaseFloatPage implements TouchProxy.OnTouchEventListener, AlignRulerMarkerFloatPage.OnAlignRulerMarkerPositionChangeListener {
    private WindowManager mWindowManager;
    private TextView mAlignHex;
    private ImageView mClose;
    private TouchProxy mTouchProxy = new TouchProxy(this);
    private AlignRulerMarkerFloatPage mMarker;
    private int mWindowWidth;
    private int mWindowHeight;

    @Override
    protected void onCreate(Context context) {
        super.onCreate(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mMarker = (AlignRulerMarkerFloatPage) FloatPageManager.getInstance().getFloatPage(PageTag.PAGE_ALIGN_RULER_MARKER);
        mMarker.addPositionChangeListener(this);
        mWindowWidth = UIUtils.getWidthPixels(context);
        mWindowHeight = UIUtils.getHeightPixels(context);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMarker.removePositionChangeListener(this);
    }

    @Override
    protected View onCreateView(Context context, ViewGroup view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_align_ruler_info, null);
    }

    @Override
    protected void onLayoutParamsCreated(WindowManager.LayoutParams params) {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.x = 0;
        params.y = UIUtils.getHeightPixels(getContext()) - UIUtils.dp2px(getContext(), 85);
    }

    @Override
    protected void onViewCreated(View view) {
        super.onViewCreated(view);
        initView();
    }

    private void initView() {
        getRootView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mTouchProxy.onTouchEvent(v, event);
            }
        });
        mAlignHex = findViewById(R.id.align_hex);
        mClose = findViewById(R.id.close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlignRulerConfig.setAlignRulerOpen(getContext(), false);
                FloatPageManager.getInstance().removeAll(AlignRulerMarkerFloatPage.class);
                FloatPageManager.getInstance().removeAll(AlignRulerLineFloatPage.class);
                FloatPageManager.getInstance().removeAll(AlignRulerInfoFloatPage.class);
            }
        });
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
    public void onPositionChanged(int x, int y) {
        int left = x;
        int top = y;
        int right = mWindowWidth - left;
        int bottom = mWindowHeight - top;
        mAlignHex.setText(getResources().getString(R.string.dk_align_info_text, left, right, top, bottom));
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