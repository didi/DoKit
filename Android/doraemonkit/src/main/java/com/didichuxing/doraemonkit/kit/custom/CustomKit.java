package com.didichuxing.doraemonkit.kit.custom;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;

/**
 * Created by yangmenglin on 2019/4/24
 */
public class CustomKit extends AbstractKit {
    @Override
    public int getCategory() {
        return Category.PERFORMANCE;
    }

    @Override
    public int getName() {
        return R.string.dk_frameinfo_custom;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_custom;
    }

    @Override
    public void onClick(Context context) {
        startUniversalActivity(context,FragmentIndex.FRAGMENT_CUSTOM);
    }

    @Override
    public void onAppInit(Context context) {

    }
}
