package com.didichuxing.doraemonkit.kit.timecounter;

import android.app.Activity;
import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.google.auto.service.AutoService;

import org.jetbrains.annotations.NotNull;

/**
 * app启动、页面跳转的计时kit
 */
@AutoService(AbstractKit.class)
public class TimeCounterKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_kit_time_counter;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_time_counter;
    }

    @Override
    public boolean onClickWithReturn(@NotNull Activity activity) {
        startUniversalActivity(TimeCounterFragment.class, activity, null, true);
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
        return "dokit_sdk_performance_ck_open_coast";
    }
}
