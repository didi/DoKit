package com.didichuxing.doraemonkit.config

/**
 * @author will
 * 将性能检测配置信息保存在内存中 不需要持久化保存
 */
object DokitMemoryConfig {
    //帧率检测全局开关
    var FPS_STATUS = false

    //CPU检测全局开关
    var CPU_STATUS = false

    //RAM检测全局开关
    var RAM_STATUS = false
    var NETWORK_STATUS = false

    //日志全局开关
    var LOG_STATUS = false //大图检测全局开关
    //public static boolean LARGE_IMG_STATUS = false;
}