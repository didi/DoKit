package com.didichuxing.doraemonkit.kit.blockmonitor;

import android.app.Activity;
import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.google.auto.service.AutoService;

import org.jetbrains.annotations.NotNull;


/**
 * @desc: 卡顿检测kit
 */
@AutoService(AbstractKit.class)
public class BlockMonitorKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_kit_block_monitor;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_block_monitor;
    }


    @Override
    public boolean onClickWithReturn(@NotNull Activity activity) {
        startUniversalActivity(BlockMonitorFragment.class, activity, null,true);
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
        return "dokit_sdk_performance_ck_block";
    }
}
