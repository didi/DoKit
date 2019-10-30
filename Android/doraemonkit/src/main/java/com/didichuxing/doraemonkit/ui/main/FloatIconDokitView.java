package com.didichuxing.doraemonkit.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.FloatIconConfig;
import com.didichuxing.doraemonkit.ui.base.AbsDokitView;
import com.didichuxing.doraemonkit.ui.base.DokitIntent;
import com.didichuxing.doraemonkit.ui.base.DokitViewLayoutParams;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;

/**
 * 悬浮按钮
 * Created by jintai on 2019/09/26.
 */

public class FloatIconDokitView extends AbsDokitView {
    public static int FLOAT_SIZE = 174;

    @Override
    public void onCreate(Context context) {

    }

    @Override
    public void onViewCreated(FrameLayout view) {
        //设置id便于查找
        getRootView().setId(R.id.float_icon_id);
        //设置icon 点击事件
        getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DokitIntent popViewIntent = new DokitIntent(ToolPanelDokitView.class);
                popViewIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
                DokitViewManager.getInstance().attach(popViewIntent);
            }
        });

    }


    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_launch_icon, view, false);
    }


    @Override
    public void initDokitViewLayoutParams(DokitViewLayoutParams params) {
        params.x = FloatIconConfig.getLastPosX(getContext());
        params.y = FloatIconConfig.getLastPosY(getContext());
        params.width = FLOAT_SIZE;
        params.height = FLOAT_SIZE;
    }

    @Override
    public void onDokitViewAdd(AbsDokitView dokitView) {
        if (dokitView == this) {
            return;
        }
        DokitViewManager.getInstance().detach(this);
        DokitIntent intent = new DokitIntent(FloatIconDokitView.class);
        intent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManager.getInstance().attach(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNormalMode()) {
            FrameLayout.LayoutParams params = getNormalLayoutParams();
            params.width = FLOAT_SIZE;
            params.height = FLOAT_SIZE;
            invalidate();
        }
    }
}
