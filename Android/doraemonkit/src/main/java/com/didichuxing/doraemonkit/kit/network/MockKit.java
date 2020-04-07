package com.didichuxing.doraemonkit.kit.network;

import android.content.Context;
import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.DokitConstant;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;


/**
 * @author jintai
 * @desc: 网络监测kit
 */
public class MockKit extends AbstractKit {

    @Override
    public int getCategory() {
        return Category.PLATFORM;
    }

    @Override
    public int getName() {
        return R.string.dk_kit_network_mock;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_net_mock;
    }


    @Override
    public void onClick(Context context) {
        if (!DokitConstant.IS_HOOK) {
            ToastUtils.showShort("需要引入doraemonkit-plugin插件以后才能使用该功能");
            return;
        }

        if (TextUtils.isEmpty(DokitConstant.PRODUCT_ID)) {
            ToastUtils.showShort("需要到www.dokit.cn上注册pId才能使用该功能");
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
