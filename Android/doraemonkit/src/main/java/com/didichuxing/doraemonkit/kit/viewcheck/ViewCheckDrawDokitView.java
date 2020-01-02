package com.didichuxing.doraemonkit.kit.viewcheck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.ActivityUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.model.ViewInfo;
import com.didichuxing.doraemonkit.ui.base.AbsDokitView;
import com.didichuxing.doraemonkit.ui.base.DokitViewLayoutParams;
import com.didichuxing.doraemonkit.ui.base.DokitViewManagerProxy;
import com.didichuxing.doraemonkit.ui.viewcheck.LayoutBorderView;

/**
 * Created by jintai on 2019/09/26.
 */

public class ViewCheckDrawDokitView extends AbsDokitView implements ViewCheckDokitView.OnViewSelectListener {
    private LayoutBorderView mLayoutBorderView;

    @Override
    public void onCreate(Context context) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ViewCheckDokitView page = (ViewCheckDokitView) DokitViewManagerProxy.getInstance().getDokitView(ActivityUtils.getTopActivity(), ViewCheckDokitView.class.getSimpleName());
        if (page != null) {
            page.removeViewSelectListener(this);
        }
    }

    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_view_check_draw, null);
    }


    @Override
    public void initDokitViewLayoutParams(DokitViewLayoutParams params) {
        params.flags = DokitViewLayoutParams.FLAG_NOT_FOCUSABLE_AND_NOT_TOUCHABLE;
        params.width = DokitViewLayoutParams.MATCH_PARENT;
        params.height = DokitViewLayoutParams.MATCH_PARENT;
    }


    @Override
    public void onViewCreated(FrameLayout view) {
        mLayoutBorderView = findViewById(R.id.rect_view);
        setDokitViewNotResponseTouchEvent(getRootView());
        postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewCheckDokitView dokitView = (ViewCheckDokitView) DokitViewManagerProxy.getInstance().getDokitView(ActivityUtils.getTopActivity(), ViewCheckDokitView.class.getSimpleName());
                if (dokitView != null) {
                    dokitView.setViewSelectListener(ViewCheckDrawDokitView.this);
                }
            }
        }, 200);

    }


    @Override
    public void onViewSelected(View view) {
        if (view == null) {
            mLayoutBorderView.showViewLayoutBorder((ViewInfo) null);
        } else {
            mLayoutBorderView.showViewLayoutBorder(new ViewInfo(view));
        }
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
            invalidate();
        }
    }

    @Override
    public boolean canDrag() {
        return false;
    }

}
