package com.didichuxing.doraemonkit.plugin.extension;

import org.gradle.api.Action;
import org.gradle.util.ConfigureUtil;

import java.util.ArrayList;
import java.util.List;

import groovy.lang.Closure;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/28-14:56
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class SlowMethodExt {
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

    public void strategy(int strategy) {
        this.strategy = strategy;
    }

    public void methodSwitch(boolean methodSwitch) {
        this.methodSwitch = methodSwitch;
    }


    public StackMethodExt stackMethod = new StackMethodExt();
    public NormalMethodExt normalMethod = new NormalMethodExt();

    public void stackMethod(Closure<StackMethodExt> closure) {

        ConfigureUtil.configure(closure, this.stackMethod);
    }

    public void normalMethod(Closure<NormalMethodExt> closure) {
        ConfigureUtil.configure(closure, this.normalMethod);
    }

    public static class StackMethodExt {
        /**
         * 默认值为5ms
         */
        public int thresholdTime = 5;
        public List<String> enterMethods = new ArrayList<>();

        public void thresholdTime(int thresholdTime) {
            this.thresholdTime = thresholdTime;
        }

        public void normalMethod(List<String> enterMethods) {
            this.enterMethods = enterMethods;
        }

        @Override
        public String toString() {
            return "StackMethodExt{" +
                    "thresholdTime=" + thresholdTime +
                    ", enterMethods=" + enterMethods +
                    '}';
        }
    }


    public static class NormalMethodExt {
        /**
         * 默认值为500ms
         */
        public int thresholdTime = 500;
        public List<String> packageNames = new ArrayList<>();
        public List<String> methodBlacklist = new ArrayList<>();

        public void thresholdTime(int thresholdTime) {
            this.thresholdTime = thresholdTime;
        }


        public void packageNames(List<String> packageNames) {
            this.packageNames = packageNames;
        }

        public void methodBlacklist(List<String> methodBlacklist) {
            this.methodBlacklist = methodBlacklist;
        }

        @Override
        public String toString() {
            return "NormalMethodExt{" +
                    "thresholdTime=" + thresholdTime +
                    ", packageNames=" + packageNames +
                    ", methodBlacklist=" + methodBlacklist +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SlowMethodExt{" +
                "strategy=" + strategy +
                ", methodSwitch=" + methodSwitch +
                ", stackMethod=" + stackMethod +
                ", normalMethod=" + normalMethod +
                '}';
    }
}
