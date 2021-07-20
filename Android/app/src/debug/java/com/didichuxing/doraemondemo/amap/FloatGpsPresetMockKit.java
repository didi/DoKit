package com.didichuxing.doraemondemo.amap;

import android.app.Activity;
import android.content.Context;

import com.blankj.utilcode.util.ToastUtils;
import com.didichuxing.doraemondemo.R;
import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.aop.DokitPluginConfig;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.util.DoKitCommUtil;

import org.jetbrains.annotations.NotNull;

/**
 * Created by changzuozhen on 2021年1月22日
 */
public class FloatGpsPresetMockKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_kit_gps_mock_preset;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_mock_location_preset;
    }

    @Override
    public boolean onClickWithReturn(@NotNull Activity activity) {
        if (!DokitPluginConfig.SWITCH_DOKIT_PLUGIN) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_close_tip));
            return false;
        }

        if (!DokitPluginConfig.SWITCH_GPS) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_gps_close_tip));
            return false;
        }

        DoKit.launchFloating(FloatGpsPresetMockKitView.class);
        return true;
    }

    @Override
    public void onAppInit(Context context) {

    }

    @Override
    public boolean isInnerKit() {
        return false;
    }


}