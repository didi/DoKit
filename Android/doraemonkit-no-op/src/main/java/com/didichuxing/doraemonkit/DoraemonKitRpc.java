package com.didichuxing.doraemonkit;

import android.app.Application;

import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager;

import java.util.List;

/**
 * Created by zhangweida on 2018/6/22.
 */

public class DoraemonKitRpc {
    public static void install(Application app) {
    }

    public static void install(Application app, List<IKit> selfKits) {
    }

    /**
     * @param app
     * @param selfKits  自定义kits
     * @param productId Dokit平台端申请的productId
     */
    public static void install(final Application app, List<IKit> selfKits, String productId) {
    }


    public static void setWebDoorCallback(WebDoorManager.WebDoorCallback callback) {
    }


    public static void show() {

    }


    /**
     * 直接显示工具面板页面
     */
    public static void showToolPanel() {

    }


    public static void hide() {

    }

    /**
     * 禁用app信息上传开关，该上传信息只为做DoKit接入量的统计，如果用户需要保护app隐私，可调用该方法进行禁用
     */
    public static void disableUpload() {

    }

    public static boolean isShow() {
        return false;
    }

    public static void setDebug(boolean debug) {

    }

    /**
     * 是否显示主入口icon
     */
    public static void setAwaysShowMianIcon(boolean awaysShow) {

    }
}