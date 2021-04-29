package com.didichuxing.doraemonkit.kit.weaknetwork;

import android.content.Context;

import com.didichuxing.doraemonkit.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.aop.DokitPluginConfig;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.util.DoKitCommUtil;
import com.google.auto.service.AutoService;

/**
 * 模拟弱网
 * <p>
 * Created by xiandanin on 2019/5/7 19:05
 */
@AutoService(AbstractKit.class)
public class WeakNetworkKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_kit_weak_network;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_weak_network;
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
        startUniversalActivity(context, FragmentIndex.FRAGMENT_WEAK_NETWORK);
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
        return "dokit_sdk_comm_ck_weaknetwork";
    }
}