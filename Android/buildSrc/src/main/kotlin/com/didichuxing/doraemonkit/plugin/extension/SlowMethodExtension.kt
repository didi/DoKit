package com.didichuxing.doraemonkit.plugin.extension

import groovy.lang.Closure
import org.gradle.util.ConfigureUtil

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/28-14:56
 * 描    述：
 * 修订历史：
 * ================================================
 */

open class SlowMethodExtension(
    //0:打印函数调用栈  1:普通模式 运行时打印某个函数的耗时 全局业务代码函数插入
    @Deprecated("已弃用,请在项目根目录的gradle.properties中通过DOKIT_METHOD_STRATEGY=0|1 来控制")
    var strategy: Int = STRATEGY_STACK,
    //函数功能开关
    @Deprecated("已弃用,请在项目根目录的gradle.properties中通过DoKit_METHOD_SWITCH=true|false 来控制")
    var methodSwitch: Boolean = false,
    //函数调用栈模式
    var stackMethod: StackMethodExt = StackMethodExt(),
    //普通模式
    var normalMethod: NormalMethodExt = NormalMethodExt()
) {


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
        var enterMethods: MutableSet<String> = mutableSetOf(),
        //插桩黑名单
        var methodBlacklist: MutableSet<String> = mutableSetOf()
    ) {

        /**
         * 默认值为5ms
         */
        fun thresholdTime(thresholdTime: Int) {
            this.thresholdTime = thresholdTime
        }


        fun enterMethods(enterMethods: MutableSet<String>) {
            this.enterMethods = enterMethods
        }

        fun methodBlacklist(methodBlacklist: MutableSet<String>) {
            this.methodBlacklist = methodBlacklist
        }

        override fun toString(): String {
            return "StackMethodExt(thresholdTime=$thresholdTime, enterMethods=$enterMethods, methodBlacklist=$methodBlacklist)"
        }


    }

    class NormalMethodExt(
        //默认阈值为500ms
        var thresholdTime: Int = 500,
        //普通函数的插装包名集合
        var packageNames: MutableSet<String> = mutableSetOf(),
        //插桩黑名单
        var methodBlacklist: MutableSet<String> = mutableSetOf()
    ) {
        /**
         * 默认值为500ms
         */

        fun thresholdTime(thresholdTime: Int) {
            this.thresholdTime = thresholdTime
        }

        fun packageNames(packageNames: MutableSet<String>) {
            this.packageNames = packageNames
        }

        fun methodBlacklist(methodBlacklist: MutableSet<String>) {
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
