package com.didichuxing.doraemonkit.kit.network.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.network.bean.NetworkRecord;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc: 网络抓包详情页，显示request和response的详情
 */

public class NetworkDetailFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "NetworkDetailFragment";

    private ViewPager mViewPager;
    private View mDiverRequest;
    private View mDiverResponse;
    private TextView mTvRequest;
    private TextView mTvResponse;


    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_network_monitor_detail;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView() {
        mViewPager = findViewById(R.id.network_viewpager);
        mDiverRequest = findViewById(R.id.diver_request);
        mDiverResponse = findViewById(R.id.diver_response);
        mTvRequest = findViewById(R.id.tv_pager_request);
        mTvResponse = findViewById(R.id.tv_pager_response);
        mTvRequest.setSelected(true);
        mTvResponse.setSelected(false);
        mTvRequest.setOnClickListener(this);
        mTvResponse.setOnClickListener(this);

        List<NetworkDetailView> views = new ArrayList<>();
        Bundle bundle = getArguments();
        NetworkRecord record = (NetworkRecord) bundle.getSerializable(NetworkListView.KEY_RECORD);
        views.add(new NetworkDetailView(getContext()));
        views.add(new NetworkDetailView(getContext()));
        NetworkPagerAdapter adapter = new NetworkPagerAdapter(views, record);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mDiverRequest.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
                mDiverResponse.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
                mTvRequest.setSelected(position == 0);
                mTvResponse.setSelected(position == 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        TitleBar mTitleBar = findViewById(R.id.title_bar);
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
        return super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_pager_request) {
            mViewPager.setCurrentItem(0, true);
        } else if (v.getId() == R.id.tv_pager_response) {
            mViewPager.setCurrentItem(1, true);
        }
    }
}