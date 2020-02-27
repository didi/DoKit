package com.didichuxing.doraemonkit.kit.sysinfo;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.util.SystemUtil;

/**
 * 进入开发者选项
 * Created by jint on 2018/6/22.
 */

public class DevelopmentPageKit extends AbstractKit {
    @Override
    public int getCategory() {
        return Category.TOOLS;
    }

    @Override
    public int getName() {
        return R.string.dk_kit_develop;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_kit_devlop;
    }

    @Override
    public void onClick(Context context) {
        SystemUtil.startDevelopmentActivity(context);
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
        return "dokit_sdk_comm_ck_develop";
    }
}
