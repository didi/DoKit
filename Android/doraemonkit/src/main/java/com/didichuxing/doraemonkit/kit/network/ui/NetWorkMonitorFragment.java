package com.didichuxing.doraemonkit.kit.network.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.PerformanceMemoryInfoConfig;
import com.didichuxing.doraemonkit.kit.health.AppHealthInfoUtil;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.parameter.AbsParameterFragment;
import com.didichuxing.doraemonkit.ui.realtime.datasource.DataSourceFactory;
import com.didichuxing.doraemonkit.ui.setting.SettingItem;
import com.didichuxing.doraemonkit.ui.setting.SettingItemAdapter;

import java.util.Collection;
import java.util.List;

public class NetWorkMonitorFragment extends AbsParameterFragment {


    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_net_monitor;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initCustomView();
    }

    @Override
    protected int getTitle() {
        return R.string.dk_kit_net_monitor;
    }

    @Override
    protected int getPerformanceType() {
        return DataSourceFactory.TYPE_NETWORK;
    }

    @Override
    protected Collection<SettingItem> getSettingItems(List<SettingItem> list) {
        list.add(new SettingItem(R.string.dk_net_monitor_detection_switch, NetworkManager.isActive()));
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
                PerformanceMemoryInfoConfig.NETWORK_STATUS = on;
            }
        };
    }

    @Override
    protected SettingItemAdapter.OnSettingItemClickListener getItemClickListener() {
        return new SettingItemAdapter.OnSettingItemClickListener() {
            @Override
            public void onSettingItemClick(View view, SettingItem data) {

            }
        };
    }

    private void initCustomView() {
        findViewById(R.id.btn_net_summary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContent(NetWorkMainPagerFragment.class);
            }
        });
    }

    private void startMonitor() {
        NetworkManager.get().startMonitor();
        openChartPage(R.string.dk_kit_net_monitor, DataSourceFactory.TYPE_NETWORK);
    }

    private void stopMonitor() {
        NetworkManager.get().stopMonitor();
        closeChartPage();
    }

}
