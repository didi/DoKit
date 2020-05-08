package com.didichuxing.doraemonkit.kit.health;

import android.content.Context;
import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.aop.DokitPluginConfig;
import com.didichuxing.doraemonkit.constant.DokitConstant;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.util.DokitUtil;


/**
 * @author jintai
 * @desc: 一键体检kit
 */
public class HealthKit extends AbstractKit {



    @Override
    public int getName() {
        return R.string.dk_kit_health;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_health;
    }


    @Override
    public void onClick(Context context) {
        if (!DokitPluginConfig.SWITCH_DOKIT_PLUGIN) {
            ToastUtils.showShort(DokitUtil.getString(R.string.dk_plugin_close_tip));
            return;
        }

        if (!DokitPluginConfig.SWITCH_NETWORK) {
            ToastUtils.showShort(DokitUtil.getString(R.string.dk_plugin_network_close_tip));
            return;
        }

        if (!DokitPluginConfig.SWITCH_METHOD) {
            ToastUtils.showShort(DokitUtil.getString(R.string.dk_plugin_method_close_tip));
            return;
        }

        if (TextUtils.isEmpty(DokitConstant.PRODUCT_ID)) {
            ToastUtils.showShort(DokitUtil.getString(R.string.dk_platform_tip));
            return;
        }
        startUniversalActivity(context, FragmentIndex.FRAGMENT_HEALTH);
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
