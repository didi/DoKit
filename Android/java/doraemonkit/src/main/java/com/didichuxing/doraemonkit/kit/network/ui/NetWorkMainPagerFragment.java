package com.didichuxing.doraemonkit.kit.network.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar;

import java.util.ArrayList;
import java.util.List;

/**
 * http 流量监控摘要页面
 */
public class NetWorkMainPagerFragment extends BaseFragment implements View.OnClickListener {
    private ViewPager mViewPager;
    private NetWorkSummaryView mSummaryView;
    private NetworkListView mNetworkListView;

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_net_main_pager;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        final TitleBar mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
            @Override
            public void onLeftClick() {
                getActivity().onBackPressed();
            }

            @Override
            public void onRightClick() {

            }
        });
        mViewPager = findViewById(R.id.vp_show);
        mSummaryView = new NetWorkSummaryView(getContext());
        mNetworkListView = new NetworkListView(getContext());
        mNetworkListView.registerNetworkListener();
        List<View> views = new ArrayList<>();
        views.add(mSummaryView);
        views.add(mNetworkListView);
        mViewPager.setAdapter(new NetWorkMainPagerAdapter(getContext(), views));

        final View tabSummary = findViewById(R.id.tab_summary);
        ((TextView) tabSummary.findViewById(R.id.tab_text)).setText(R.string.dk_net_monitor_title_summary);
        ((ImageView) tabSummary.findViewById(R.id.tab_icon)).setImageResource(R.drawable.dk_net_work_monitor_summary_selector);
        tabSummary.setSelected(true);
        tabSummary.setOnClickListener(this);

        final View tabList = findViewById(R.id.tab_list);
        ((TextView) tabList.findViewById(R.id.tab_text)).setText(R.string.dk_net_monitor_list);
        ((ImageView) tabList.findViewById(R.id.tab_icon)).setImageResource(R.drawable.dk_net_work_monitor_list_selector);
        tabList.setOnClickListener(this);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    tabSummary.setSelected(true);
                    tabList.setSelected(false);
                } else {
                    tabList.setSelected(true);
                    tabSummary.setSelected(false);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tab_summary) {
            mViewPager.setCurrentItem(0, true);
        } else if (id == R.id.tab_list) {
            mViewPager.setCurrentItem(1, true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mNetworkListView != null) {
            mNetworkListView.unRegisterNetworkListener();
        }
    }
}
