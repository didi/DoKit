package com.didichuxing.doraemonkit.weex.log;

import android.content.Context;

import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.ui.base.DokitIntent;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;
import com.didichuxing.doraemonkit.weex.R;

/**
 * @author haojianglong
 * @date 2019-06-11
 */
public class WeexLogKit extends AbstractKit {

    @Override
    public int getCategory() {
        return Category.WEEX;
    }

    @Override
    public int getName() {
        return R.string.dk_console_log_name;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_log_info;
    }

    @Override
    public void onClick(Context context) {
        DokitIntent intent = new DokitIntent(WeexLogInfoDokitView.class);
        intent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManager.getInstance().attach(intent);
    }

    @Override
    public void onAppInit(Context context) {

    }

}
