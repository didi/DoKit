package com.didichuxing.doraemonkit.weex.log;

import android.content.Context;

import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.loginfo.LogInfoManager;
import com.didichuxing.doraemonkit.kit.core.DokitIntent;
import com.didichuxing.doraemonkit.kit.core.DokitViewManager;
import com.didichuxing.doraemonkit.weex.R;
import com.google.auto.service.AutoService;

/**
 * @author haojianglong
 * @date 2019-06-11
 */
@AutoService(AbstractKit.class)
public class WeexLogKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_console_log_name;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_log_info;
    }

    @Override
    public void onClick(Context context) {
        DokitIntent intent = new DokitIntent(WeexLogInfoDokitView.class);
        intent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManager.getInstance().attach(intent);
        //开启日志服务
        LogInfoManager.getInstance().start();
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
        return "dokit_sdk_weex_ck_log";
    }
}
