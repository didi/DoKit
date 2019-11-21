package com.didichuxing.doraemonkit;

import android.app.Activity;
import android.app.Application;

import com.bumptech.glide.manager.LifecycleListener;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager;

import java.util.List;

/**
 * Created by zhangweida on 2018/6/22.
 */

public class DoraemonKit {

    public static void setWebDoorCallback(WebDoorManager.WebDoorCallback callback) {

    }

    public static void registerListener(LifecycleListener listener) {

    }

    public static void unRegisterListener(LifecycleListener listener) {

    }

    public static void install(final Application app) {
    }

    public static void install(final Application app, List<IKit> selfKits) {

    }

    public static void show(Activity activity) {

    }

    public static void hide() {
    }

    public static boolean isShow() {
        return false;
    }

    public static void disableUpload() {
    }


    public static void enableRequestPermissionSelf() {
    }

    public static void setDebug(boolean debug) {

    }

    public static Activity getCurrentResumedActivity() {
        return null;
    }
}