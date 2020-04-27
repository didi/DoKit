package com.didichuxing.doraemonkit.plugin;

import com.quinn.hunter.transform.RunVariant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jint on 07/10/2018.
 */
public class DokitExtension {

    public RunVariant runVariant = RunVariant.DEBUG;
    public boolean duplcatedClassSafeMode = false;
    /**
     * dokit 插件开关 字段权限必须为public 否则无法进行赋值
     */
    public boolean dokitPluginSwitch = true;

    public CommConfig comm = new CommConfig();

    public SlowMethodConfig slowMethod = new SlowMethodConfig();

    /**
     * 通用配置
     */
    public static class CommConfig {
        /**
         * 地图经纬度开关
         */
        public boolean mapSwitch = true;
        /**
         * 网络开关
         */
        public boolean networkSwitch = true;
        /**
         * 大图开关
         */
        public boolean bigImgSwitch = true;
    }

    /**
     * 慢函数配置
     */
    public static class SlowMethodConfig {
        public static final int STRATEGY_STACK = 0;
        public static final int STRATEGY_NORMAL = 1;
        /**
         * 0:打印函数调用栈  1:普通模式 运行时打印某个函数的耗时 全局业务代码函数插入
         */
        public int strategy = STRATEGY_STACK;
        /**
         * 函数功能开关
         */
        public boolean methodSwitch = true;

        public StackMethodConfig stackMethodConfig = new StackMethodConfig();
        public NormalMethodConfig normalMethodConfig = new NormalMethodConfig();

        public static class StackMethodConfig {
            /**
             * 默认值为5ms
             */
            public int thresholdTime = 5;
            public List<String> enterMethods = new ArrayList<>();
        }


        public static class NormalMethodConfig {
            /**
             * 默认值为500ms
             */
            public int thresholdTime = 500;
            public List<String> packageNames = new ArrayList<>();
            public List<String> methodBlacklist = new ArrayList<>();
        }
    }


}
