package com.didichuxing.doraemonkit.kit.sysinfo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.aop.DokitThirdLibInfo;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.widget.recyclerview.DividerItemDecoration;
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 第三方库信息
 * Created by jintai on 2020/10/20.
 */

public class ThirdLibInfoFragment extends BaseFragment {
    private RecyclerView mInfoList;
    private ThirdLibInfoItemAdapter mInfoItemAdapter;
    private EditText mEditText;

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
        mEditText = findViewById(R.id.edittext);
        TextView mTvSearch = findViewById(R.id.tv_search);
        mTvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyWords = mEditText.getText().toString().trim();
                if (keyWords.isEmpty()) {
                    initData();
                } else {
                    notifyThirdKeyWordsLibInfos(keyWords);
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mInfoList.setLayoutManager(layoutManager);
        mInfoItemAdapter = new ThirdLibInfoItemAdapter(getContext());
        mInfoList.setAdapter(mInfoItemAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.dk_divider));
        mInfoList.addItemDecoration(decoration);

        if (DokitThirdLibInfo.THIRD_LIB_INFOS == null || DokitThirdLibInfo.THIRD_LIB_INFOS.isEmpty()) {
            ToastUtils.showShort("检查gradle.properties中的DOKIT_THIRD_LIB_SWITCH值是否为true");
        }

    }

    private void initData() {
        List<SysInfoItem> sysInfoItems = new ArrayList<>();
        addThirdLibInfos(sysInfoItems);
        mInfoItemAdapter.setData(sysInfoItems);
    }

    //添加所有三方库信息
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


    //添加所有三方库信息
    private void notifyThirdKeyWordsLibInfos(String keyWords) {
        List<SysInfoItem> sysInfoItems = new ArrayList<>();
        for (Map.Entry<String, String> entry : DokitThirdLibInfo.THIRD_LIB_INFOS.entrySet()) {
            if (entry.getKey().startsWith(keyWords) || entry.getKey().contains(keyWords)) {
                sysInfoItems.add(new SysInfoItem(entry.getKey(), entry.getValue()));
            }
        }

        Collections.sort(sysInfoItems, new Comparator<SysInfoItem>() {
            @Override
            public int compare(SysInfoItem o1, SysInfoItem o2) {
                return o1.name.compareToIgnoreCase(o2.name);
            }
        });

        mInfoItemAdapter.setData(sysInfoItems);

    }


}
