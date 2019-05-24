package com.didichuxing.doraemonkit.kit.custom;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.PerformanceInfoConfig;
import com.didichuxing.doraemonkit.kit.common.PerformanceDataManager;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.realtime.OnFloatPageChangeListener;
import com.didichuxing.doraemonkit.ui.realtime.RealTimeChartIconPage;
import com.didichuxing.doraemonkit.ui.realtime.RealTimeChartPage;
import com.didichuxing.doraemonkit.ui.setting.SettingItem;
import com.didichuxing.doraemonkit.ui.setting.SettingItemAdapter;
import com.didichuxing.doraemonkit.ui.widget.titlebar.HomeTitleBar;

public class MonitorDataUploadFragment extends BaseFragment implements OnFloatPageChangeListener {
    private static final String TAG = "MonitorDataUploadFragment";
    private SettingItemAdapter mSettingItemAdapter;
    private RecyclerView mSettingList;
    private TextView mCommitButton;

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_monitor_data_upload_page;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PerformanceDataManager.getInstance().init(getContext().getApplicationContext());
        initView();
        initCommitButton();
    }

    private void initView() {
        HomeTitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle(R.string.dk_category_performance);
        titleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                getActivity().finish();
            }
        });
        mCommitButton = findViewById(R.id.commit);
        mSettingList = findViewById(R.id.setting_list);
        mSettingList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSettingItemAdapter = new SettingItemAdapter(getContext());
        mSettingItemAdapter.append(new SettingItem(R.string.dk_frameinfo_fps, PerformanceInfoConfig.isFPSOpen(getContext())));
        mSettingItemAdapter.append(new SettingItem(R.string.dk_frameinfo_cpu, PerformanceInfoConfig.isCPUOpen(getContext())));
        mSettingItemAdapter.append(new SettingItem(R.string.dk_frameinfo_ram, PerformanceInfoConfig.isMemoryOpen(getContext())));
        mSettingItemAdapter.append(new SettingItem(R.string.dk_kit_net_monitor, PerformanceInfoConfig.isTrafficOpen(getContext())));

        mSettingItemAdapter.setOnSettingItemSwitchListener(new SettingItemAdapter.OnSettingItemSwitchListener() {
            @Override
            public void onSettingItemSwitch(View view, SettingItem data, boolean on) {
                if (data.desc == R.string.dk_frameinfo_fps) {
                    PerformanceInfoConfig.setFPSOpen(getContext(), on);
                } else if (data.desc == R.string.dk_frameinfo_cpu) {
                    PerformanceInfoConfig.setCPUOpen(getContext(), on);
                } else if (data.desc == R.string.dk_frameinfo_ram) {
                    PerformanceInfoConfig.setMemoryOpen(getContext(), on);
                } else if (data.desc == R.string.dk_kit_net_monitor) {
                    PerformanceInfoConfig.setTrafficOpen(getContext(), on);
                }
                setCommitButtonState();
            }
        });
        mSettingList.setAdapter(mSettingItemAdapter);
      mCommitButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if (mCommitButton.getText().equals(getString(R.string.dk_platform_monitor_data_button_stop))) {
                  mCommitButton.setText(R.string.dk_platform_monitor_data_button);
                  PerformanceDataManager.getInstance().stopUploadMonitorData();
              } else {
                  mCommitButton.setText(R.string.dk_platform_monitor_data_button_stop);
                  PerformanceDataManager.getInstance().startUploadMonitorData();
              }
          }
      });
    }

    private boolean checkCommitButtonEnable() {
        if (PerformanceInfoConfig.isCPUOpen(getContext()) ||
                PerformanceInfoConfig.isFPSOpen(getContext()) ||
                PerformanceInfoConfig.isMemoryOpen(getContext()) ||
                PerformanceInfoConfig.isTrafficOpen(getContext())) {
            return true;
        } else {
            return false;
        }
    }

    private void setCommitButtonState(){
        if (checkCommitButtonEnable()) {
            mCommitButton.setEnabled(true);
        } else {
            mCommitButton.setEnabled(false);
        }
    }

    private void initCommitButton() {
        setCommitButtonState();
        if (checkCommitButtonEnable() && PerformanceDataManager.getInstance().isUploading()) {
            mCommitButton.setText(R.string.dk_platform_monitor_data_button_stop);
        } else {
            mCommitButton.setText(R.string.dk_platform_monitor_data_button);
        }
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
