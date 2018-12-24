package com.didichuxing.doraemonkit.kit.ram;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.PerformanceInfoConfig;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.kit.common.PerformanceDataManager;
import com.didichuxing.doraemonkit.kit.common.PerformanceFragment;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.realtime.datasource.DataSourceFactory;
import com.didichuxing.doraemonkit.ui.realtime.OnFloatPageChangeListener;
import com.didichuxing.doraemonkit.ui.realtime.RealTimeChartIconPage;
import com.didichuxing.doraemonkit.ui.realtime.RealTimeChartPage;
import com.didichuxing.doraemonkit.ui.setting.SettingItem;
import com.didichuxing.doraemonkit.ui.setting.SettingItemAdapter;
import com.didichuxing.doraemonkit.ui.widget.titlebar.HomeTitleBar;

public class RamMainPageFragment extends BaseFragment implements OnFloatPageChangeListener {
    private static final String TAG = "RamMainPageFragment";
    private SettingItemAdapter mSettingItemAdapter;
    private RecyclerView mSettingList;

    @Override
    protected int onRequestLayout() {
        return R.layout.fragment_cpu_main_page;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PerformanceDataManager.getInstance().init(getContext().getApplicationContext());
        initView();
    }

    private void initView() {
        HomeTitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle(R.string.dk_ram_detection_title);
        titleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                getActivity().finish();
            }
        });

        mSettingList = findViewById(R.id.setting_list);
        mSettingList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSettingItemAdapter = new SettingItemAdapter(getContext());
        mSettingItemAdapter.append(new SettingItem(R.string.dk_ram_detection_switch, PerformanceInfoConfig.isMemoryOpen(getContext())));
        mSettingItemAdapter.append(new SettingItem(R.string.dk_item_cache_log, R.drawable.dk_more_icon));

        mSettingItemAdapter.setOnSettingItemSwitchListener(new SettingItemAdapter.OnSettingItemSwitchListener() {
            @Override
            public void onSettingItemSwitch(View view, SettingItem data, boolean on) {
                if (data.desc == R.string.dk_ram_detection_switch) {
                    if (on) {
                        startMonitor();
                    } else {
                        stopMonitor();
                    }
                    PerformanceInfoConfig.setMemoryOpen(getContext(), on);
                }
            }
        });
        mSettingItemAdapter.setOnSettingItemClickListener(new SettingItemAdapter.OnSettingItemClickListener() {
            @Override
            public void onSettingItemClick(View view, SettingItem data) {
                if (data.desc == R.string.dk_item_cache_log) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(BundleKey.PERFORMANCE_TYPE, PerformanceFragment.RAM);
                    showContent(PerformanceFragment.class, bundle);
                }
            }
        });
        mSettingList.setAdapter(mSettingItemAdapter);
//        if (PerformanceInfoConfig.isMemoryOpen(getContext())) {
//            startMonitor();
//        }
    }

    private void startMonitor() {
        PerformanceDataManager.getInstance().startMonitorMemoryInfo();
        String title = getString(R.string.dk_ram_detection_title);
        int type = DataSourceFactory.TYPE_MEMORY;
        RealTimeChartPage.openChartPage(title, type, RealTimeChartPage.DEFAULT_REFRESH_INTERVAL, this);
    }

    private void stopMonitor() {
        PerformanceDataManager.getInstance().stopMonitorMemoryInfo();
        RealTimeChartPage.closeChartPage();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RealTimeChartPage.removeCloseListener();
    }

    @Override
    public void onFloatPageClose(String tag) {
        if (!TextUtils.equals(RealTimeChartIconPage.TAG, tag)) {
            return;
        }
        if (mSettingList == null || mSettingList.isComputingLayout()) {
            return;
        }
        if (mSettingItemAdapter == null) {
            return;
        }
        if (!mSettingItemAdapter.getData().get(0).isChecked) {
            return;
        }
        mSettingItemAdapter.getData().get(0).isChecked = false;
        mSettingItemAdapter.notifyItemChanged(0);
    }

    @Override
    public void onFloatPageOpen(String tag) {

    }
}
