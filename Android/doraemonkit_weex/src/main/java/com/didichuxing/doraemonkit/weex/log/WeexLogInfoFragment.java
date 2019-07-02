package com.didichuxing.doraemonkit.weex.log;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.base.FloatPageManager;
import com.didichuxing.doraemonkit.ui.base.PageIntent;

/**
 * @author haojianglong
 * @date 2019-06-25
 */
public class WeexLogInfoFragment extends BaseFragment implements WeexLogInfoPage.PageCloseCallback {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WeexLogInfoPage.setCallback(this);
        PageIntent intent = new PageIntent(WeexLogInfoPage.class);
        intent.mode = PageIntent.MODE_SINGLE_INSTANCE;
        FloatPageManager.getInstance().add(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FloatPageManager.getInstance().removeAll(WeexLogInfoPage.class);
    }

    @Override
    public void onPageDestroy() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

}
