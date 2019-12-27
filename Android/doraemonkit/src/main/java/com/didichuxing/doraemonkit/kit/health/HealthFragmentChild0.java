package com.didichuxing.doraemonkit.kit.health;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.widget.titlebar.HomeTitleBar;
import com.didichuxing.doraemonkit.view.verticalviewpager.VerticalViewPager;

/**
 * 健康体检fragment
 */
public class HealthFragmentChild0 extends BaseFragment {


    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_health_child0;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null) {
            return;
        }

    }




}
