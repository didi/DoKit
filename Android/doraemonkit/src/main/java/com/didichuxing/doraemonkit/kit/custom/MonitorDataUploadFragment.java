package com.didichuxing.doraemonkit.kit.custom;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig;
import com.didichuxing.doraemonkit.kit.common.PerformanceDataManager;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.base.DokitIntent;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;
import com.didichuxing.doraemonkit.ui.dialog.DialogInfo;
import com.didichuxing.doraemonkit.ui.dialog.SimpleDialogListener;
import com.didichuxing.doraemonkit.ui.setting.SettingItem;
import com.didichuxing.doraemonkit.ui.setting.SettingItemAdapter;
import com.didichuxing.doraemonkit.ui.widget.titlebar.HomeTitleBar;

import java.io.File;

/**
 * 多功能性能检测整合页面
 */
public class MonitorDataUploadFragment extends BaseFragment {
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
        initView();
        initCommitButton();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PerformanceDataManager.getInstance().init(getContext());
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
        mSettingItemAdapter.append(new SettingItem(R.string.dk_frameinfo_fps, PerformanceSpInfoConfig.isFPSOpen(getContext())));
        mSettingItemAdapter.append(new SettingItem(R.string.dk_frameinfo_cpu, PerformanceSpInfoConfig.isCPUOpen(getContext())));
        mSettingItemAdapter.append(new SettingItem(R.string.dk_frameinfo_ram, PerformanceSpInfoConfig.isMemoryOpen(getContext())));
        mSettingItemAdapter.append(new SettingItem(R.string.dk_kit_net_monitor, PerformanceSpInfoConfig.isTrafficOpen(getContext())));
        mSettingItemAdapter.append(new SettingItem(R.string.dk_kit_ui_monitor, PerformanceSpInfoConfig.isFrameUiOpen(getContext())));
        mSettingItemAdapter.append(new SettingItem(R.string.dk_platform_monitor_view_stat_data, R.drawable.dk_more_icon));

        mSettingItemAdapter.setOnSettingItemSwitchListener(new SettingItemAdapter.OnSettingItemSwitchListener() {
            @Override
            public void onSettingItemSwitch(View view, SettingItem data, boolean on) {
                if (data.desc == R.string.dk_frameinfo_fps) {
                    PerformanceSpInfoConfig.setFPSOpen(getContext(), on);
                } else if (data.desc == R.string.dk_frameinfo_cpu) {
                    PerformanceSpInfoConfig.setCPUOpen(getContext(), on);
                } else if (data.desc == R.string.dk_frameinfo_ram) {
                    PerformanceSpInfoConfig.setMemoryOpen(getContext(), on);
                } else if (data.desc == R.string.dk_kit_net_monitor) {
                    PerformanceSpInfoConfig.setTrafficOpen(getContext(), on);
                } else if (data.desc == R.string.dk_kit_ui_monitor) {
                    PerformanceSpInfoConfig.setFrameUiOpen(getContext(), on);
                }
                setCommitButtonState();
            }
        });
        mSettingItemAdapter.setOnSettingItemClickListener(new SettingItemAdapter.OnSettingItemClickListener() {
            @Override
            public void onSettingItemClick(View view, SettingItem data) {
                if (data.desc == R.string.dk_platform_monitor_view_stat_data) {
                    showContent(PageDataFragment.class);
                }
            }
        });
        mSettingList.setAdapter(mSettingItemAdapter);
        mCommitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommitButton.getText().equals(getString(R.string.dk_platform_monitor_data_button_stop))) {
                    DialogInfo dialogInfo = new DialogInfo();
                    dialogInfo.title = getString(R.string.dk_platform_monitor_data_button_stop);
                    dialogInfo.listener = new SimpleDialogListener() {
                        @Override
                        public boolean onPositive() {
                            if (getActivity() != null) {
                                ToastUtils.showLong("数据保存目录:" + getFilePath(getActivity()));
                            }
                            mCommitButton.setText(R.string.dk_platform_monitor_data_button);
                            PerformanceDataManager.getInstance().stopUploadMonitorData();
                            if (PerformanceSpInfoConfig.isFrameUiOpen(getContext())) {
                                DokitViewManager.getInstance().detach(RealTimePerformDataDokitView.class.getSimpleName());
                            }

                            return true;
                        }

                        @Override
                        public boolean onNegative() {
                            return true;
                        }
                    };
                    showDialog(dialogInfo);
                } else {
                    DialogInfo dialogInfo = new DialogInfo();
                    dialogInfo.title = getString(R.string.dk_platform_monitor_data_button);
                    dialogInfo.listener = new SimpleDialogListener() {
                        @Override
                        public boolean onPositive() {
                            mCommitButton.setText(R.string.dk_platform_monitor_data_button_stop);
                            PerformanceDataManager.getInstance().startUploadMonitorData();
                            if (PerformanceSpInfoConfig.isFrameUiOpen(getContext())) {
                                DokitIntent dokitIntent = new DokitIntent(RealTimePerformDataDokitView.class);
                                dokitIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
                                DokitViewManager.getInstance().attach(dokitIntent);
                            }


                            return true;
                        }

                        @Override
                        public boolean onNegative() {
                            return true;
                        }
                    };
                    showDialog(dialogInfo);
                }
            }
        });
    }

    /**
     * 获取数据保存目录
     *
     * @param context
     * @return
     */
    private String getFilePath(Context context) {
        return context.getCacheDir() + File.separator + "doraemon/";
    }

    private boolean checkCommitButtonEnable() {
        if (PerformanceSpInfoConfig.isCPUOpen(getContext()) ||
                PerformanceSpInfoConfig.isFPSOpen(getContext()) ||
                PerformanceSpInfoConfig.isMemoryOpen(getContext()) ||
                PerformanceSpInfoConfig.isTrafficOpen(getContext())) {
            return true;
        } else {
            return false;
        }
    }

    private void setCommitButtonState() {
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


}
