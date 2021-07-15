package com.didichuxing.doraemonkit.kit.core

import android.content.Context
import android.os.Bundle
import kotlin.reflect.KClass

/**
 * 悬浮窗和全屏启动器
 */
object SimpleDoKitStarter {
    /**
     * @JvmStatic:允许使用java的静态方法的方式调用
     * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
     */
    @JvmStatic
    @JvmOverloads
    fun startFloating(
        targetClass: Class<out AbsDokitView?>,
        bundle: Bundle? = null,
        mode: DoKitViewLaunchMode = DoKitViewLaunchMode.SINGLE_INSTANCE
    ) {

    }

    @JvmStatic
    fun removeFloating(
        targetClass: Class<out AbsDokitView?>
    ) {
    }


    /**
     * @JvmStatic:允许使用java的静态方法的方式调用
     * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
     */
    @JvmStatic
    @JvmOverloads
    fun startFloating(
        targetClass: KClass<out AbsDokitView>,
        bundle: Bundle? = null,
        mode: DoKitViewLaunchMode = DoKitViewLaunchMode.SINGLE_INSTANCE
    ) {
    }

    @JvmStatic
    fun removeFloating(
        targetClass: KClass<out AbsDokitView>
    ) {
    }


    /**
     * @JvmStatic:允许使用java的静态方法的方式调用
     * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
     */
    @JvmStatic
    @JvmOverloads
    fun startFullScreen(
        targetClass: Class<out BaseFragment?>,
        context: Context? = null,
        bundle: Bundle? = null,
        isSystemFragment: Boolean = false
    ) {

    }

    /**
     * @JvmStatic:允许使用java的静态方法的方式调用
     * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
     */
    @JvmStatic
    @JvmOverloads
    fun startFullScreen(
        targetClass: KClass<out BaseFragment>,
        context: Context? = null,
        bundle: Bundle? = null,
        isSystemFragment: Boolean = false
    ) {
    }


}