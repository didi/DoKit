package com.didichuxing.doraemonkit.kit.blockmonitor;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.blockmonitor.bean.BlockInfo;
import com.didichuxing.doraemonkit.kit.blockmonitor.core.BlockMonitorManager;
import com.didichuxing.doraemonkit.kit.blockmonitor.core.OnBlockInfoUpdateListener;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.DividerItemDecoration;
import com.didichuxing.doraemonkit.ui.widget.titlebar.TitleBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @desc: 卡顿检测日志列表
 */

public class BlockListFragment extends BaseFragment implements OnBlockInfoUpdateListener {
    private static final String TAG = "BlockMonitorIndexFragment";

    private RecyclerView mBlockList;
    private BlockListAdapter mBlockListAdapter;
    private TextView mLogDetail;
    private TitleBar mTitleBar;

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_block_list;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        load();
        BlockMonitorManager.getInstance().setOnBlockInfoUpdateListener(this);
    }


    private void initView() {
        mBlockList = findViewById(R.id.block_list);
        mLogDetail = findViewById(R.id.tx_block_detail);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mBlockList.setLayoutManager(layoutManager);
        mBlockListAdapter = new BlockListAdapter(getContext());
        mBlockList.setAdapter(mBlockListAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.dk_divider));
        mBlockList.addItemDecoration(decoration);
        mBlockListAdapter.setOnItemClickListener(new BlockListAdapter.OnItemClickListener() {
            @Override
            public void onClick(BlockInfo info) {
                mLogDetail.setText(info.toString());
                mLogDetail.setVisibility(View.VISIBLE);
                mBlockList.setVisibility(View.GONE);
                mTitleBar.setTitle(R.string.dk_kit_block_monitor_detail);
            }
        });
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

    @Override
    public boolean onBackPressed() {
        if (mLogDetail.getVisibility() == View.VISIBLE) {
            mLogDetail.setVisibility(View.GONE);
            mBlockList.setVisibility(View.VISIBLE);
            mTitleBar.setTitle(R.string.dk_kit_block_monitor_list);
            return true;
        }
        return super.onBackPressed();
    }


    private void load() {
        List<BlockInfo> infos = new ArrayList<>(BlockMonitorManager.getInstance().getBlockInfoList());
        Collections.sort(infos, new Comparator<BlockInfo>() {
            @Override
            public int compare(BlockInfo lhs, BlockInfo rhs) {
                return Long.valueOf(rhs.time)
                        .compareTo(lhs.time);
            }
        });
        mBlockListAdapter.setData(infos);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BlockMonitorManager.getInstance().setOnBlockInfoUpdateListener(null);
    }

    @Override
    public void onBlockInfoUpdate(BlockInfo blockInfo) {
        mBlockListAdapter.append(blockInfo, 0);
    }
}