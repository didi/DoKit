package com.didichuxing.doraemonkit.kit.alignruler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.util.ActivityUtils;
import com.didichuxing.doraemonkit.util.ConvertUtils;
import com.didichuxing.doraemonkit.util.ScreenUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView;
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams;

/**
 * Created by jintai on 2019/09/26.
 */

public class AlignRulerLineDoKitView extends AbsDoKitView implements AlignRulerMarkerDoKitView.OnAlignRulerMarkerPositionChangeListener {
    private AlignRulerMarkerDoKitView mMarker;
    private AlignLineView mAlignInfoView;

    @Override
    public void onCreate(Context context) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMarker.removePositionChangeListener(this);
    }

    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_align_ruler_line, view, false);
    }


    @Override
    public void initDokitViewLayoutParams(DoKitViewLayoutParams params) {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.height = DoKitViewLayoutParams.MATCH_PARENT;
        params.width = DoKitViewLayoutParams.MATCH_PARENT;
    }

    @Override
    public void onViewCreated(FrameLayout view) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mMarker = DoKit.getDoKitView(ActivityUtils.getTopActivity(), AlignRulerMarkerDoKitView.class);
                if (mMarker != null) {
                    mMarker.addPositionChangeListener(AlignRulerLineDoKitView.this);
                }
            }
        }, 100);
        setDoKitViewNotResponseTouchEvent(getDoKitView());
        mAlignInfoView = findViewById(R.id.info_view);
    }

    @Override
    public void onPositionChanged(int x, int y) {
        /**
         * 限制边界
         */
        if (!isNormalMode()) {
            int iconSize = ConvertUtils.dp2px(30);
            if (y <= iconSize) {
                y = iconSize;
            }

            if (ScreenUtils.isPortrait()) {
                if (y >= getScreenLongSideLength() - iconSize) {
                    y = getScreenLongSideLength() - iconSize;
                }
            } else {
                if (y >= getScreenShortSideLength() - iconSize) {
                    y = getScreenShortSideLength() - iconSize;
                }
            }


            if (x <= iconSize) {
                x = iconSize;
            }
            if (ScreenUtils.isPortrait()) {
                if (x >= getScreenShortSideLength() - iconSize) {
                    x = getScreenShortSideLength() - iconSize;
                }
            } else {
                if (x >= getScreenLongSideLength() - iconSize) {
                    x = getScreenLongSideLength() - iconSize;
                }
            }
        }


        mAlignInfoView.showInfo(x, y);
    }

    @Override
    public boolean canDrag() {
        return false;
    }

    @Override
    public boolean restrictBorderline() {
        return true;
    }

    public AlignLineView getAlignInfoView() {
        return mAlignInfoView;
    }
}
