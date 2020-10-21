package com.didichuxing.doraemonkit.aop;

import com.didichuxing.doraemonkit.util.LogHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/27-18:24
 * 描    述：app三方库信息
 * 修订历史：
 * ================================================
 */
public class DokitThirdLibInfo {
    private static final String TAG = "DokitThirdLibInfo";
    public static Map<String, String> THIRD_LIB_INFOS = new HashMap<>();


    /**
     * 注入插件配置 动态注入到DoraemonKitReal#pluginConfig方法中
     */
    public static void inject(Map config) {
        LogHelper.i(TAG, "map====>" + config);
        THIRD_LIB_INFOS = config;
    }


}
