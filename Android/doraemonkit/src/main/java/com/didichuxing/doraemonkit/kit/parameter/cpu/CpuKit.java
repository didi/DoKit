package com.didichuxing.doraemonkit.kit.parameter.cpu;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;

public class CpuKit extends AbstractKit {

    @Override
    public int getName() {
        return R.string.dk_frameinfo_cpu;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_cpu;
    }

    @Override
    public void onClick(Context context) {
        startUniversalActivity(context, FragmentIndex.FRAGMENT_CPU);
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
