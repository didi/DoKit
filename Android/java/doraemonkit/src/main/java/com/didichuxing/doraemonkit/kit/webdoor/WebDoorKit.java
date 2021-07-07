package com.didichuxing.doraemonkit.kit.webdoor;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.google.auto.service.AutoService;

import org.jetbrains.annotations.Nullable;

/**
 * Created by wanglikun on 2018/10/10.
 */
@AutoService(AbstractKit.class)
public class WebDoorKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_kit_web_door;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_web_door;
    }

    @Override
    public boolean onClickWithReturn(@Nullable Context context) {
        startUniversalActivity(WebDoorFragment.class, context, null, true);
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
        return "dokit_sdk_comm_ck_h5";
    }
}