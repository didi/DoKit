package com.didichuxing.doraemonkit.kit.network.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;

import com.didichuxing.doraemonkit.util.FileIOUtils;
import com.didichuxing.doraemonkit.util.FileUtils;
import com.didichuxing.doraemonkit.util.GsonUtils;
import com.didichuxing.doraemonkit.util.PathUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.DokitMemoryConfig;
import com.didichuxing.doraemonkit.kit.core.DoKitManager;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.bean.WhiteHostBean;
import com.didichuxing.doraemonkit.kit.parameter.AbsParameterFragment;
import com.didichuxing.doraemonkit.kit.performance.datasource.DataSourceFactory;
import com.didichuxing.doraemonkit.kit.core.SettingItem;
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author jint
 */
public class NetWorkMonitorFragment extends AbsParameterFragment {
    RecyclerView mHostRv;
    WhiteHostAdapter mHostAdapter;
    List<WhiteHostBean> mHostBeans = new ArrayList<>();

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
                DokitMemoryConfig.NETWORK_STATUS = on;
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
        mHostRv = findViewById(R.id.host_list);
        mHostRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (DoKitManager.WHITE_HOSTS.isEmpty()) {
            String whiteHostArray = FileIOUtils.readFile2String(whiteHostPath);
            if (TextUtils.isEmpty(whiteHostArray)) {
                mHostBeans.add(new WhiteHostBean("", true));
            } else {
                mHostBeans = GsonUtils.fromJson(whiteHostArray, GsonUtils.getListType(WhiteHostBean.class));
                DoKitManager.WHITE_HOSTS.clear();
                DoKitManager.WHITE_HOSTS.addAll(mHostBeans);
            }
        } else {
            mHostBeans.addAll(DoKitManager.WHITE_HOSTS);
        }

        mHostAdapter = new WhiteHostAdapter(R.layout.dk_item_white_host, mHostBeans);
        mHostRv.setAdapter(mHostAdapter);
    }

    private void startMonitor() {
        NetworkManager.get().startMonitor();
        openChartPage(R.string.dk_kit_net_monitor, DataSourceFactory.TYPE_NETWORK);
    }

    private void stopMonitor() {
        NetworkManager.get().stopMonitor();
        closeChartPage();
    }

    private String whiteHostPath = PathUtils.getInternalAppFilesPath() + File.separator + "white_host.json";

    @Override
    public void onDestroy() {
        super.onDestroy();
        //保存白名单
        List<WhiteHostBean> hostBeans = mHostAdapter.getData();
        if (hostBeans.size() == 1 && TextUtils.isEmpty(hostBeans.get(0).getHost())) {
            DoKitManager.WHITE_HOSTS.clear();
            FileUtils.delete(whiteHostPath);
            return;
        }
        DoKitManager.WHITE_HOSTS.clear();
        DoKitManager.WHITE_HOSTS.addAll(hostBeans);
        String hostArray = GsonUtils.toJson(hostBeans);
        //保存到本地
        FileUtils.delete(whiteHostPath);
        FileIOUtils.writeFileFromString(whiteHostPath, hostArray);
        //ToastUtils.showShort("host白名单已保存");

    }
}
