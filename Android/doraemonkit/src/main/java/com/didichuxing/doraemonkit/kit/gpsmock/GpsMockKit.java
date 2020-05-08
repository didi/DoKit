package com.didichuxing.doraemonkit.kit.gpsmock;

import android.content.Context;

import com.blankj.utilcode.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.aop.DokitPluginConfig;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.util.DokitUtil;

/**
 * Created by wanglikun on 2018/9/20.
 */

public class GpsMockKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_kit_gps_mock;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_gps_mock;
    }

    @Override
    public void onClick(Context context) {
        if (!DokitPluginConfig.SWITCH_DOKIT_PLUGIN) {
            ToastUtils.showShort(DokitUtil.getString(R.string.dk_plugin_close_tip));
            return;
        }

        if (!DokitPluginConfig.SWITCH_GPS) {
            ToastUtils.showShort(DokitUtil.getString(R.string.dk_plugin_gps_close_tip));
            return;
        }


        startUniversalActivity(context, FragmentIndex.FRAGMENT_GPS_MOCK);
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
        return "dokit_sdk_comm_ck_gps";
    }
}