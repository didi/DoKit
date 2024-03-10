package com.didichuxing.doraemonkit.kit.uiperformance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.model.ViewInfo;
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView;
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams;
import com.didichuxing.doraemonkit.kit.viewcheck.LayoutBorderView;

import java.util.List;

/**
 * Created by jintai on 2019/09/26.
 */
public class UIPerformanceDisplayDoKitView extends AbsDoKitView implements UIPerformanceManager.PerformanceDataListener {
    private LayoutBorderView mLayoutBorderView;

    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(view.getContext()).inflate(R.layout.dk_float_ui_performance_display, view, false);
    }

    @Override
    public void onViewCreated(FrameLayout view) {
        mLayoutBorderView = findViewById(R.id.rect_view);
        //设置不响应触摸事件
        setDoKitViewNotResponseTouchEvent(getDoKitView());
    }


    @Override
    public void initDokitViewLayoutParams(DoKitViewLayoutParams params) {
        params.flags = DoKitViewLayoutParams.FLAG_NOT_FOCUSABLE_AND_NOT_TOUCHABLE;
        params.width = DoKitViewLayoutParams.MATCH_PARENT;
        params.height = DoKitViewLayoutParams.MATCH_PARENT;
    }

    @Override
    public void onCreate(Context context) {
        UIPerformanceManager.getInstance().addListener(UIPerformanceDisplayDoKitView.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UIPerformanceManager.getInstance().removeListener(this);
    }

    @Override
    public void onRefresh(List<ViewInfo> infos) {
        mLayoutBorderView.showViewLayoutBorder(infos);
    }


    @Override
    public boolean canDrag() {
        return false;
    }

}
