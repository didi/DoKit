package com.didichuxing.doraemonkit.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.ui.base.BaseFloatPage;
import com.didichuxing.doraemonkit.ui.kit.GroupKitAdapter;
import com.didichuxing.doraemonkit.ui.kit.KitItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglikun on 2018/10/23.
 */

public class KitFloatPage extends BaseFloatPage{
    private RecyclerView mGroupKitContainer;
    private GroupKitAdapter mGroupKitAdapter;

    @Override
    protected View onCreateView(Context context, ViewGroup view) {
        return LayoutInflater.from(context).inflate(R.layout.float_kit, null);
    }

    @Override
    protected void onViewCreated(View view) {
        super.onViewCreated(view);
        initView();
    }

    private void initView() {
        mGroupKitContainer = findViewById(R.id.group_kit_container);
        mGroupKitContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        mGroupKitAdapter = new GroupKitAdapter(getContext());
        List<List<KitItem>> kitLists = new ArrayList<>();
        List<KitItem> bizs = DoraemonKit.getKitItems(Category.BIZ);
        if (bizs != null && !bizs.isEmpty()) {
            kitLists.add(bizs);
        }
        kitLists.add(DoraemonKit.getKitItems(Category.TOOLS));
        kitLists.add(DoraemonKit.getKitItems(Category.PERFORMANCE));
        kitLists.add(DoraemonKit.getKitItems(Category.UI));
        kitLists.add(DoraemonKit.getKitItems(Category.CLOSE));
        mGroupKitAdapter.setData(kitLists);
        mGroupKitContainer.setAdapter(mGroupKitAdapter);
    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return true;
    }

    @Override
    public void onHomeKeyPress() {
        finish();
    }

    @Override
    public void onRecentAppKeyPress() {
        finish();
    }
}
