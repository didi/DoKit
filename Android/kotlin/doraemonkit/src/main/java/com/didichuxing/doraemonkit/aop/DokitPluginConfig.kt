package com.didichuxing.doraemonkit.aop

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/27-18:24
 * 描    述：
 * 修订历史：
 * ================================================
 */
public object DokitPluginConfig {
    private const val TAG = "DokitPluginConfig"
    var STRATEGY_STACK = 0
    var STRATEGY_NORMAL = 1

    //字节码被关闭
    var STRATEGY_NULL = -1
    var SWITCH_DOKIT_PLUGIN = false
    var SWITCH_BIG_IMG = false
    var SWITCH_NETWORK = false
    var SWITCH_GPS = false
    var SWITCH_METHOD = false

    /**
     * 0 代表调用栈 1:代表普通模式 -1:字节码插装关闭
     */
    var VALUE_METHOD_STRATEGY = STRATEGY_NULL

    /**
     * 注入插件配置 动态注入到DoraemonKitReal#pluginConfig方法中
     */
    @JvmStatic
    fun inject(config: Map<*, *>) {
        //LogHelper.i(TAG, "map====>" + config);
        SWITCH_DOKIT_PLUGIN = config["dokitPluginSwitch"] as Boolean
        SWITCH_METHOD = config["methodSwitch"] as Boolean
        SWITCH_BIG_IMG = config["bigImgSwitch"] as Boolean
        SWITCH_NETWORK = config["networkSwitch"] as Boolean
        SWITCH_GPS = config["gpsSwitch"] as Boolean
        VALUE_METHOD_STRATEGY = config["methodStrategy"] as Int
    }
}