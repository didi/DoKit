package com.didichuxing.doraemonkit.kit.core

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.extension.tagName
import kotlin.reflect.KClass

/**
 * 悬浮窗和全屏启动器
 */
object SimpleDokitStarter {
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
        val doKitIntent = DokitIntent(targetClass)
        doKitIntent.mode = mode
        doKitIntent.bundle = bundle
        DokitViewManager.instance.attach(doKitIntent)
    }

    @JvmStatic
    fun removeFloating(
        targetClass: Class<out AbsDokitView?>
    ) {
        DokitViewManager.instance.detach(targetClass.tagName)
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
        startFloating(targetClass.java, bundle, mode)
    }

    @JvmStatic
    fun removeFloating(
        targetClass: KClass<out AbsDokitView>
    ) {
        removeFloating(targetClass)
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
        val ctx = context ?: DoKit.APPLICATION.applicationContext
        ctx.startActivity(Intent(ctx, UniversalActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (isSystemFragment) {
                putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_SYSTEM)
                putExtra(BundleKey.SYSTEM_FRAGMENT_CLASS, targetClass)
            } else {
                putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_CUSTOM)
                putExtra(BundleKey.CUSTOM_FRAGMENT_CLASS, targetClass)
            }
            if (bundle != null) {
                putExtras(bundle)
            }
        })
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
        startFullScreen(targetClass.java, context, bundle, isSystemFragment)
    }


}