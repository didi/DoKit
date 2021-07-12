package com.didichuxing.doraemonkit.kit.alignruler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.didichuxing.doraemonkit.util.ActivityUtils;
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

    private CheckBox mIncludeStatusBarHeight;
    private OnCheckedChangeListener mListener;

    private int left, right, top, bottom;

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
        params.y = UIUtils.getHeightPixels() - UIUtils.dp2px(150);
    }

    @Override
    public void onViewCreated(FrameLayout view) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mMarker = (AlignRulerMarkerDokitView) DokitViewManager.getInstance().getDokitView(ActivityUtils.getTopActivity(), AlignRulerMarkerDokitView.class.getCanonicalName());
                if (mMarker != null) {
                    mMarker.addPositionChangeListener(AlignRulerInfoDokitView.this);
                }
            }
        }, 100);
        initView();
    }

    private void initView() {
        getDoKitView().setOnTouchListener(new View.OnTouchListener() {
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
                AlignRulerConfig.setAlignRulerOpen(false);
                DokitViewManager.getInstance().detach(AlignRulerMarkerDokitView.class.getCanonicalName());
                DokitViewManager.getInstance().detach(AlignRulerLineDokitView.class.getCanonicalName());
                DokitViewManager.getInstance().detach(AlignRulerInfoDokitView.class.getCanonicalName());
            }
        });

        mIncludeStatusBarHeight = findViewById(R.id.cb_status_bar);
        mIncludeStatusBarHeight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mListener != null) {
                    mListener.onCheckedChanged(isChecked);
                }
                setTextInfo(isChecked);
            }
        });
    }

    public void setCheckBoxListener(OnCheckedChangeListener mListener) {
        this.mListener = mListener;
    }


    public interface OnCheckedChangeListener {
        void onCheckedChanged(boolean isChecked);
    }

    @Override
    public void onPositionChanged(int x, int y) {
        left = x;
        top = y;
        right = mWindowWidth - left;
        bottom = mWindowHeight - top;
        setTextInfo(mIncludeStatusBarHeight.isChecked());
    }


    private void setTextInfo(boolean includeStatusBar) {
        if (includeStatusBar) {
            mAlignHex.setText(getResources().getString(R.string.dk_align_info_text, left, right, top + UIUtils.getStatusBarHeight(), bottom));
        } else {
            mAlignHex.setText(getResources().getString(R.string.dk_align_info_text, left, right, top, bottom));
        }
    }
}