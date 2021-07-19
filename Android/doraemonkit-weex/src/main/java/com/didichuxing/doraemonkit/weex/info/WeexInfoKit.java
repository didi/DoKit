package com.didichuxing.doraemonkit.weex.info;

import android.app.Activity;
import android.content.Context;

import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.parameter.cpu.CpuMainPageFragment;
import com.didichuxing.doraemonkit.weex.R;
import com.didichuxing.doraemonkit.weex.common.DKCommonActivity;
import com.google.auto.service.AutoService;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author haojianglong
 * @date 2019-06-11
 */
@AutoService(AbstractKit.class)
public class WeexInfoKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_weex_info_name;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_sys_info;
    }

    @Override
    public boolean onClickWithReturn(@NotNull Activity activity) {
        startUniversalActivity(CpuMainPageFragment.class, activity, null, true);
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
        return "dokit_sdk_weex_ck_info";
    }
}
