package com.didichuxing.doraemonkit.kit.weaknetwork;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;

/**
 * 模拟弱网
 *
 * @author denghaha
 * created 2019/5/7 19:05
 */
public class WeakNetworkKit extends AbstractKit {
    @Override
    public int getCategory() {
        return Category.TOOLS;
    }

    @Override
    public int getName() {
        return R.string.dk_kit_weak_network;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_weak_network;
    }

    @Override
    public void onClick(Context context) {
        startUniversalActivity(context,FragmentIndex.FRAGMENT_WEAK_NETWORK);
    }

    @Override
    public void onAppInit(Context context) {

    }
}