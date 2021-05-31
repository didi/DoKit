package com.didichuxing.doraemonkit.kit.parameter.ram;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.DokitMemoryConfig;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.kit.parameter.AbsParameterFragment;
import com.didichuxing.doraemonkit.kit.performance.PerformanceDataManager;
import com.didichuxing.doraemonkit.kit.performance.PerformanceFragment;
import com.didichuxing.doraemonkit.kit.performance.datasource.DataSourceFactory;
import com.didichuxing.doraemonkit.kit.core.SettingItem;
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter;

import java.util.Collection;
import java.util.List;

public class RamMainPageFragment extends AbsParameterFragment {
    private static final String TAG = "RamMainPageFragment";

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
        return R.string.dk_ram_detection_title;
    }

    @Override
    protected int getPerformanceType() {
        return DataSourceFactory.TYPE_RAM;
    }

    @Override
    protected Collection<SettingItem> getSettingItems(List<SettingItem> list) {
        list.add(new SettingItem(R.string.dk_ram_detection_switch, DokitMemoryConfig.RAM_STATUS));
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
                DokitMemoryConfig.RAM_STATUS = on;
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
                    bundle.putInt(BundleKey.PERFORMANCE_TYPE, PerformanceFragment.RAM);
                    showContent(PerformanceFragment.class, bundle);

                }
            }
        };
    }

    protected void startMonitor() {
        PerformanceDataManager.getInstance().startMonitorMemoryInfo();
        openChartPage(R.string.dk_ram_detection_title, DataSourceFactory.TYPE_RAM);
    }

    protected void stopMonitor() {
        PerformanceDataManager.getInstance().stopMonitorMemoryInfo();
        closeChartPage();
    }


}
