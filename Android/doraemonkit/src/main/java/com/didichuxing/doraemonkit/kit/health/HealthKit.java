package com.didichuxing.doraemonkit.kit.health;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;


/**
 * @author jintai
 * @desc: 一键体检kit
 */
public class HealthKit extends AbstractKit {

    @Override
    public int getCategory() {
        return Category.PLATFORM;
    }

    @Override
    public int getName() {
        return R.string.dk_kit_health;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_health;
    }


    @Override
    public void onClick(Context context) {
        startUniversalActivity(context, FragmentIndex.FRAGMENT_HEALTH);
    }

    @Override
    public void onAppInit(Context context) {

    }
}
