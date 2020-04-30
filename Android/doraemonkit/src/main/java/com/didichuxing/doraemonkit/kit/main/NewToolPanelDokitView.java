package com.didichuxing.doraemonkit.kit.main;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.constant.DokitConstant;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.core.AbsDokitView;
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams;
import com.didichuxing.doraemonkit.kit.core.UniversalActivity;
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jintai
 * Created by jintai on 2019/09/26.
 * 新的工具面板弹窗
 */
public class NewToolPanelDokitView extends AbsDokitView {
    private RecyclerView mGroupKitContainer;
    private GroupKitAdapter mGroupKitAdapter;

    @Override
    public void onCreate(Context context) {

    }

    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_tool_panel_new, view, false);
    }

    @Override
    public void onViewCreated(FrameLayout view) {
        initView();
    }

    private void initView() {
        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
            @Override
            public void onLeftClick() {
                detach();
            }

            @Override
            public void onRightClick() {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), UniversalActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_DOKIT_SETTING);
                    getActivity().startActivity(intent);
                }
            }
        });

        mGroupKitContainer = findViewById(R.id.group_kit_container);
        mGroupKitContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        mGroupKitAdapter = new GroupKitAdapter(getContext());
        List<List<KitItem>> kitLists = new ArrayList<>();
        //获取指定类型的工具
        List<KitItem> bizs = DokitConstant.getKitItems(Category.BIZ);
        if (bizs != null && !bizs.isEmpty()) {
            kitLists.add(bizs);
        }
        //平台工具
        if (DokitConstant.getKitItems(Category.PLATFORM) != null && !DokitConstant.getKitItems(Category.PLATFORM).isEmpty()) {
            kitLists.add(DokitConstant.getKitItems(Category.PLATFORM));
        }
        //常用工具
        kitLists.add(DokitConstant.getKitItems(Category.TOOLS));
        //weex
        if (DokitConstant.getKitItems(Category.WEEX) != null && !DokitConstant.getKitItems(Category.WEEX).isEmpty()) {
            kitLists.add(DokitConstant.getKitItems(Category.WEEX));
        }
        //性能监控
        kitLists.add(DokitConstant.getKitItems(Category.PERFORMANCE));
        //视觉工具
        kitLists.add(DokitConstant.getKitItems(Category.UI));

        kitLists.add(DokitConstant.getKitItems(Category.FLOAT_MODE));
        kitLists.add(DokitConstant.getKitItems(Category.CLOSE));
        kitLists.add(DokitConstant.getKitItems(Category.VERSION));
        mGroupKitAdapter.setData(kitLists);
        mGroupKitContainer.setAdapter(mGroupKitAdapter);
    }


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
