package com.didichuxing.doraemonkit.kit.alignruler;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.AlignRulerConfig;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.kit.core.SettingItem;
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter;
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar;

/**
 * Created by wanglikun on 2018/9/19.
 */

public class AlignRulerSettingFragment extends BaseFragment {
    private HomeTitleBar mTitleBar;
    private RecyclerView mSettingList;
    private SettingItemAdapter mSettingItemAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTitleBar();
    }

    private void initTitleBar() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                finish();
            }
        });
        mSettingList = findViewById(R.id.setting_list);
        mSettingList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSettingItemAdapter = new SettingItemAdapter(getContext());
        mSettingItemAdapter.append(new SettingItem(R.string.dk_kit_align_ruler, AlignRulerConfig.isAlignRulerOpen()));
        mSettingList.setAdapter(mSettingItemAdapter);
        mSettingItemAdapter.setOnSettingItemSwitchListener(new SettingItemAdapter.OnSettingItemSwitchListener() {
            @Override
            public void onSettingItemSwitch(View view, SettingItem data, boolean on) {
                if (data.desc == R.string.dk_kit_align_ruler) {
                    if (on) {
                        DoKit.launchFloating(AlignRulerMarkerDoKitView.class);
                        DoKit.launchFloating(AlignRulerLineDoKitView.class);
                    } else {
                        DoKit.removeFloating(AlignRulerMarkerDoKitView.class);
                        DoKit.removeFloating(AlignRulerLineDoKitView.class);
                    }
                    AlignRulerConfig.setAlignRulerOpen(on);
                }
            }
        });
    }

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_align_ruler_setting;
    }
}
