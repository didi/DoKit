package com.didichuxing.doraemonkit.kit.viewcheck;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.core.DokitIntent;
import com.didichuxing.doraemonkit.kit.core.DokitViewManager;
import com.google.auto.service.AutoService;

/**
 * Created by wanglikun on 2018/11/20.
 */
@AutoService(AbstractKit.class)
public class ViewCheckerKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_kit_view_check;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_view_check;
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
    }

    @Override
    public void onAppInit(Context context) {
    }

    @Override
    public boolean isInnerKit() {
        return true;
    }

    @Override
    public String innerKitId() {
        return "dokit_sdk_ui_ck_widget";
    }
}