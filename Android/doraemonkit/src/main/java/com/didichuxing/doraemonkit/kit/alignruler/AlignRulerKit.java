package com.didichuxing.doraemonkit.kit.alignruler;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.AlignRulerConfig;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.core.DokitIntent;
import com.didichuxing.doraemonkit.kit.core.DokitViewManager;

/**
 * Created by wanglikun on 2018/9/19.
 */

public class AlignRulerKit extends AbstractKit {
    @Override
    public int getCategory() {
        return Category.UI;
    }

    @Override
    public int getName() {
        return R.string.dk_kit_align_ruler;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_align_ruler;
    }

    @Override
    public void onClick(Context context) {
        DokitViewManager.getInstance().detachToolPanel();

        DokitIntent pageIntent = new DokitIntent(AlignRulerMarkerDokitView.class);
        pageIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManager.getInstance().attach(pageIntent);

        pageIntent = new DokitIntent(AlignRulerLineDokitView.class);
        pageIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManager.getInstance().attach(pageIntent);

        pageIntent = new DokitIntent(AlignRulerInfoDokitView.class);
        pageIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManager.getInstance().attach(pageIntent);


        AlignRulerConfig.setAlignRulerOpen(true);
    }

    @Override
    public void onAppInit(Context context) {
        AlignRulerConfig.setAlignRulerOpen(false);
    }

    @Override
    public boolean isInnerKit() {
        return true;
    }

    @Override
    public String innerKitId() {
        return "dokit_sdk_ui_ck_aligin_scaleplate";
    }
}
