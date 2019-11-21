package com.didichuxing.doraemonkit.kit.largepicture;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.blockmonitor.BlockListAdapter;
import com.didichuxing.doraemonkit.kit.blockmonitor.bean.BlockInfo;
import com.didichuxing.doraemonkit.kit.blockmonitor.core.BlockMonitorManager;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.DividerItemDecoration;
import com.didichuxing.doraemonkit.ui.widget.titlebar.TitleBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @desc: 大图检测列表
 */

public class LargeImageListFragment extends BaseFragment {
    private static final String TAG = "LargeImageListFragment";

    private RecyclerView mLargeImageList;
    private LargeImageListAdapter mLargeImageListAdapter;
    private TitleBar mTitleBar;

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_large_img_list;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        load();
    }


    private void initView() {
        mLargeImageList = findViewById(R.id.block_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mLargeImageList.setLayoutManager(layoutManager);
        mLargeImageListAdapter = new LargeImageListAdapter(getContext());
        mLargeImageList.setAdapter(mLargeImageListAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.dk_divider));
        mLargeImageList.addItemDecoration(decoration);

        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
            @Override
            public void onLeftClick() {
                getActivity().onBackPressed();
            }

            @Override
            public void onRightClick() {

            }
        });
    }


    private void load() {
        List<LargeImageInfo> infos = new ArrayList<>();
        for (LargeImageInfo largeImageInfo : LargePictureManager.LARGE_IMAGE_INFO_MAP.values()) {
            infos.add(largeImageInfo);
        }
        mLargeImageListAdapter.setData(infos);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}