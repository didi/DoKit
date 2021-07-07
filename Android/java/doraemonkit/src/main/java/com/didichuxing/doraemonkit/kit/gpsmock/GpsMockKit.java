package com.didichuxing.doraemonkit.kit.gpsmock;

import android.content.Context;

import com.didichuxing.doraemonkit.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.aop.DokitPluginConfig;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.util.DoKitCommUtil;
import com.google.auto.service.AutoService;

import org.jetbrains.annotations.Nullable;

/**
 * Created by wanglikun on 2018/9/20.
 */
@AutoService(AbstractKit.class)
public class GpsMockKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_kit_gps_mock;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_gps_mock;
    }

    @Override
    public boolean onClickWithReturn(@Nullable Context context) {
        if (!DokitPluginConfig.SWITCH_DOKIT_PLUGIN) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_close_tip));
            return false;
        }

        if (!DokitPluginConfig.SWITCH_GPS) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_gps_close_tip));
            return false;
        }

        startUniversalActivity(GpsMockFragment.class, context, null, true);
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
        return "dokit_sdk_comm_ck_gps";
    }
}