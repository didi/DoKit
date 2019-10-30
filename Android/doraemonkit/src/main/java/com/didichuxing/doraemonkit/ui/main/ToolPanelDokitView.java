package com.didichuxing.doraemonkit.ui.main;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.ui.base.AbsDokitView;
import com.didichuxing.doraemonkit.ui.base.DokitViewLayoutParams;
import com.didichuxing.doraemonkit.ui.kit.GroupKitAdapter;
import com.didichuxing.doraemonkit.ui.kit.KitItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jintai on 2019/09/26.
 * 工具面板弹窗
 */
public class ToolPanelDokitView extends AbsDokitView {
    private RecyclerView mGroupKitContainer;
    private GroupKitAdapter mGroupKitAdapter;

    @Override
    public void onCreate(Context context) {

    }

    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_kit, view, false);
    }

    @Override
    public void onViewCreated(FrameLayout view) {
        initView();
    }

    private void initView() {
        mGroupKitContainer = findViewById(R.id.group_kit_container);
        mGroupKitContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        mGroupKitAdapter = new GroupKitAdapter(getContext());
        List<List<KitItem>> kitLists = new ArrayList<>();
        //获取指定类型的工具
        List<KitItem> bizs = DoraemonKit.getKitItems(Category.BIZ);
        if (bizs != null && !bizs.isEmpty()) {
            kitLists.add(bizs);
        }
        kitLists.add(DoraemonKit.getKitItems(Category.TOOLS));
        if (DoraemonKit.getKitItems(Category.WEEX) != null && !DoraemonKit.getKitItems(Category.WEEX).isEmpty()) {
            kitLists.add(DoraemonKit.getKitItems(Category.WEEX));
        }
        kitLists.add(DoraemonKit.getKitItems(Category.PERFORMANCE));
        kitLists.add(DoraemonKit.getKitItems(Category.UI));
        kitLists.add(DoraemonKit.getKitItems(Category.FLOAT_MODE));
        kitLists.add(DoraemonKit.getKitItems(Category.CLOSE));
        kitLists.add(DoraemonKit.getKitItems(Category.VERSION));
        mGroupKitAdapter.setData(kitLists);
        mGroupKitContainer.setAdapter(mGroupKitAdapter);
    }

//    @Override
//    public void onNormalLayoutParamsCreated(FrameLayout.LayoutParams params) {
//        params.leftMargin = 0;
//        params.topMargin = 0;
//        params.width = FrameLayout.LayoutParams.MATCH_PARENT;
//        params.height = FrameLayout.LayoutParams.MATCH_PARENT;
//    }
//
//    @Override
//    public void onSystemLayoutParamsCreated(WindowManager.LayoutParams params) {
//        super.onSystemLayoutParamsCreated(params);
//    }

    @Override
    public void initDokitViewLayoutParams(DokitViewLayoutParams params) {
        params.x = 0;
        params.y = 0;
        params.width = DokitViewLayoutParams.MATCH_PARENT;
        params.height = DokitViewLayoutParams.MATCH_PARENT;
    }

    @Override
    public boolean onBackPressed() {
        detach();
        return true;
    }

    @Override
    public boolean shouldDealBackKey() {
        return true;
    }

    @Override
    public void onHomeKeyPress() {
        detach();
    }

    @Override
    public void onRecentAppKeyPress() {
        detach();
    }
}
