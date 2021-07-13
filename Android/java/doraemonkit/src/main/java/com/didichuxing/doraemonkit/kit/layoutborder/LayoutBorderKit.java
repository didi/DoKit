package com.didichuxing.doraemonkit.kit.layoutborder;

import android.app.Activity;
import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.LayoutBorderConfig;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.core.DokitIntent;
import com.didichuxing.doraemonkit.kit.core.DokitViewManager;
import com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter;
import com.google.auto.service.AutoService;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by wanglikun on 2019/1/7
 */
@AutoService(AbstractKit.class)
public class LayoutBorderKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_kit_layout_border;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_view_border;
    }

    @Override
    public boolean onClickWithReturn(@NotNull Activity activity) {

        SimpleDokitStarter.startFloating(LayoutLevelDokitView.class);
        LayoutBorderManager.getInstance().start();
        LayoutBorderConfig.setLayoutBorderOpen(true);
        LayoutBorderConfig.setLayoutLevelOpen(true);
        return true;
    }


    @Override
    public void onAppInit(Context context) {
        LayoutBorderConfig.setLayoutBorderOpen(false);
        LayoutBorderConfig.setLayoutLevelOpen(false);
    }

    @Override
    public boolean isInnerKit() {
        return true;
    }


    @Override
    public String innerKitId() {
        return "dokit_sdk_ui_ck_border";
    }
}