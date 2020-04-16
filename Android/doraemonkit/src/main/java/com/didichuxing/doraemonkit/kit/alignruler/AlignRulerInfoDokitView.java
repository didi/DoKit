package com.didichuxing.doraemonkit.kit.alignruler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.AlignRulerConfig;
import com.didichuxing.doraemonkit.kit.core.AbsDokitView;
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams;
import com.didichuxing.doraemonkit.kit.core.DokitViewManager;
import com.didichuxing.doraemonkit.util.UIUtils;

/**
 * Created by jintai on 2019/09/26.
 */

public class AlignRulerInfoDokitView extends AbsDokitView implements AlignRulerMarkerDokitView.OnAlignRulerMarkerPositionChangeListener {
    private TextView mAlignHex;
    private ImageView mClose;
    private AlignRulerMarkerDokitView mMarker;
    private int mWindowWidth;
    private int mWindowHeight;

    @Override
    public void onCreate(Context context) {
        mWindowWidth = UIUtils.getWidthPixels();
        mWindowHeight = UIUtils.getHeightPixels();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMarker.removePositionChangeListener(this);
    }

    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_align_ruler_info, null);
    }


    @Override
    public void initDokitViewLayoutParams(DokitViewLayoutParams params) {
        params.width = getScreenShortSideLength();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.x = 0;
        params.y = UIUtils.getHeightPixels() - UIUtils.dp2px(95);
    }

    @Override
    public void onViewCreated(FrameLayout view) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mMarker = (AlignRulerMarkerDokitView) DokitViewManager.getInstance().getDokitView(ActivityUtils.getTopActivity(), AlignRulerMarkerDokitView.class.getSimpleName());
                if (mMarker != null) {
                    mMarker.addPositionChangeListener(AlignRulerInfoDokitView.this);
                }
            }
        }, 100);
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
                DokitViewManager.getInstance().detach(AlignRulerMarkerDokitView.class.getSimpleName());
                DokitViewManager.getInstance().detach(AlignRulerLineDokitView.class.getSimpleName());
                DokitViewManager.getInstance().detach(AlignRulerInfoDokitView.class.getSimpleName());
            }
        });
    }


    @Override
    public void onPositionChanged(int x, int y) {
        int left = x;
        int top = y;
        int right = mWindowWidth - left;
        int bottom = mWindowHeight - top;
        mAlignHex.setText(getResources().getString(R.string.dk_align_info_text, left, right, top, bottom));
    }


}