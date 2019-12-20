package com.didichuxing.doraemonkit;

import android.app.Application;

import com.didichuxing.doraemonkit.config.DokitConstant;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager;
import com.didichuxing.foundation.net.rpc.http.PlatformHttpHook;

import java.util.List;

/**
 * Created by jintai on 2018/6/22.
 */

public class DoraemonKitRpc {
    public static Application APPLICATION;
    private static final String TAG = "DoraemonKitRpc";

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
        DoraemonKit.APPLICATION = app;
        DoraemonKitReal.install(app, selfKits, productId);
        //平台端 http 拦截器注入
        PlatformHttpHook.installInterceptor();
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
    public static void setAwaysShowMianIcon(boolean awaysShow) {
        DokitConstant.AWAYS_SHOW_MAIN_ICON = awaysShow;
    }
}
