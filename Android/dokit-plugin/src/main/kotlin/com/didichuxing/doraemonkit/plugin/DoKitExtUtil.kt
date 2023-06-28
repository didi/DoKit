package com.didichuxing.doraemonkit.plugin

import com.didichuxing.doraemonkit.plugin.extension.DoKitExtension
import com.didichuxing.doraemonkit.plugin.extension.SlowMethodExtension
import com.didichuxing.doraemonkit.plugin.thirdlib.ThirdLibInfo

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

    var DOKIT_GPS_MOCK_INCLUDE = false

    /**
     * 三方库版本信息
     */

    val THIRD_LIB_INFOS = mutableListOf<ThirdLibInfo>()

    /**
     * dokit 插件开关 字段权限必须为public 否则无法进行赋值
     */
    var DOKIT_PLUGIN_SWITCH = true
    var DOKIT_LOG_SWITCH = false

    /**
     * 默认函数调用为5级
     */
    var STACK_METHOD_LEVEL = 5

    /**
     * 自定义webview全限定名
     */
    var WEBVIEW_CLASS_NAME: String = ""


    /**
     * 慢函数默认关闭
     */
    var SLOW_METHOD_SWITCH = false

    /**
     * 三方库信息开关
     */
    var THIRD_LIBINFO_SWITCH = true


    /**
     * 慢函数策略 默认为函数调用栈策略
     */
    var SLOW_METHOD_STRATEGY = SlowMethodExtension.STRATEGY_STACK

    private val applications: MutableSet<String> = mutableSetOf()

    /**
     * app的packageName
     */
    private var appPackageName: String = ""


    val slowMethodExt = SlowMethodExtension()


    fun dokitPluginSwitchOpen(): Boolean {
        return DOKIT_PLUGIN_SWITCH
    }


    fun dokitLogSwitchOpen(): Boolean {
        return DOKIT_LOG_SWITCH
    }

    fun dokitSlowMethodSwitchOpen(): Boolean {
        return SLOW_METHOD_SWITCH
    }

    /**
     * 初始化
     *
     * @param dokitEx dokitExtension
     * @param appExtension   appExtension
     */
    fun init(dokitEx: DoKitExtension) {
        //设置普通的配置
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
        if (appPackageName.isNotEmpty()) {
            if (slowMethodExt.normalMethod.packageNames.isEmpty()) {
                slowMethodExt.normalMethod.packageNames.add(appPackageName)
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

        //设置慢函数调用栈策略插装包名黑名单
        slowMethodExt.stackMethod.methodBlacklist.clear()
        for (blackStr in dokitEx.slowMethod.stackMethod.methodBlacklist) {
            slowMethodExt.stackMethod.methodBlacklist.add(blackStr)
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

    /**
     * 设置packageName
     */
    fun setAppPackageName(packageName: String) {
        appPackageName = packageName
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
        "com.didichuxing.doraemonkit.DoKit",
        "com.didichuxing.doraemonkit.DoKitReal"

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

    fun log(
        tag: String,
        className: String,
        methodName: String,
        access: Int,
        desc: String,
        signature: String,
        thresholdTime: Int
    ) {
        if (DOKIT_LOG_SWITCH) {
            println("$tag===matched====>  className===$className   methodName===$methodName   access===$access   desc===$desc   signature===$signature    thresholdTime===$thresholdTime")
        }
    }

}
