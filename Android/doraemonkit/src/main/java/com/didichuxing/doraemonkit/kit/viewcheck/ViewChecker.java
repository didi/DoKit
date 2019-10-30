package com.didichuxing.doraemonkit.kit.viewcheck;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.ViewCheckConfig;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.ui.base.DokitIntent;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;

/**
 * Created by wanglikun on 2018/11/20.
 */

public class ViewChecker implements IKit {
    @Override
    public int getCategory() {
        return Category.UI;
    }

    @Override
    public int getName() {
        return R.string.dk_kit_view_check;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_view_check;
    }

    @Override
    public void onClick(Context context) {
        DokitViewManager.getInstance().detachToolPanel();

        DokitIntent intent = new DokitIntent(ViewCheckDokitView.class);
        intent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManager.getInstance().attach(intent);

        intent = new DokitIntent(ViewCheckDrawDokitView.class);
        intent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManager.getInstance().attach(intent);

        intent = new DokitIntent(ViewCheckInfoDokitView.class);
        intent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManager.getInstance().attach(intent);


        ViewCheckConfig.setViewCheckOpen(context, true);
    }

    @Override
    public void onAppInit(Context context) {
        ViewCheckConfig.setViewCheckOpen(context, false);
    }
}