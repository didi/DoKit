package com.didichuxing.doraemonkit.kit.network;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;


/**
 * @author jintai
 * @desc: 网络监测kit
 */
public class MockKit extends AbstractKit {

    @Override
    public int getCategory() {
        return Category.PLATFORM;
    }

    @Override
    public int getName() {
        return R.string.dk_kit_network_mock;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_net_mock;
    }


    @Override
    public void onClick(Context context) {
        startUniversalActivity(context,FragmentIndex.FRAGMENT_NETWORK_MOCK);
    }

    @Override
    public void onAppInit(Context context) {

    }
}
