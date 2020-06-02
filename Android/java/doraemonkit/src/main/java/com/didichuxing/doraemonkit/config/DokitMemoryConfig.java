package com.didichuxing.doraemonkit.config;

/**
 * Created by jintai on 2019/10/10.
 * 将性能检测配置信息保存在内存中 不需要持久化保存
 */

public class DokitMemoryConfig {
    //帧率检测全局开关
    public static boolean FPS_STATUS = false;
    //CPU检测全局开关
    public static boolean CPU_STATUS = false;
    //RAM检测全局开关
    public static boolean RAM_STATUS = false;
    //流量检测全局开关
    public static boolean NETWORK_STATUS = false;

    //日志全局开关
    public static boolean LOG_STATUS = false;
    //大图检测全局开关
    //public static boolean LARGE_IMG_STATUS = false;
}
