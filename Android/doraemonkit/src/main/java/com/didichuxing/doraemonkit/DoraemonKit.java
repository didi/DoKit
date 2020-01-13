package com.didichuxing.doraemonkit;

import android.app.Application;

import com.didichuxing.doraemonkit.constant.DokitConstant;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager;

import java.util.List;

/**
 * Created by zhangweida on 2018/6/22.
 */

public class DoraemonKit {
    public static Application APPLICATION;
    private static final String TAG = "DoraemonKit";

    public static void install(Application app) {
        install(app, null);
    }

    public static void install(Application app, List<IKit> selfKits) {
        install(app, selfKits, "");
    }

    /**
     * @param app
     * @param selfKits  自定义kits
     * @param productId Dokit平台端申请的productId
     */
    public static void install(final Application app, List<IKit> selfKits, String productId) {
        APPLICATION = app;
        try {
            DoraemonKitReal.install(app, selfKits, productId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void setWebDoorCallback(WebDoorManager.WebDoorCallback callback) {
        DoraemonKitReal.setWebDoorCallback(callback);
    }


    public static void show() {
        DoraemonKitReal.show();
    }


    /**
     * 直接显示工具面板页面
     */
    public static void showToolPanel() {
        DoraemonKitReal.showToolPanel();
    }


    public static void hide() {
        DoraemonKitReal.hide();
    }

    /**
     * 禁用app信息上传开关，该上传信息只为做DoKit接入量的统计，如果用户需要保护app隐私，可调用该方法进行禁用
     */
    public static void disableUpload() {
        DoraemonKitReal.disableUpload();
    }

    public static boolean isShow() {
        return DoraemonKitReal.isShow();
    }

    public static void setDebug(boolean debug) {
        DoraemonKitReal.setDebug(debug);
    }

    /**
     * 是否显示主入口icon
     */
    public static void setAwaysShowMainIcon(boolean awaysShow) {
        DokitConstant.AWAYS_SHOW_MAIN_ICON = awaysShow;
    }
}
