package com.didichuxing.doraemonkit.kit.network.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.didichuxing.doraemonkit.R;

import java.util.ArrayList;
import java.util.List;
public class NetWorkMainPagerAdapter extends PagerAdapter {
    private List<View> views;
    private List<String> tabs = new ArrayList<>();

    public NetWorkMainPagerAdapter(Context context, List<View> views) {
        this.views = views;
        tabs.add(context.getString(R.string.dk_net_monitor_title_summary));
        tabs.add(context.getString(R.string.dk_net_monitor_list));
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

}
