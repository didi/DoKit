package com.didichuxing.doraemonkit.kit.network;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;


/**
 * @desc: 网络监测kit
 */
public class NetworkKit extends AbstractKit {

    @Override
    public int getCategory() {
        return Category.PERFORMANCE;
    }

    @Override
    public int getName() {
        return R.string.dk_kit_network_monitor;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_net_monitor;
    }


    @Override
    public void onClick(Context context) {
        startUniversalActivity(context,FragmentIndex.FRAGMENT_NETWORK_MONITOR);
    }

    @Override
    public void onAppInit(Context context) {

    }
}
