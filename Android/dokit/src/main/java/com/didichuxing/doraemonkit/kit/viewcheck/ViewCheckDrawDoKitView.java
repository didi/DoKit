package com.didichuxing.doraemonkit.kit.viewcheck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.util.ActivityUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.model.ViewInfo;
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView;
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams;

import java.util.List;

/**
 * Created by jintai on 2019/09/26.
 * 在改布局上绘制相应的View
 */
public class ViewCheckDrawDoKitView extends AbsDoKitView implements ViewCheckDoKitView.OnViewSelectListener {
    private LayoutBorderView mLayoutBorderView;

    @Override
    public void onCreate(Context context) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ViewCheckDoKitView page = (ViewCheckDoKitView) DoKit.getDoKitView(ActivityUtils.getTopActivity(), ViewCheckDoKitView.class);
        if (page != null) {
            page.removeViewSelectListener(this);
        }
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
        postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewCheckDoKitView dokitView = DoKit.getDoKitView(ActivityUtils.getTopActivity(), ViewCheckDoKitView.class);
                if (dokitView != null) {
                    dokitView.setViewSelectListener(ViewCheckDrawDoKitView.this);
                }
            }
        }, 200);

    }

    @Override
    public void onViewSelected(@Nullable View current, @NonNull List<View> checkViewList) {
        if (current == null) {
            mLayoutBorderView.showViewLayoutBorder((ViewInfo) null);
        } else {
            mLayoutBorderView.showViewLayoutBorder(new ViewInfo(current));
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
            immInvalidate();
        }
    }

    @Override
    public boolean canDrag() {
        return false;
    }

}
