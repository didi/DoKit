package com.didichuxing.doraemonkit.plugin.extension

import groovy.lang.Closure
import org.gradle.util.ConfigureUtil
import java.util.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/28-14:56
 * 描    述：
 * 修订历史：
 * ================================================
 */

open class SlowMethodExt(
        //0:打印函数调用栈  1:普通模式 运行时打印某个函数的耗时 全局业务代码函数插入
        var strategy: Int = STRATEGY_STACK,
        //函数功能开关
        var methodSwitch: Boolean = true,
        //函数调用栈模式
        var stackMethod: StackMethodExt = StackMethodExt(),
        //普通模式
        var normalMethod: NormalMethodExt = NormalMethodExt()) {


    /**
     * 函数功能开关
     */
    fun strategy(strategy: Int) {
        this.strategy = strategy
    }

    fun methodSwitch(methodSwitch: Boolean) {
        this.methodSwitch = methodSwitch
    }

    fun stackMethod(closure: Closure<StackMethodExt?>?) {
        ConfigureUtil.configure(closure, stackMethod)
    }

    fun normalMethod(closure: Closure<NormalMethodExt?>?) {
        ConfigureUtil.configure(closure, normalMethod)
    }

    class StackMethodExt(
            //默认阈值为5ms
            var thresholdTime: Int = 5,
            //入口函集合
            var enterMethods: MutableList<String> = mutableListOf()) {
        /**
         * 默认值为5ms
         */
        fun thresholdTime(thresholdTime: Int) {
            this.thresholdTime = thresholdTime
        }

        fun normalMethod(enterMethods: MutableList<String>) {
            this.enterMethods = enterMethods
        }

        override fun toString(): String {
            return "StackMethodExt{" +
                    "thresholdTime=" + thresholdTime +
                    ", enterMethods=" + enterMethods +
                    '}'
        }
    }

    class NormalMethodExt(
            //默认阈值为500ms
            var thresholdTime: Int = 500,
            //普通函数的插装包名集合
            var packageNames: MutableList<String> = mutableListOf(),
            //插桩黑名单
            var methodBlacklist: MutableList<String> = mutableListOf()) {
        /**
         * 默认值为500ms
         */

        fun thresholdTime(thresholdTime: Int) {
            this.thresholdTime = thresholdTime
        }

        fun packageNames(packageNames: MutableList<String>) {
            this.packageNames = packageNames
        }

        fun methodBlacklist(methodBlacklist: MutableList<String>) {
            this.methodBlacklist = methodBlacklist
        }

        override fun toString(): String {
            return "NormalMethodExt{" +
                    "thresholdTime=" + thresholdTime +
                    ", packageNames=" + packageNames +
                    ", methodBlacklist=" + methodBlacklist +
                    '}'
        }
    }

    override fun toString(): String {
        return "SlowMethodExt{" +
                "strategy=" + strategy +
                ", methodSwitch=" + methodSwitch +
                ", stackMethod=" + stackMethod +
                ", normalMethod=" + normalMethod +
                '}'
    }

    companion object {
        const val STRATEGY_STACK = 0
        const val STRATEGY_NORMAL = 1
    }
}