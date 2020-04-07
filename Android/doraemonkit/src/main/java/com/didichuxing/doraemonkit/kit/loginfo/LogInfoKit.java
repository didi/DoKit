package com.didichuxing.doraemonkit.kit.loginfo;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.LogInfoConfig;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;

/**
 * Created by wanglikun on 2018/10/9.
 */

public class LogInfoKit extends AbstractKit {

    @Override
    public int getCategory() {
        return Category.TOOLS;
    }

    @Override
    public int getName() {
        return  R.string.dk_kit_log_info;
    }

    @Override
    public int getIcon() {
        return  R.drawable.dk_log_info;
    }

    @Override
    public void onClick(Context context) {
        startUniversalActivity(context,FragmentIndex.FRAGMENT_LOG_INFO_SETTING);

    }

    @Override
    public void onAppInit(Context context) {
        LogInfoConfig.setLogInfoOpen(context, false);
    }

    @Override
    public boolean isInnerKit() {
        return true;
    }

    @Override
    public String innerKitId() {
        return "dokit_sdk_comm_ck_log";
    }
}