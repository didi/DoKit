package com.didichuxing.doraemonkit.kit.viewcheck;

import android.app.Activity;
import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.core.SimpleDoKitStarter;
import com.google.auto.service.AutoService;

import org.jetbrains.annotations.NotNull;

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
    public boolean onClickWithReturn(@NotNull Activity activity) {

        SimpleDoKitStarter.startFloating(ViewCheckDokitView.class);
        SimpleDoKitStarter.startFloating(ViewCheckDrawDokitView.class);
        SimpleDoKitStarter.startFloating(ViewCheckInfoDokitView.class);

        return true;
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