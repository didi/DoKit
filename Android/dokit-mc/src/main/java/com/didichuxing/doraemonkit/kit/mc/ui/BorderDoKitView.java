package com.didichuxing.doraemonkit.kit.mc.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView;
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams;
import com.didichuxing.doraemonkit.kit.viewcheck.LayoutBorderView;
import com.didichuxing.doraemonkit.model.ViewInfo;

/**
 * Created by jintai on 2019/09/26.
 * 在相应的界面上绘制指定View的边框
 */
public class BorderDoKitView extends AbsDoKitView {
    private LayoutBorderView mLayoutBorderView = null;

    @Override
    public void onCreate(Context context) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_view_check_draw, null);
    }


    @Override
    public void initDokitViewLayoutParams(DoKitViewLayoutParams params) {
        params.flags = DoKitViewLayoutParams.FLAG_NOT_FOCUSABLE_AND_NOT_TOUCHABLE;
        params.width = DoKitViewLayoutParams.MATCH_PARENT;
        params.height = DoKitViewLayoutParams.MATCH_PARENT;
    }


    @Override
    public void onViewCreated(FrameLayout view) {
        mLayoutBorderView = findViewById(R.id.rect_view);
        setDoKitViewNotResponseTouchEvent(getDoKitView());
    }


    /**
     * 解决ViewCheckDrawDokitView的margin被改变的bug
     */
    @Override
    public void onResume() {
        super.onResume();
        if (getNormalLayoutParams() != null) {
            FrameLayout.LayoutParams params = getNormalLayoutParams();
            params.setMargins(0, 0, 0, 0);
            params.width = FrameLayout.LayoutParams.MATCH_PARENT;
            params.height = FrameLayout.LayoutParams.MATCH_PARENT;
            immInvalidate();
        }
    }

    @Override
    public boolean canDrag() {
        return false;
    }

    public void showBorder(View target) {
        if (target == null) {
            mLayoutBorderView.showViewLayoutBorder((ViewInfo) null);
        } else {
            mLayoutBorderView.showViewLayoutBorder(new ViewInfo(target));
        }
    }

}
