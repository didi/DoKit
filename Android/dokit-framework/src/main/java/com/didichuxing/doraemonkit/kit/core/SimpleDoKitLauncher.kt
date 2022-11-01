package com.didichuxing.doraemonkit.kit.core

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.extension.tagName

/**
 * 悬浮窗和全屏启动器
 */
internal object SimpleDoKitLauncher {
    /**
     * @JvmStatic:允许使用java的静态方法的方式调用
     * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
     */
    fun launchFloating(
        targetClass: Class<out AbsDoKitView>,
        mode: DoKitViewLaunchMode = DoKitViewLaunchMode.SINGLE_INSTANCE,
        bundle: Bundle? = null
    ) {
        val doKitIntent = DoKitIntent(targetClass)
        doKitIntent.mode = mode
        doKitIntent.bundle = bundle
        DoKitViewManager.INSTANCE.attach(doKitIntent)
    }

    fun removeFloating(
        targetClass: Class<out AbsDoKitView>
    ) {
        DoKitViewManager.INSTANCE.detach(targetClass.tagName)
    }

    fun removeFloating(
        dokitView:  AbsDoKitView
    ) {
        DoKitViewManager.INSTANCE.detach(dokitView)
    }



    /**
     * @JvmStatic:允许使用java的静态方法的方式调用
     * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
     */
    fun launchFullScreen(
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




}
