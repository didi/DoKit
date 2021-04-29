package com.didichuxing.doraemonkit.kit.sysinfo;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.util.DoKitSystemUtil;
import com.google.auto.service.AutoService;

/**
 * 进入开发者选项
 * Created by jint on 2018/6/22.
 */
@AutoService(AbstractKit.class)
public class ServiceRunningKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_kit_service_running;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_kit_s_runing;
    }

    @Override
    public void onClick(Context context) {
        DoKitSystemUtil.startServiceRunningActivity(context);
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
