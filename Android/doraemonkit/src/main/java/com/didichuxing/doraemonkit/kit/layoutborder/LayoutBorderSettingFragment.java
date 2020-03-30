package com.didichuxing.doraemonkit.kit.layoutborder;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.LayoutBorderConfig;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.base.DokitIntent;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;
import com.didichuxing.doraemonkit.ui.setting.SettingItem;
import com.didichuxing.doraemonkit.ui.setting.SettingItemAdapter;
import com.didichuxing.doraemonkit.ui.widget.titlebar.HomeTitleBar;

/**
 * Created by wanglikun on 2018/10/9.
 */

public class LayoutBorderSettingFragment extends BaseFragment {
    private static final String TAG = "LayoutBorderSettingFragment";
    private RecyclerView mSettingList;
    private SettingItemAdapter mSettingItemAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        HomeTitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                finish();
            }
        });
        mSettingList = findViewById(R.id.setting_list);
        mSettingList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSettingItemAdapter = new SettingItemAdapter(getContext());
        mSettingItemAdapter.append(new SettingItem(R.string.dk_kit_layout_border, LayoutBorderConfig.isLayoutBorderOpen()));
        mSettingItemAdapter.append(new SettingItem(R.string.dk_layout_level, LayoutBorderConfig.isLayoutLevelOpen()));
        mSettingItemAdapter.setOnSettingItemSwitchListener(new SettingItemAdapter.OnSettingItemSwitchListener() {
            @Override
            public void onSettingItemSwitch(View view, SettingItem data, boolean on) {
                if (data.desc == R.string.dk_kit_layout_border) {
                    if (on) {
                        LayoutBorderManager.getInstance().start();
                    } else {
                        LayoutBorderManager.getInstance().stop();
                    }
                    LayoutBorderConfig.setLayoutBorderOpen(on);
                } else if (data.desc == R.string.dk_layout_level) {
                    if (on) {
                        DokitIntent intent = new DokitIntent(LayoutLevelDokitView.class);
                        intent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
                        DokitViewManager.getInstance().attach(intent);
                    } else {
                        DokitViewManager.getInstance().detach(LayoutLevelDokitView.class);
                    }
                    LayoutBorderConfig.setLayoutLevelOpen(on);
                }
            }
        });
        mSettingList.setAdapter(mSettingItemAdapter);
    }

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_layout_border_setting;
    }
}