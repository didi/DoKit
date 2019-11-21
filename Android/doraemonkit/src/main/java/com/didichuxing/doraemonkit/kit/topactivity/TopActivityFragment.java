package com.didichuxing.doraemonkit.kit.topactivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.TopActivityConfig;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.base.DokitIntent;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;
import com.didichuxing.doraemonkit.ui.setting.SettingItem;
import com.didichuxing.doraemonkit.ui.setting.SettingItemAdapter;
import com.didichuxing.doraemonkit.ui.widget.titlebar.HomeTitleBar;

/**
 * 项目名:    Android
 * 包名       com.didichuxing.doraemonkit.kit.topactivity
 * 文件名:    TopActivityFragment
 * 创建时间:  2019-04-29 on 12:16
 * 描述:
 *
 * @author 阿钟
 */

public class TopActivityFragment extends BaseFragment {
    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_top_activity;
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
                getActivity().finish();
            }
        });
        RecyclerView topActivityList = findViewById(R.id.top_activity_list);
        topActivityList.setLayoutManager(new LinearLayoutManager(getContext()));
        SettingItemAdapter topActivityAdapter = new SettingItemAdapter(getContext());
        topActivityAdapter.append(new SettingItem(R.string.dk_kit_top_activity, TopActivityConfig.isTopActivityOpen(getContext())));
        topActivityAdapter.setOnSettingItemSwitchListener(new SettingItemAdapter.OnSettingItemSwitchListener() {
            @Override
            public void onSettingItemSwitch(View view, SettingItem data, boolean on) {
                if (data.desc == R.string.dk_kit_top_activity) {
                    if (on) {
                        DokitIntent intent = new DokitIntent(TopActivityDokitView.class);
                        DokitViewManager.getInstance().attach(intent);
                        getActivity().finish();
                    } else {
                        DokitViewManager.getInstance().detach(TopActivityDokitView.class);
                    }
                    TopActivityConfig.setTopActivityOpen(getContext(), on);
                }
            }
        });
        topActivityList.setAdapter(topActivityAdapter);
    }
}
