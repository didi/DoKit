package com.didichuxing.doraemonkit.kit.parameter.cpu;

import android.app.Activity;
import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.google.auto.service.AutoService;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AutoService(AbstractKit.class)
public class CpuKit extends AbstractKit {

    @Override
    public int getName() {
        return R.string.dk_frameinfo_cpu;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_cpu;
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
        return "dokit_sdk_performance_ck_cpu";
    }
}
