package com.didichuxing.doraemonkit.kit.parameter.frameInfo;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.DokitMemoryConfig;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.kit.performance.PerformanceDataManager;
import com.didichuxing.doraemonkit.kit.performance.PerformanceFragment;
import com.didichuxing.doraemonkit.kit.parameter.AbsParameterFragment;
import com.didichuxing.doraemonkit.kit.performance.datasource.DataSourceFactory;
import com.didichuxing.doraemonkit.kit.core.SettingItem;
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter;

import java.util.Collection;
import java.util.List;

/**
 * Created by wanglikun on 2018/9/13.
 */

public class FrameInfoFragment extends AbsParameterFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PerformanceDataManager.getInstance().init();
    }

    @Override
    protected int getTitle() {
        return R.string.dk_kit_frame_info_desc;
    }

    @Override
    protected int getPerformanceType() {
        return DataSourceFactory.TYPE_FPS;
    }

    @Override
    protected Collection<SettingItem> getSettingItems(List<SettingItem> list) {
        list.add(new SettingItem(R.string.dk_frameinfo_detection_switch, DokitMemoryConfig.FPS_STATUS));
        list.add(new SettingItem(R.string.dk_item_cache_log, R.mipmap.dk_more_icon));
        return list;
    }

    @Override
    protected SettingItemAdapter.OnSettingItemSwitchListener getItemSwitchListener() {
        return new SettingItemAdapter.OnSettingItemSwitchListener() {
            @Override
            public void onSettingItemSwitch(View view, SettingItem data, boolean on) {
                if (on) {
                    startMonitor();
                } else {
                    stopMonitor();
                }

                DokitMemoryConfig.FPS_STATUS = on;
            }
        };
    }

    @Override
    protected SettingItemAdapter.OnSettingItemClickListener getItemClickListener() {
        return new SettingItemAdapter.OnSettingItemClickListener() {
            @Override
            public void onSettingItemClick(View view, SettingItem data) {
                if (data.desc == R.string.dk_item_cache_log) {

                    Bundle bundle = new Bundle();
                    bundle.putInt(BundleKey.PERFORMANCE_TYPE, PerformanceFragment.FPS);
                    showContent(PerformanceFragment.class, bundle);
                }

            }
        };
    }

    private void startMonitor() {
        PerformanceDataManager.getInstance().startMonitorFrameInfo();
        openChartPage(R.string.dk_kit_frame_info_desc, DataSourceFactory.TYPE_FPS);
    }

    private void stopMonitor() {
        PerformanceDataManager.getInstance().stopMonitorFrameInfo();
        closeChartPage();
    }

}
