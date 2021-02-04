package com.didichuxing.doraemonkit.kit.fly.manual;

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
import com.didichuxing.doraemonkit.kit.fly.route.FloatGpsRouteMockCache;
import com.didichuxing.doraemonkit.kit.fly.common.LocInfo;
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager;
import com.didichuxing.doraemonkit.model.LatLng;

public class FloatGpsMockKitView extends SimpleDokitView {
    public static final String TAG = "FloatGpsMockKitView";
    private View mRootView;
    private static int sMockSpeed = 10;
    private TextView mMockSpeedTv;
    private SeekBar mSpeedSeekBar;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_mock_location;
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
                moveMockLocation(sMockSpeed, 0);
            }
        });
        ViewSetupHelper.setupButton(mRootView, R.id.btn_mock_gps_south, "下", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveMockLocation(-sMockSpeed, 0);
            }
        });
        ViewSetupHelper.setupButton(mRootView, R.id.btn_mock_gps_west, "左", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveMockLocation(0, -sMockSpeed);
            }
        });
        ViewSetupHelper.setupButton(mRootView, R.id.btn_mock_gps_east, "右", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveMockLocation(0, sMockSpeed);
            }
        });

        // 单点步进速度控制
        mMockSpeedTv = findViewById(R.id.tv_mock_speed);
        mSpeedSeekBar = findViewById(R.id.dk_sb_seekBar);
        updateSpeedView(mMockSpeedTv, mSpeedSeekBar);
        mSpeedSeekBar.setMax(500);
        mSpeedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    sMockSpeed = progress;
                    mMockSpeedTv.setText("步进速度控制:" + sMockSpeed);
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
                sMockSpeed -= 10;
                updateSpeedView(mMockSpeedTv, mSpeedSeekBar);
            }
        });
        findViewById(R.id.dk_btn_upMockSpeed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sMockSpeed += 10;
                updateSpeedView(mMockSpeedTv, mSpeedSeekBar);
            }
        });
    }

    private void updateSpeedView(TextView mockSpeed, SeekBar seekBar) {
        seekBar.setProgress(sMockSpeed);
        mMockSpeedTv.setText("步进速度控制:" + sMockSpeed);
    }

    private void updateCurrentLocConfig(LocInfo currentConfig) {
        TextView envInfo = this.findViewById(R.id.env_info3);
        updateGsonInfo(currentConfig.toString(), envInfo);
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


}
