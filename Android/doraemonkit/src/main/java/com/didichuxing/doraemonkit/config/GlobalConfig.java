package com.didichuxing.doraemonkit.config;

import android.content.Context;

import com.didichuxing.doraemonkit.constant.SharedPrefsKey;
import com.didichuxing.doraemonkit.util.SharedPrefsUtil;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020-01-06-11:37
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class GlobalConfig {
    /**
     * 设置健康体检
     *
     * @param context
     * @param isRunning
     */
    public static void setAppHealth(Context context, boolean isRunning) {
        SharedPrefsUtil.putBoolean(context, SharedPrefsKey.APP_HEALTH, isRunning);
    }

    /**
     * 获得app 健康体检功能的本地状态
     *
     * @param context
     */
    public static boolean getAppHealth(Context context) {
        return SharedPrefsUtil.getBoolean(context, SharedPrefsKey.APP_HEALTH, false);
    }
}
