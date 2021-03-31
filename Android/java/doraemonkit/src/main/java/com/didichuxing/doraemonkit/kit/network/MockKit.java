package com.didichuxing.doraemonkit.kit.network;

import android.content.Context;
import android.text.TextUtils;

import com.didichuxing.doraemonkit.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.aop.DokitPluginConfig;
import com.didichuxing.doraemonkit.constant.DoKitConstant;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.util.DoKitCommUtil;


/**
 * @author jintai
 * @desc: 网络监测kit
 */
public class MockKit extends AbstractKit {



    @Override
    public int getName() {
        return R.string.dk_kit_network_mock;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_net_mock;
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

        if (TextUtils.isEmpty(DoKitConstant.PRODUCT_ID)) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_platform_tip));
            return;
        }

        startUniversalActivity(context, FragmentIndex.FRAGMENT_NETWORK_MOCK);
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
        return "dokit_sdk_platform_ck_mock";
    }
}
