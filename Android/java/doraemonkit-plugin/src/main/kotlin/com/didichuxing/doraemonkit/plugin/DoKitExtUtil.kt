package com.didichuxing.doraemonkit.plugin

import com.didichuxing.doraemonkit.plugin.extension.CommExt
import com.didichuxing.doraemonkit.plugin.extension.DoKitExt
import com.didichuxing.doraemonkit.plugin.extension.SlowMethodExt

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/24-14:58
 * 描    述：
 * 修订历史：
 * ================================================
 */
object DoKitExtUtil {
    //private var mApplicationId: String = ""

    /**
     * dokit 插件开关 字段权限必须为public 否则无法进行赋值
     */
    private var mDokitPluginSwitch = true
    private var mDokitLogSwitch = false

    /**
     * 默认函数调用为5级
     */
    public var mStackMethodLevel = 5

    /**
     * 慢函数默认关闭
     */
    public var mSlowMethodSwitch = false


    /**
     * 慢函数策略 默认为函数调用栈策略
     */
    public var mSlowMethodStrategy = SlowMethodExt.STRATEGY_STACK

    private val applications: MutableSet<String> = mutableSetOf()
    var commExt = CommExt()
        private set
    val slowMethodExt = SlowMethodExt()

    fun dokitPluginSwitchOpen(): Boolean {
        return mDokitPluginSwitch
    }


    fun dokitLogSwitchOpen(): Boolean {
        return mDokitLogSwitch
    }

    fun dokitSlowMethodSwitchOpen(): Boolean {
        return mSlowMethodSwitch
    }

    /**
     * 初始化
     *
     * @param dokitEx dokitExtension
     * @param appExtension   appExtension
     */
    fun init(dokitEx: DoKitExt, applicationId: String) {
        mDokitPluginSwitch = dokitEx.dokitPluginSwitch
        mDokitLogSwitch = dokitEx.dokitLogSwitch
        //设置普通的配置
        commExt = dokitEx.comm
        //slowMethodExt.strategy = dokitEx.slowMethod.strategy
        //slowMethodExt.methodSwitch = dokitEx.slowMethod.methodSwitch
        /**
         * ============慢函数普通策略的配置 start==========
         */
        slowMethodExt.normalMethod.thresholdTime = dokitEx.slowMethod.normalMethod.thresholdTime
        //设置慢函数普通策略插装包名
        slowMethodExt.normalMethod.packageNames.clear()
        for (packageName in dokitEx.slowMethod.normalMethod.packageNames) {
            slowMethodExt.normalMethod.packageNames.add(packageName)
        }
        //添加默认的包名
        if (applicationId.isNotEmpty()) {
            if (slowMethodExt.normalMethod.packageNames.isEmpty()) {
                slowMethodExt.normalMethod.packageNames.add(applicationId)
            }
        }


        //设置慢函数普通策略插装包名黑名单
        slowMethodExt.normalMethod.methodBlacklist.clear()
        for (blackStr in dokitEx.slowMethod.normalMethod.methodBlacklist) {
            slowMethodExt.normalMethod.methodBlacklist.add(blackStr)
        }
        /**
         * ============慢函数普通策略的配置end==========
         */
        /**
         * ============慢函数stack策略的配置 start==========
         */
        slowMethodExt.stackMethod.thresholdTime = dokitEx.slowMethod.stackMethod.thresholdTime
        slowMethodExt.stackMethod.enterMethods.clear()
        //添加默认的入口函数
        for (application in applications) {
            val attachBaseContextMethodName = "$application.attachBaseContext"
            val onCreateMethodName = "$application.onCreate"
            slowMethodExt.stackMethod.enterMethods.add(attachBaseContextMethodName)
            slowMethodExt.stackMethod.enterMethods.add(onCreateMethodName)
        }
        for (methodName in dokitEx.slowMethod.stackMethod.enterMethods) {
            slowMethodExt.stackMethod.enterMethods.add(methodName)
        }
        /**
         * ============慢函数stack策略的配置  end==========
         */

    }


    fun setApplications(applications: MutableSet<String>) {
        if (applications.isEmpty()) {
            return
        }
        this.applications.clear()
        for (application in applications) {
            this.applications.add(application)
        }
    }

    fun ignorePackageNames(className: String): Boolean {
        //命中白名单返回false
        for (packageName in whitePackageNames) {
            if (className.startsWith(packageName, true)) {
                return false
            }
        }

        //命中黑名单返回true
        for (packageName in blackPackageNames) {
            if (className.startsWith(packageName, true)) {
                return true
            }
        }

        return false
    }


    /**
     * 白名单
     */
    private val whitePackageNames = arrayOf(
            "com.didichuxing.doraemonkit.DoraemonKit",
            "com.didichuxing.doraemonkit.DoraemonKitReal"
    )


    /**
     * 黑名单
     */
    private val blackPackageNames = arrayOf(
            "com.didichuxing.doraemonkit.",
            "kotlin.",
            "java.",
            "android.",
            "androidx."
    )

    fun log(tag: String, className: String, methodName: String, access: Int, desc: String, signature: String, thresholdTime: Int) {
        if (mDokitLogSwitch) {
            println("$tag===matched====>  className===$className   methodName===$methodName   access===$access   desc===$desc   signature===$signature    thresholdTime===$thresholdTime")
        }
    }

}