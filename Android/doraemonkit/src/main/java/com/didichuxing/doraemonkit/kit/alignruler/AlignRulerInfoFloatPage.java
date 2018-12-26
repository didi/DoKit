package com.didichuxing.doraemonkit.kit.alignruler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.PageTag;
import com.didichuxing.doraemonkit.ui.alignruler.AlignInfoView;
import com.didichuxing.doraemonkit.ui.base.BaseFloatPage;
import com.didichuxing.doraemonkit.ui.base.FloatPageManager;

/**
 * Created by wanglikun on 2018/11/13.
 */

public class AlignRulerInfoFloatPage extends BaseFloatPage implements AlignRulerMarkerFloatPage.OnAlignRulerMarkerPositionChangeListener {
    private AlignRulerMarkerFloatPage mMarker;
    private AlignInfoView mAlignInfoView;

    @Override
    protected void onCreate(Context context) {
        super.onCreate(context);
        mMarker = (AlignRulerMarkerFloatPage) FloatPageManager.getInstance().getFloatPage(PageTag.PAGE_ALIGN_RULER_MARKER);
        mMarker.addPositionChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMarker.removePositionChangeListener(this);
    }

    @Override
    protected View onCreateView(Context context, ViewGroup view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_align_ruler_info, view, false);
    }

    @Override
    protected void onLayoutParamsCreated(WindowManager.LayoutParams params) {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected void onViewCreated(View view) {
        super.onViewCreated(view);
        mAlignInfoView = findViewById(R.id.info_view);
    }

    @Override
    public void onPositionChanged(int x, int y) {
        mAlignInfoView.showInfo(x, y);
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