package com.didichuxing.doraemonkit.kit.sysinfo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.aop.DokitThirdLibInfo;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.okhttp_api.OkHttpWrap;
import com.didichuxing.doraemonkit.util.DeviceUtils;
import com.didichuxing.doraemonkit.util.ExecutorUtil;
import com.didichuxing.doraemonkit.util.PermissionUtil;
import com.didichuxing.doraemonkit.util.UIUtils;
import com.didichuxing.doraemonkit.widget.recyclerview.DividerItemDecoration;
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 第三方库信息
 * Created by jintai on 2020/10/20.
 */

public class ThirdLibInfoFragment extends BaseFragment {
    private RecyclerView mInfoList;
    private ThirdLibInfoItemAdapter mInfoItemAdapter;


    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_third_lib_info;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            initView();
            initData();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initView() {
        mInfoList = findViewById(R.id.info_list);
        HomeTitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                getActivity().finish();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mInfoList.setLayoutManager(layoutManager);
        mInfoItemAdapter = new ThirdLibInfoItemAdapter(getContext());
        mInfoList.setAdapter(mInfoItemAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.dk_divider));
        mInfoList.addItemDecoration(decoration);
    }

    private void initData() throws Exception {
        List<SysInfoItem> sysInfoItems = new ArrayList<>();
        addThirdLibInfos(sysInfoItems);
        mInfoItemAdapter.setData(sysInfoItems);
    }

    //三方库信息
    private void addThirdLibInfos(List<SysInfoItem> sysInfoItems) {
        for (Map.Entry<String, String> entry : DokitThirdLibInfo.THIRD_LIB_INFOS.entrySet()) {
            sysInfoItems.add(new SysInfoItem(entry.getKey(), entry.getValue()));
        }

        Collections.sort(sysInfoItems, new Comparator<SysInfoItem>() {
            @Override
            public int compare(SysInfoItem o1, SysInfoItem o2) {
                return o1.name.compareToIgnoreCase(o2.name);
            }
        });

    }


}
