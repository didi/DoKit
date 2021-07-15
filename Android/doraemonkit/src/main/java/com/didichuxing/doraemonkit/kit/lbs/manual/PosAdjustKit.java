package com.didichuxing.doraemonkit.kit.lbs.manual;

import android.app.Activity;
import android.content.Context;

import com.didichuxing.doraemonkit.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.aop.DokitPluginConfig;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.core.SimpleDoKitStarter;
import com.didichuxing.doraemonkit.util.DoKitCommUtil;
import com.google.auto.service.AutoService;

import org.jetbrains.annotations.NotNull;

/**
 * Created by changzuozhen on 2021年1月22日
 */
@AutoService(AbstractKit.class)
public class PosAdjustKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_kit_gps_mock_manual;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_mock_location;
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

        SimpleDoKitStarter.startFloating(PosAdjustKitView.class);
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
        return "dokit_sdk_lbs_ck_pos_adjust";
    }
}