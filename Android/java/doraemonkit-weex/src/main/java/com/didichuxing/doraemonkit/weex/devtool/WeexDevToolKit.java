package com.didichuxing.doraemonkit.weex.devtool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.weex.R;
import com.google.auto.service.AutoService;

import org.jetbrains.annotations.NotNull;

/**
 * @author haojianglong
 * @date 2019-06-11
 */
@AutoService(AbstractKit.class)
public class WeexDevToolKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_dev_tool_name;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_custom;
    }

    @Override
    public boolean onClickWithReturn(@NotNull Activity activity) {
        Intent intent = new Intent(activity, DevToolActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
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
        return "dokit_sdk_weex_ck_devtool";
    }
}
