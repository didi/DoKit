package com.didichuxing.doraemonkit.aop;

import com.didichuxing.doraemonkit.util.LogHelper;

import java.util.Map;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/27-18:24
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DokitPluginConfig {
//    private static final String TAG = "DokitPluginConfig";

    public static boolean SWITCH_METHOD = true;
    public static int STRATEGY_STACK = 0;
    public static int STRATEGY_NORMAL = 1;
    /**
     * 0 代表调用栈 1:代表普通模式
     */
    public static int VALUE_METHOD_STRATEGY = STRATEGY_STACK;
    public static boolean SWITCH_DOKIT_PLUGIN = true;
    public static boolean SWITCH_BIG_IMG = true;
    public static boolean SWITCH_NETWORK = true;
    public static boolean SWITCH_MAP = true;

    /**
     * 注入插件配置 动态注入到DoraemonKitReal#pluginConfig方法中
     */
    public static void inject(Map config) {
        //LogHelper.i(TAG, "map====>" + config);
        SWITCH_DOKIT_PLUGIN = (boolean) config.get("dokitPluginSwitch");
        SWITCH_METHOD = (boolean) config.get("methodSwitch");
        SWITCH_BIG_IMG = (boolean) config.get("bigImgSwitch");
        SWITCH_NETWORK = (boolean) config.get("networkSwitch");
        SWITCH_MAP = (boolean) config.get("mapSwitch");
        VALUE_METHOD_STRATEGY = (int) config.get("methodStrategy");


    }
}
