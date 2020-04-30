package com.didichuxing.doraemonkit.kit.health;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import android.view.View;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar;
import com.didichuxing.doraemonkit.widget.verticalviewpager.VerticalViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 健康体检fragment
 */
public class HealthFragment extends BaseFragment {
    VerticalViewPager mVerticalViewPager;
    HomeTitleBar mHomeTitleBar;
    List<Fragment> mFragments = new ArrayList<>();
    FragmentPagerAdapter mFragmentPagerAdapter;

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_health;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null) {
            return;
        }
        initView();
    }

    private void initView() {
        mFragments.clear();
        mFragments.add(new HealthFragmentChild0());
        mFragments.add(new HealthFragmentChild1());
        mHomeTitleBar = findViewById(R.id.title_bar);
        mHomeTitleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                finish();
            }
        });
        mVerticalViewPager = findViewById(R.id.view_pager);
        mFragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };
        mVerticalViewPager.setAdapter(mFragmentPagerAdapter);
    }

    /**
     * 滑动到顶部
     */
    protected void scroll2theTop() {
        if (mVerticalViewPager != null && mFragmentPagerAdapter != null) {
            mVerticalViewPager.setCurrentItem(0, true);
        }
    }


}
