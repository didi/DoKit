package com.didichuxing.doraemonkit.kit.largepicture;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.widget.recyclerview.DividerItemDecoration;
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar;

import java.util.ArrayList;
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

    private double fileThreshold = PerformanceSpInfoConfig.getLargeImgFileThreshold(LargePictureManager.FILE_DEFAULT_THRESHOLD);
    private double memoryThreshold = PerformanceSpInfoConfig.getLargeImgMemoryThreshold(LargePictureManager.MEMORY_DEFAULT_THRESHOLD);


    private void load() {
        List<LargeImageInfo> imageInfos = new ArrayList<>();
        for (LargeImageInfo largeImageInfo : LargePictureManager.LARGE_IMAGE_INFO_MAP.values()) {
            if (largeImageInfo.getFileSize() < fileThreshold && largeImageInfo.getMemorySize() < memoryThreshold) {
                continue;
            }
            imageInfos.add(largeImageInfo);
        }
        mLargeImageListAdapter.setData(imageInfos);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}