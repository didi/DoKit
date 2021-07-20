package com.didichuxing.doraemonkit.kit.sysinfo;

import android.app.Activity;
import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.google.auto.service.AutoService;

import org.jetbrains.annotations.NotNull;

/**
 * 设备、app信息
 * Created by zhangweida on 2018/6/22.
 */
@AutoService(AbstractKit.class)
public class SysInfoKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_kit_sysinfo;
    }


    @Override
    public int getIcon() {
        return R.mipmap.dk_sys_info;
    }

    @Override
    public boolean onClickWithReturn(@NotNull Activity activity) {
        startUniversalActivity(SysInfoFragment.class, activity, null, true);
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
        return "dokit_sdk_comm_ck_appinfo";
    }
}
