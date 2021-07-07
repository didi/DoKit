package com.didichuxing.doraemonkit.kit.loginfo;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.LogInfoConfig;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter;
import com.google.auto.service.AutoService;

/**
 * Created by wanglikun on 2018/10/9.
 */
@AutoService(AbstractKit.class)
public class LogInfoKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_kit_log_info;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_log_info;
    }

    @Override
    public boolean onClickWithReturn(Context context) {
        SimpleDokitStarter.startFloating(LogInfoDokitView.class);
        //开启日志服务
        LogInfoManager.getInstance().start();
        return true;
    }

    @Override
    public void onAppInit(Context context) {
        LogInfoConfig.setLogInfoOpen(false);
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