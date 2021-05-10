package com.didichuxing.doraemonkit.kit.sysinfo;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.didichuxing.doraemonkit.util.ToastUtils;
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
    private RadioGroup mSortGroup;

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
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyWords = s.toString().trim();
                if (keyWords.isEmpty()) {
                    initData();
                } else {
                    notifyThirdKeyWordsLibInfos(keyWords);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mInfoList.setLayoutManager(layoutManager);
        mInfoItemAdapter = new ThirdLibInfoItemAdapter(getContext());
        mInfoList.setAdapter(mInfoItemAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.dk_divider));
        mInfoList.addItemDecoration(decoration);

        mSortGroup = findViewById(R.id.sort_option);
        mSortGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mInfoItemAdapter.setData(sortSysInfoItems(null));
            }
        });

        if (DokitThirdLibInfo.THIRD_LIB_INFOS == null || DokitThirdLibInfo.THIRD_LIB_INFOS.isEmpty()) {
            ToastUtils.showShort("检查gradle.properties中的DOKIT_THIRD_LIB_SWITCH值是否为true");
        }

    }

    private void initData() {
        List<SysInfoItem> sysInfoItems = new ArrayList<>();
        addThirdLibInfos(sysInfoItems);
        mInfoItemAdapter.setData(sortSysInfoItems(sysInfoItems));
    }

    //添加所有三方库信息
    private void addThirdLibInfos(List<SysInfoItem> sysInfoItems) {
        for (Map.Entry<String, String> entry : DokitThirdLibInfo.THIRD_LIB_INFOS.entrySet()) {
            sysInfoItems.add(new SysInfoItem(entry.getKey(), entry.getValue()));
        }
    }

    private List<SysInfoItem> sortSysInfoItems(List<SysInfoItem> sysInfoItems) {
        List<SysInfoItem> data = sysInfoItems == null ? mInfoItemAdapter.getData() : sysInfoItems;
        if (mSortGroup.getCheckedRadioButtonId() == R.id.sort_size) {
            Collections.sort(data, new Comparator<SysInfoItem>() {
                @Override
                public int compare(SysInfoItem o1, SysInfoItem o2) {
                    return (int) (Long.parseLong(o2.value) - Long.parseLong(o1.value));
                }
            });
        } else {
            Collections.sort(data, new Comparator<SysInfoItem>() {
                @Override
                public int compare(SysInfoItem o1, SysInfoItem o2) {
                    return o1.name.compareToIgnoreCase(o2.name);
                }
            });
        }
        return data;
    }

    //添加所有三方库信息
    private void notifyThirdKeyWordsLibInfos(String keyWords) {
        List<SysInfoItem> sysInfoItems = new ArrayList<>();
        for (Map.Entry<String, String> entry : DokitThirdLibInfo.THIRD_LIB_INFOS.entrySet()) {
            if (entry.getKey().toLowerCase().startsWith(keyWords.toLowerCase()) || entry.getKey().toLowerCase().contains(keyWords.toLowerCase())) {
                sysInfoItems.add(new SysInfoItem(entry.getKey(), entry.getValue()));
            }
        }
        mInfoItemAdapter.setData(sortSysInfoItems(sysInfoItems));

    }


}
