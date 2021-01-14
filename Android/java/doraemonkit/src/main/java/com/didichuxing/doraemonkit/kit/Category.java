package com.didichuxing.doraemonkit.kit;

/**
 * Created by jint on 2018/6/22.
 * 已弃用 保留是为了兼容以前的api
 */

@Deprecated
public interface Category {
    /**
     * 业务模块
     */
    int BIZ = 0;
    /**
     * 常用工具模块
     */
    int TOOLS = 1;

    /**
     * 性能监控模块
     */
    int PERFORMANCE = 2;

    /**
     * 视觉工具模块
     */
    int UI = 3;
    /**
     * 平台工具模块
     */
    int PLATFORM = 4;
    /**
     * 关闭
     */
    int CLOSE = 5;
    /**
     * Dokit版本号
     */
    int VERSION = 6;

    /**
     * 浮标模式 系统或者内置
     */
    int FLOAT_MODE = 7;
    /**
     * weex
     */
    int WEEX = 8;


    /**
     * default
     */
    int DEFAULT = 9;
}
