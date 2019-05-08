package com.didichuxing.doraemonkit.kit.fileexplorer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.constant.SpInputType;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.widget.titlebar.TitleBar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import static com.didichuxing.doraemonkit.util.FileUtil.XML;

public class SpFragment extends BaseFragment {
    private ArrayList<SpBean> spBeans;
    private SharedPreferences.Editor edit;
    private String spNameFileName;


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_submit) {
                boolean commit = edit.commit();
                if (commit) {
                    finish();
                    showToast(R.string.dk_success);
                } else {
                    showToast(R.string.dk_fail);
                }
            } else if (id == R.id.btn_cancel) {
                finish();
            }

        }
    };

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_sp_show;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getArguments();
        if (data != null) {
            File mFile = (File) data.getSerializable(BundleKey.FILE_KEY);
            spNameFileName = mFile.getName().replace(XML, "");
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(spNameFileName, Context.MODE_PRIVATE);
            edit = sharedPreferences.edit();

            Map<String, ?> all = sharedPreferences.getAll();
            spBeans = new ArrayList<>(all.size());
            SpBean spBean;
            for (Map.Entry<String, ?> entry : all.entrySet()) {
                Object value = entry.getValue();
                spBean = new SpBean();
                spBean.key = entry.getKey();
                spBean.value = value;
                spBeans.add(spBean);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (spBeans != null && spBeans.size() > 0) {
            TitleBar mTitleBar = findViewById(R.id.title_bar);
            mTitleBar.setTitle(spNameFileName);
            mTitleBar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
                @Override
                public void onLeftClick() {
                    onBackPressed();
                }

                @Override
                public void onRightClick() {

                }
            });
            RecyclerView recyclerView = findViewById(R.id.rv_sp);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
            SpAdapter spAdapter = new SpAdapter(getActivity());
            spAdapter.append(spBeans);
            spAdapter.setOnSpDataChangerListener(new SpAdapter.OnSpDataChangerListener() {
                @Override
                public void onDataChanged(String key, Object o) {
                    spUpData(key, o);
                }
            });
            recyclerView.setAdapter(spAdapter);

            findViewById(R.id.btn_submit).setOnClickListener(mOnClickListener);
            findViewById(R.id.btn_cancel).setOnClickListener(mOnClickListener);
        } else {
            finish();
        }
    }

    public void spUpData(String key, Object o) {
        String simpleName = o.getClass().getSimpleName();
        if (simpleName.equals(SpInputType.STRING)) {
            edit.putString(key, o.toString());
        }
        if (simpleName.equals(SpInputType.BOOLEAN)) {
            edit.putBoolean(key, (Boolean) o);
        }
        if (simpleName.equals(SpInputType.HASHSET)) {
            edit.putStringSet(key, (HashSet) o);
        }
        if (simpleName.equals(SpInputType.INTEGER)) {
            edit.putInt(key, (Integer) o);
        }
        if (simpleName.equals(SpInputType.FLOAT)) {
            edit.putFloat(key, (Float) o);
        }
        if (simpleName.equals(SpInputType.LONG)) {
            edit.putLong(key, (Long) o);
        }
    }


}
