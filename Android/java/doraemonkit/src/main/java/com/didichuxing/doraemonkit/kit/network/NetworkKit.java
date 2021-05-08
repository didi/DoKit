package com.didichuxing.doraemonkit.kit.network;

import android.content.Context;

import com.didichuxing.doraemonkit.kit.network.ui.NetWorkMonitorFragment;
import com.didichuxing.doraemonkit.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.aop.DokitPluginConfig;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.util.DoKitCommUtil;
import com.google.auto.service.AutoService;


/**
 * @desc: 网络监测kit
 */
@AutoService(AbstractKit.class)
public class NetworkKit extends AbstractKit {

    @Override
    public int getName() {
        return R.string.dk_kit_network_monitor;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_net_monitor;
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

        startUniversalActivity(NetWorkMonitorFragment.class, context, null,true);
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
        return "dokit_sdk_performance_ck_network";
    }
}
