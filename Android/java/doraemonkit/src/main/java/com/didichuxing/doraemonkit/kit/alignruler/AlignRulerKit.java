package com.didichuxing.doraemonkit.kit.alignruler;

import android.content.Context;

import com.blankj.utilcode.util.ActivityUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.AlignRulerConfig;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.core.AbsDokitView;
import com.didichuxing.doraemonkit.kit.core.DokitIntent;
import com.didichuxing.doraemonkit.kit.core.DokitViewManager;

/**
 * Created by wanglikun on 2018/9/19.
 */

public class AlignRulerKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_kit_align_ruler;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_align_ruler;
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

        AlignRulerInfoDokitView alignRulerInfoDokitView = (AlignRulerInfoDokitView) DokitViewManager.getInstance().getDokitView(ActivityUtils.getTopActivity(), AlignRulerInfoDokitView.class.getSimpleName());
        alignRulerInfoDokitView.setCheckBoxListener(new AlignRulerInfoDokitView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean isChecked) {
                AlignRulerLineDokitView alignRulerLineDokitView = (AlignRulerLineDokitView) DokitViewManager.getInstance().getDokitView(ActivityUtils.getTopActivity(), AlignRulerLineDokitView.class.getSimpleName());
                alignRulerLineDokitView.getAlignInfoView().refreshInfo(isChecked);
            }
        });
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
