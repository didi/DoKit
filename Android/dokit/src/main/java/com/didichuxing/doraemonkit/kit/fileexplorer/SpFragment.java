package com.didichuxing.doraemonkit.kit.fileexplorer;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.constant.SpInputType;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar;
import com.didichuxing.doraemonkit.util.DoKitSPUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.didichuxing.doraemonkit.util.DoKitFileUtil.XML;

public class SpFragment extends BaseFragment {
    private SharedPreferences.Editor edit;
    private String spTableName;


    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_sp_show;
    }

    private List<SpBean> getSpBeans() {
        ArrayList<SpBean> spBeans = new ArrayList<>();

        File mFile = (File) getArguments().getSerializable(BundleKey.FILE_KEY);
        if (mFile == null) {
            return spBeans;
        }
        spTableName = mFile.getName().replace(XML, "");
        SharedPreferences sp = DoKitSPUtil.getSharedPrefs(spTableName);
        edit = sp.edit();
        Map<String, ?> all = sp.getAll();
        if (all.isEmpty()) {
            return spBeans;
        }
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            spBeans.add(new SpBean(entry.getKey(), entry.getValue()));
        }
        return spBeans;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<SpBean> spBeans = getSpBeans();
        if (spBeans.isEmpty()) {
            finish();
            return;
        }
        RecyclerView recyclerView = findViewById(R.id.rv_sp);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        SpAdapter spAdapter = new SpAdapter(getActivity());
        spAdapter.setOnSpDataChangerListener(new SpAdapter.OnSpDataChangerListener() {
            @Override
            public void onDataChanged(SpBean bean) {
                spUpData(bean);
            }
        });
        spAdapter.append(spBeans);
        recyclerView.setAdapter(spAdapter);
        if (spTableName != null) {
            TitleBar mTitleBar = findViewById(R.id.title_bar);
            mTitleBar.setTitle(spTableName);
            mTitleBar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
                @Override
                public void onLeftClick() {
                    finish();
                }

                @Override
                public void onRightClick() {

                }
            });
        }

    }


    public void spUpData(SpBean bean) {
        String key = bean.key;
        switch (bean.value.getClass().getSimpleName()) {
            case SpInputType.STRING:
                DoKitSPUtil.putString(key, bean.value.toString());
                break;
            case SpInputType.BOOLEAN:
                DoKitSPUtil.putBoolean(spTableName, key, (Boolean) bean.value);
                break;
            case SpInputType.INTEGER:
                DoKitSPUtil.putInt(spTableName, key, (Integer) bean.value);
                break;
            case SpInputType.FLOAT:
                DoKitSPUtil.putFloat(spTableName, key, (Float) bean.value);
                break;
            case SpInputType.LONG:
                DoKitSPUtil.putLong(spTableName, key, (Long) bean.value);
                break;
            default:
                break;
        }

    }

}
