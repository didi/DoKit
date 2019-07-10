package com.didichuxing.doraemonkit.weex.info;

import android.content.Context;

import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.weex.R;
import com.didichuxing.doraemonkit.weex.common.DKCommonActivity;

/**
 * @author haojianglong
 * @date 2019-06-11
 */
public class WeexInfoKit implements IKit {

    @Override
    public int getCategory() {
        return Category.BIZ;
    }

    @Override
    public int getName() {
        return R.string.dk_weex_info_name;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_sys_info;
    }

    @Override
    public void onClick(Context context) {
        DKCommonActivity.startWith(context, WeexInfoFragment.class);
    }

    @Override
    public void onAppInit(Context context) {

    }

}
