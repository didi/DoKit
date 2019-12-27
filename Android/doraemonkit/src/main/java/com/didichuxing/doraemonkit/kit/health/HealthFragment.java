package com.didichuxing.doraemonkit.kit.health;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager;
import com.didichuxing.doraemonkit.kit.network.room_db.MockTemplateApiBean;
import com.didichuxing.doraemonkit.kit.network.ui.TemplateMockAdapter;
import com.didichuxing.doraemonkit.okgo.OkGo;
import com.didichuxing.doraemonkit.okgo.callback.StringCallback;
import com.didichuxing.doraemonkit.okgo.model.Response;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.widget.titlebar.HomeTitleBar;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.view.jsonviewer.JsonRecyclerView;
import com.didichuxing.doraemonkit.view.verticalviewpager.VerticalViewPager;

import org.json.JSONException;
import org.json.JSONObject;

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
