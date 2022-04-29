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

import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.util.ActivityUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.AlignRulerConfig;
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView;
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams;
import com.didichuxing.doraemonkit.util.UIUtils;

/**
 * Created by jintai on 2019/09/26.
 */

public class AlignRulerInfoDoKitView extends AbsDoKitView implements AlignRulerMarkerDoKitView.OnAlignRulerMarkerPositionChangeListener {
    private TextView mAlignHex;
    private ImageView mClose;

    private AlignRulerMarkerDoKitView mMarker;
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
    public void initDokitViewLayoutParams(DoKitViewLayoutParams params) {
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
                mMarker = DoKit.getDoKitView(ActivityUtils.getTopActivity(), AlignRulerMarkerDoKitView.class);
                if (mMarker != null) {
                    mMarker.addPositionChangeListener(AlignRulerInfoDoKitView.this);
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
                DoKit.removeFloating(AlignRulerMarkerDoKitView.class);
                DoKit.removeFloating(AlignRulerLineDoKitView.class);
                DoKit.removeFloating(AlignRulerInfoDoKitView.class);
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
