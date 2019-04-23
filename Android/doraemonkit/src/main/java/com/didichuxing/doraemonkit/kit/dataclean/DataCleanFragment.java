package com.didichuxing.doraemonkit.kit.dataclean;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.setting.SettingItem;
import com.didichuxing.doraemonkit.ui.setting.SettingItemAdapter;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.DividerItemDecoration;
import com.didichuxing.doraemonkit.ui.widget.titlebar.HomeTitleBar;
import com.didichuxing.doraemonkit.util.DataCleanUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglikun on 2018/11/17.
 */

public class DataCleanFragment extends BaseFragment {
    private RecyclerView mSettingList;
    private SettingItemAdapter mSettingItemAdapter;

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_data_clean;
    }

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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mSettingList.setLayoutManager(layoutManager);
        List<SettingItem> settingItems = new ArrayList<>();
        settingItems.add(new SettingItem(R.string.dk_kit_data_clean));
        mSettingItemAdapter = new SettingItemAdapter(getContext());
        mSettingItemAdapter.setData(settingItems);
        mSettingItemAdapter.setOnSettingItemClickListener(new SettingItemAdapter.OnSettingItemClickListener() {
            @Override
            public void onSettingItemClick(View view, SettingItem data) {
                if (data.desc == R.string.dk_kit_data_clean) {
                    DataCleanUtil.cleanApplicationData(getContext());
                    Toast.makeText(getContext(), R.string.dk_data_clean_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mSettingList.setAdapter(mSettingItemAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.dk_divider));
        mSettingList.addItemDecoration(decoration);
    }
}