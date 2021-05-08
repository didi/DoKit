package com.didichuxing.doraemonkit.kit.health;

import android.content.Context;
import android.text.TextUtils;

import com.didichuxing.doraemonkit.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.aop.DokitPluginConfig;
import com.didichuxing.doraemonkit.constant.DoKitConstant;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.util.DoKitCommUtil;
import com.google.auto.service.AutoService;


/**
 * @author jintai
 * @desc: 一键体检kit
 */
@AutoService(AbstractKit.class)
public class HealthKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_kit_health;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_health;
    }


    @Override
    public void onClick(Context context) {
        if (!DokitPluginConfig.SWITCH_DOKIT_PLUGIN) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_close_tip));
            return;
        }

        if (!DokitPluginConfig.SWITCH_NETWORK) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_network_close_tip));
            return;
        }

        if (!DokitPluginConfig.SWITCH_METHOD) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_method_close_tip));
            return;
        }

        if (TextUtils.isEmpty(DoKitConstant.PRODUCT_ID)) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_platform_tip));
            return;
        }

        startUniversalActivity(HealthFragment.class, context, null,true);
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
        return "dokit_sdk_platform_ck_health";
    }
}
