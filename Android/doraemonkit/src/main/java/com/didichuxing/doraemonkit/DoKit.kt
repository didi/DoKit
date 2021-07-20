package com.didichuxing.doraemonkit

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.View
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.core.DoKitViewLaunchMode
import com.didichuxing.doraemonkit.kit.core.McClientProcessor
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DokitExtInterceptor
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager
import kotlin.reflect.KClass

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：4/7/21-16:00
 * 描    述：DoKit 入口类
 * 修订历史：
 * ================================================
 */
public class DoKit private constructor() {

    companion object {
        const val TAG = "DoKit"

        lateinit var APPLICATION: Application

        /**
         * 主icon是否处于显示状态
         */
        @JvmStatic
        val isMainIconShow: Boolean
            get() = DoKitReal.isShow


        /**
         * 显示主icon
         */
        @JvmStatic
        fun show() {
            DoKitReal.show()
        }

        /**
         * 直接显示工具面板页面
         */
        @JvmStatic
        fun showToolPanel() {
            DoKitReal.showToolPanel()
        }

        /**
         * 直接隐藏工具面板
         */
        @JvmStatic
        fun hideToolPanel() {
            DoKitReal.hideToolPanel()
        }

        /**
         * 隐藏主icon
         */
        @JvmStatic
        fun hide() {
            DoKitReal.hide()
        }

        /**
         * 启动悬浮窗
         * @JvmStatic:允许使用java的静态方法的方式调用
         * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
         */
        @JvmStatic
        @JvmOverloads
        fun launchFloating(
            targetClass: Class<out AbsDokitView>,
            mode: DoKitViewLaunchMode = DoKitViewLaunchMode.SINGLE_INSTANCE,
            bundle: Bundle? = null
        ) {
            DoKitReal.launchFloating(targetClass, mode, bundle)
        }


        /**
         * 启动悬浮窗
         * @JvmStatic:允许使用java的静态方法的方式调用
         * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
         */
        @JvmStatic
        @JvmOverloads
        fun launchFloating(
            targetClass: KClass<out AbsDokitView>,
            mode: DoKitViewLaunchMode = DoKitViewLaunchMode.SINGLE_INSTANCE,
            bundle: Bundle? = null
        ) {
            launchFloating(targetClass.java, mode, bundle)
        }

        /**
         * 移除悬浮窗
         * @JvmStatic:允许使用java的静态方法的方式调用
         * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
         */
        @JvmStatic
        fun removeFloating(targetClass: Class<out AbsDokitView>) {
            DoKitReal.removeFloating(targetClass)
        }

        /**
         * 移除悬浮窗
         * @JvmStatic:允许使用java的静态方法的方式调用
         * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
         */
        @JvmStatic
        fun removeFloating(targetClass: KClass<out AbsDokitView>) {
            removeFloating(targetClass.java)
        }

        /**
         * 移除悬浮窗
         * @JvmStatic:允许使用java的静态方法的方式调用
         * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
         */
        @JvmStatic
        fun removeFloating(dokitView: AbsDokitView) {
            DoKitReal.removeFloating(dokitView)
        }


        /**
         * 启动全屏页面
         * @JvmStatic:允许使用java的静态方法的方式调用
         * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
         */
        @JvmStatic
        @JvmOverloads
        fun launchFullScreen(
            targetClass: Class<out BaseFragment>,
            context: Context? = null,
            bundle: Bundle? = null,
            isSystemFragment: Boolean = false
        ) {
            DoKitReal.launchFullScreen(targetClass, context, bundle, isSystemFragment)
        }

        /**
         * 启动全屏页面
         * @JvmStatic:允许使用java的静态方法的方式调用
         * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
         */
        @JvmStatic
        @JvmOverloads
        fun launchFullScreen(
            targetClass: KClass<out BaseFragment>,
            context: Context? = null,
            bundle: Bundle? = null,
            isSystemFragment: Boolean = false
        ) {
            launchFullScreen(targetClass.java, context, bundle, isSystemFragment)
        }


        @JvmStatic
        fun <T : AbsDokitView> getDoKitView(
            activity: Activity,
            clazz: Class<out T>
        ): T? {
            return DoKitReal.getDoKitView<T>(activity, clazz)
        }

        @JvmStatic
        fun <T : AbsDokitView> getDoKitView(
            activity: Activity,
            clazz: KClass<out T>
        ): T? {
            return getDoKitView(activity, clazz.java)
        }

        /**
         * 发送自定义一机多控事件
         */
        @JvmStatic
        fun sendCustomEvent(
            eventType: String,
            view: View? = null,
            param: Map<String, String>? = null
        ) {
            DoKitReal.sendCustomEvent(eventType, view, param)
        }
    }


    class Builder(private val app: Application) {
        private var productId: String = ""
        private var mapKits: LinkedHashMap<String, List<AbstractKit>> = linkedMapOf()
        private var listKits: List<AbstractKit> = arrayListOf()

        init {
            APPLICATION = app
        }

        fun productId(productId: String): Builder {
            this.productId = productId
            return this
        }

        /**
         * mapKits & listKits 二选一
         */
        fun customKits(mapKits: LinkedHashMap<String, List<AbstractKit>>): Builder {
            this.mapKits = mapKits
            return this
        }

        /**
         * mapKits & listKits 二选一
         */
        fun customKits(listKits: List<AbstractKit>): Builder {
            this.listKits = listKits
            return this
        }

        /**
         * H5任意门全局回调
         */
        fun webDoorCallback(callback: WebDoorManager.WebDoorCallback): Builder {
            DoKitReal.setWebDoorCallback(callback)
            return this
        }

        /**
         * 禁用app信息上传开关，该上传信息只为做DoKit接入量的统计，如果用户需要保护app隐私，可调用该方法进行禁用
         */
        fun disableUpload(): Builder {
            DoKitReal.disableUpload()
            return this
        }

        fun debug(debug: Boolean): Builder {
            DoKitReal.setDebug(debug)
            return this
        }

        /**
         * 是否显示主入口icon
         */
        fun awaysShowMainIcon(awaysShow: Boolean): Builder {
            DoKitReal.setAwaysShowMainIcon(awaysShow)
            return this
        }

        /**
         * 设置加密数据库密码
         */
        fun databasePass(map: Map<String, String>): Builder {
            DoKitReal.setDatabasePass(map)
            return this
        }

        /**
         * 设置文件管理助手http端口号
         */
        fun fileManagerHttpPort(port: Int): Builder {
            DoKitReal.setFileManagerHttpPort(port)
            return this
        }

        /**
         * 一机多控端口号
         */
        fun mcWSPort(port: Int): Builder {
            DoKitReal.setMCWSPort(port)
            return this
        }

        /**
         * 一机多控自定义拦截器
         */
        fun mcClientProcess(interceptor: McClientProcessor): Builder {
            DoKitReal.setMCIntercept(interceptor)
            return this
        }

        /**
         *设置dokit的性能监控全局回调
         */
        fun callBack(callback: DoKitCallBack): Builder {
            DoKitReal.setCallBack(callback)
            return this
        }


        /**
         * 设置扩展网络拦截器的代理对象
         */
        fun netExtInterceptor(extInterceptorProxy: DokitExtInterceptor.DokitExtInterceptorProxy): Builder {
            DoKitReal.setNetExtInterceptor(extInterceptorProxy)
            return this
        }


        fun build() {
            DoKitReal.install(app, mapKits, listKits, productId)
        }
    }
}