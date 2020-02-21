package com.didichuxing.doraemonkit.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.FloatIconConfig;
import com.didichuxing.doraemonkit.datapick.DataPickManager;
import com.didichuxing.doraemonkit.ui.base.AbsDokitView;
import com.didichuxing.doraemonkit.ui.base.DokitIntent;
import com.didichuxing.doraemonkit.ui.base.DokitViewLayoutParams;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;

/**
 * 悬浮按钮
 * Created by jintai on 2019/09/26.
 */

public class MainIconDokitView extends AbsDokitView {
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
                //统计入口
                DataPickManager.getInstance().addData("click",1,"mainIcon");
                DokitIntent dokitViewIntent = new DokitIntent(ToolPanelDokitView.class);
                dokitViewIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
                DokitViewManager.getInstance().attach(dokitViewIntent);
            }
        });

    }


    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_main_launch_icon, view, false);
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
        DokitIntent intent = new DokitIntent(MainIconDokitView.class);
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
