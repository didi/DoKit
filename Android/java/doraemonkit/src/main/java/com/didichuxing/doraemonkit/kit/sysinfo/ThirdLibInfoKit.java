package com.didichuxing.doraemonkit.kit.sysinfo;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.google.auto.service.AutoService;

/**
 * 设备、app信息
 * Created by zhangweida on 2018/6/22.
 */
@AutoService(AbstractKit.class)
public class ThirdLibInfoKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_third_library_info;
    }


    @Override
    public int getIcon() {
        return R.mipmap.dk_icon_third_lib;
    }

    @Override
    public void onClick(Context context) {
        startUniversalActivity(ThirdLibInfoFragment.class, context, null,true);
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
        return "dokit_sdk_comm_ck_thirdlibinfo";
    }
}
