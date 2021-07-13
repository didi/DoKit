package com.didichuxing.doraemonkit.kit.alignruler;

import android.app.Activity;
import android.content.Context;

import com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter;
import com.didichuxing.doraemonkit.util.ActivityUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.AlignRulerConfig;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.core.DokitViewManager;
import com.google.auto.service.AutoService;

import org.jetbrains.annotations.NotNull;

/**
 * Created by wanglikun on 2018/9/19.
 */
@AutoService(AbstractKit.class)
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
    public boolean onClickWithReturn(@NotNull Activity activity) {

        SimpleDokitStarter.startFloating(AlignRulerMarkerDokitView.class);
        SimpleDokitStarter.startFloating(AlignRulerLineDokitView.class);
        SimpleDokitStarter.startFloating(AlignRulerInfoDokitView.class);


        AlignRulerInfoDokitView alignRulerInfoDokitView = (AlignRulerInfoDokitView) DokitViewManager.getInstance().getDoKitView(ActivityUtils.getTopActivity(), AlignRulerInfoDokitView.class.getCanonicalName());
        alignRulerInfoDokitView.setCheckBoxListener(new AlignRulerInfoDokitView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean isChecked) {
                AlignRulerLineDokitView alignRulerLineDokitView = (AlignRulerLineDokitView) DokitViewManager.getInstance().getDoKitView(ActivityUtils.getTopActivity(), AlignRulerLineDokitView.class.getCanonicalName());
                alignRulerLineDokitView.getAlignInfoView().refreshInfo(isChecked);
            }
        });
        AlignRulerConfig.setAlignRulerOpen(true);
        return true;
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
