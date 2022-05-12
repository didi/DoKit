package com.didichuxing.doraemondemo.amap;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.didichuxing.doraemondemo.R;
import com.didichuxing.doraemondemo.dokit.SimpleDoKitView;
import com.didichuxing.doraemonkit.gps_mock.lbs.common.LocInfo;
import com.didichuxing.doraemonkit.gps_mock.lbs.manual.FloatGpsMockCache;
import com.didichuxing.doraemonkit.gps_mock.lbs.preset.FloatGpsPresetMockCache;
import com.didichuxing.doraemonkit.gps_mock.lbs.preset.MockLocList;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

public class FloatGpsPresetMockKitView extends SimpleDoKitView {
    public static final String TAG = "FloatGpsPresetMockKitView";
    private View mRootView;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_mock_location_preset;
    }


    @Override
    public void onViewCreated(FrameLayout rootView) {
        super.onViewCreated(rootView);
        mRootView = rootView;
        setMockLocationConfig();
    }

    private void setMockLocationConfig() {
        // 模拟位置预 单点
        final MockLocList locationList = FloatGpsPresetMockCache.sMockLocationList;
        ArrayList<String> configString = new ArrayList<>();
        for (LocInfo locInfo : locationList) {
            configString.add(locInfo.locName);
        }

        LocInfo config = FloatGpsPresetMockCache.getMockLocConfig();
        if (config != null) {
            Log.i(getTAG(), "⚠️setMockLocationConfig() setSelection called" + config.locName);
            updateCurrentLocConfig(config);
        }

        FlexboxLayout flexboxLayout = findViewById(R.id.cl_mock_gps_flexbox_container);
        for (final LocInfo locInfo : FloatGpsPresetMockCache.sMockLocationList) {
            Button button = new Button(getContext());
            button.setText(locInfo.locName);
            button.setMinWidth(0);
            button.setMinHeight(0);
            button.setMinimumWidth(0);//必须同时设置这个
            button.setMinimumHeight(0);//必须同时设置这个
            button.setTextSize(9);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Log.d(getTAG(), "⚠️onNewItemSelected() called with: locInfo = [" + locInfo + "]");
                        FloatGpsMockCache.mockToLocation(locInfo.lat, locInfo.lng);
                        ((Switch) findViewById(R.id.env_switch3)).setChecked(true);
                        updateCurrentLocConfig(locInfo);
                    } catch (Exception e) {
                    }
                }
            });
            flexboxLayout.addView(button);
        }
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

}
