package com.didichuxing.doraemonkit.kit.lbs.manual;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.GpsMockConfig;
import com.didichuxing.doraemonkit.kit.core.SimpleDokitView;
import com.didichuxing.doraemonkit.kit.core.ViewSetupHelper;
import com.didichuxing.doraemonkit.kit.lbs.route.FloatGpsRouteMockCache;
import com.didichuxing.doraemonkit.kit.lbs.common.LocInfo;
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager;
import com.didichuxing.doraemonkit.model.LatLng;

/**
 * 定位微调悬浮窗
 */
public class PosAdjustKitView extends SimpleDokitView {
    public static final String TAG = "FloatGpsMockKitView";
    public static final int MIN_STEP = 5;
    public static final int MAX_STEP = 500;
    private View mRootView;
    private static int sMockStep = 10;
    private TextView mMockSpeedTv;
    private SeekBar mSpeedSeekBar;
    private TextView mEnvInfo = null;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_mock_pos_adjust;
    }


    @Override
    public void onViewCreated(FrameLayout rootView) {
        super.onViewCreated(rootView);
        mRootView = rootView;
        setMockLocationConfig();
    }

    private void setMockLocationConfig() {

        // 单点模拟
        // 开关
        final Switch envSwitch = findViewById(R.id.env_switch3);
        envSwitch.setChecked(GpsMockManager.getInstance().isMocking());
        envSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean on) {
                if (on) {
                    GpsMockManager.getInstance().startMock();
                } else {
                    GpsMockManager.getInstance().stopMock();
                }
            }
        });

        ViewSetupHelper.setupButton(mRootView, R.id.btn_reset, "重试", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng mocLoc = GpsMockConfig.getMockLocation();
                if (mocLoc != null) {
                    FloatGpsMockCache.mockToLocation(mocLoc.latitude, mocLoc.longitude);
                } else {
                    ToastUtils.showShort("没有设置过模拟位置");
                }
//                FloatGpsMockCache.mockToLocation(GpsMockManager.getInstance().getLatitude(), GpsMockManager.getInstance().getLongitude());
            }
        });

        // 单点步进调节功能按钮
        ViewSetupHelper.setupButton(mRootView, R.id.btn_mock_gps_north, "上", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveMockLocation(sMockStep, 0);
            }
        });
        ViewSetupHelper.setupButton(mRootView, R.id.btn_mock_gps_south, "下", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveMockLocation(-sMockStep, 0);
            }
        });
        ViewSetupHelper.setupButton(mRootView, R.id.btn_mock_gps_west, "左", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveMockLocation(0, -sMockStep);
            }
        });
        ViewSetupHelper.setupButton(mRootView, R.id.btn_mock_gps_east, "右", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveMockLocation(0, sMockStep);
            }
        });

        // 单点步进速度控制
        mMockSpeedTv = findViewById(R.id.tv_mock_speed);
        mSpeedSeekBar = findViewById(R.id.dk_sb_seekBar);
        updateSpeedView(mMockSpeedTv, mSpeedSeekBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mSpeedSeekBar.setMin(MIN_STEP);
        }
        mSpeedSeekBar.setMax(MAX_STEP);
        mSpeedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    sMockStep = progress;
                    mMockSpeedTv.setText(String.format("步进速度控制:%s米", sMockStep));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        findViewById(R.id.dk_btn_downMockSpeed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sMockStep -= 10;
                if (sMockStep < MIN_STEP) sMockStep = MIN_STEP;
                updateSpeedView(mMockSpeedTv, mSpeedSeekBar);
            }
        });
        findViewById(R.id.dk_btn_upMockSpeed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sMockStep += 10;
                if (sMockStep > MAX_STEP) sMockStep = MAX_STEP;
                updateSpeedView(mMockSpeedTv, mSpeedSeekBar);
            }
        });


        mEnvInfo = this.findViewById(R.id.env_info3);
        mEnvInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard(mEnvInfo.getText().toString());
            }
        });
    }

    private void updateSpeedView(TextView mockSpeed, SeekBar seekBar) {
        seekBar.setProgress(sMockStep);
        mMockSpeedTv.setText(String.format("步进速度控制:%s米", sMockStep));
    }

    private void updateCurrentLocConfig(LocInfo currentConfig) {
        if (mEnvInfo == null) return;
        updateGsonInfo(currentConfig.toString(), mEnvInfo);
    }

    private void updateGsonInfo(String currentConfig, TextView envInfo) {
        if (currentConfig == null) {
            envInfo.setVisibility(View.GONE);
        } else {
            envInfo.setVisibility(View.VISIBLE);
            envInfo.setText(currentConfig);
        }
    }

    private void moveMockLocation(int latDistance, int lngDistance) {
        try {
            ((Switch) findViewById(R.id.env_switch3)).setChecked(true);
            FloatGpsMockCache.mockToLocation(
                    GpsMockManager.getInstance().getLatitude() + GPSTools.getLatDiff(latDistance),
                    GpsMockManager.getInstance().getLongitude() + GPSTools.getLngDiff(GpsMockManager.getInstance().getLatitude(), lngDistance)
            );
            updateCurrentLocConfig(FloatGpsRouteMockCache.getCurrentLocConfig());
        } catch (Exception e) {
        }
    }

    private void copyToClipboard(String s) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        String label = "已拷贝值剪切板，可以到别的地方直接粘贴使用了";
        ToastUtils.showShort(label);
        ClipData clip = ClipData.newPlainText(label, s);
        clipboard.setPrimaryClip(clip);
    }

}
