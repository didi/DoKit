package com.didichuxing.doraemonkit.kit.parameter.cpu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.PerformanceMemoryInfoConfig;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.kit.common.PerformanceDataManager;
import com.didichuxing.doraemonkit.kit.common.PerformanceFragment;
import com.didichuxing.doraemonkit.kit.parameter.AbsParameterFragment;
import com.didichuxing.doraemonkit.ui.realtime.datasource.DataSourceFactory;
import com.didichuxing.doraemonkit.ui.setting.SettingItem;
import com.didichuxing.doraemonkit.ui.setting.SettingItemAdapter;

import java.util.Collection;
import java.util.List;

public class CpuMainPageFragment extends AbsParameterFragment {
    private static final String TAG = "CpuMainPageFragment";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PerformanceDataManager.getInstance().init(getContext());
    }

    @Override
    protected int getTitle() {
        return R.string.dk_frameinfo_cpu;
    }

    @Override
    protected int getPerformanceType() {
        return DataSourceFactory.TYPE_CPU;
    }

    @Override
    protected Collection<SettingItem> getSettingItems(List<SettingItem> list) {
        list.add(new SettingItem(R.string.dk_cpu_detection_switch, PerformanceMemoryInfoConfig.CPU_STATUS));
        //list.add(new SettingItem(R.string.dk_item_cache_log, R.drawable.dk_more_icon));
        return list;
    }

    @Override
    protected SettingItemAdapter.OnSettingItemSwitchListener getItemSwitchListener() {
        return new SettingItemAdapter.OnSettingItemSwitchListener() {
            @Override
            public void onSettingItemSwitch(View view, SettingItem data, boolean on) {
                // TODO: 2019-10-11 测试 需要恢复
                if (on) {
                    startMonitor();
                } else {
                    stopMonitor();
                }
                PerformanceMemoryInfoConfig.CPU_STATUS = on;

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
                    bundle.putInt(BundleKey.PERFORMANCE_TYPE, PerformanceFragment.CPU);
                    showContent(PerformanceFragment.class, bundle);
                }
            }
        };
    }

    private void startMonitor() {

        PerformanceDataManager.getInstance().startMonitorCPUInfo();
        openChartPage(R.string.dk_frameinfo_cpu, DataSourceFactory.TYPE_CPU);
    }

    private void stopMonitor() {
        PerformanceDataManager.getInstance().stopMonitorCPUInfo();
        closeChartPage();
    }

}
