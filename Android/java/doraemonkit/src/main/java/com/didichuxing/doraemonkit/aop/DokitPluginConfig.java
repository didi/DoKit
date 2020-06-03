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
    private static final String TAG = "DokitPluginConfig";

    public static int STRATEGY_STACK = 0;
    public static int STRATEGY_NORMAL = 1;
    //字节码被关闭
    public static int STRATEGY_NULL = -1;
    public static boolean SWITCH_DOKIT_PLUGIN = false;
    public static boolean SWITCH_BIG_IMG = false;
    public static boolean SWITCH_NETWORK = false;
    public static boolean SWITCH_GPS = false;
    public static boolean SWITCH_METHOD = false;
    /**
     * 0 代表调用栈 1:代表普通模式 -1:字节码插装关闭
     */
    public static int VALUE_METHOD_STRATEGY = STRATEGY_NULL;


    /**
     * 注入插件配置 动态注入到DoraemonKitReal#pluginConfig方法中
     */
    public static void inject(Map config) {
        //LogHelper.i(TAG, "map====>" + config);
        SWITCH_DOKIT_PLUGIN = (boolean) config.get("dokitPluginSwitch");
        SWITCH_METHOD = (boolean) config.get("methodSwitch");
        SWITCH_BIG_IMG = (boolean) config.get("bigImgSwitch");
        SWITCH_NETWORK = (boolean) config.get("networkSwitch");
        SWITCH_GPS = (boolean) config.get("gpsSwitch");
        VALUE_METHOD_STRATEGY = (int) config.get("methodStrategy");
    }


}
