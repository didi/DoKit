package com.didichuxing.doraemondemo.dokit;

import android.content.Context;

import com.didichuxing.doraemondemo.R;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.ui.base.DokitIntent;
import com.didichuxing.doraemonkit.ui.base.DokitViewManagerProxy;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-24-15:48
 * 描    述：kit demo
 * 修订历史：
 * ================================================
 */
public class DemoKit extends AbstractKit {
    @Override
    public int getCategory() {
        return Category.BIZ;
    }

    @Override
    public int getName() {
        return R.string.dk_kit_demo;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_sys_info;
    }

    @Override
    public void onClick(Context context) {
        DokitIntent dokitIntent = new DokitIntent(DemoDokitView.class);
        dokitIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManagerProxy.getInstance().attach(dokitIntent);
    }

    @Override
    public void onAppInit(Context context) {

    }
}
