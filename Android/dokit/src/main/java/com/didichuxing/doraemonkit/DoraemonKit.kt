package com.didichuxing.doraemonkit

import android.app.Application
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.McClientProcessor
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DokitExtInterceptor
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager.WebDoorCallback

/**
 * Created by jint on 2018/6/22.
 */
@Deprecated("请用DoKit来代替")
object DoraemonKit {
    @JvmField
    var APPLICATION: Application? = null
    private const val TAG = "DoraemonKit"


    @JvmStatic
    fun install(app: Application) {
        install(app, linkedMapOf(), mutableListOf(), "")
    }

    @JvmStatic
    fun install(app: Application, productId: String) {
        install(app, linkedMapOf(), mutableListOf(), productId)
    }


    @JvmStatic
    fun install(app: Application, mapKits: LinkedHashMap<String, List<AbstractKit>>) {
        install(app, mapKits, mutableListOf(), "")
    }

    @JvmStatic
    fun install(
        app: Application,
        mapKits: LinkedHashMap<String, List<AbstractKit>>,
        productId: String
    ) {
        install(app, mapKits, mutableListOf(), productId)
    }

    @JvmStatic
    fun install(app: Application, listKits: List<AbstractKit>) {
        install(app, linkedMapOf(), listKits, "")
    }

    @JvmStatic
    fun install(app: Application, listKits: List<AbstractKit>, productId: String) {
        install(app, linkedMapOf(), listKits, productId)
    }

    /**
     * @param app
     * @param mapKits  自定义kits  根据用户传进来的分组 建议优先选择mapKits 两者都传的话会选择mapKits
     * @param listKits  自定义kits 兼容原先老的api
     * @param productId Dokit平台端申请的productId
     */
    @JvmStatic
    private fun install(
        app: Application,
        mapKits: LinkedHashMap<String, List<AbstractKit>>? = linkedMapOf(),
        listKits: List<AbstractKit>? = mutableListOf(),
        productId: String? = ""
    ) {
        APPLICATION = app
        DoKitEnv.app = app
        try {
            DoKitReal.install(
                app, mapKits ?: linkedMapOf(), listKits
                    ?: mutableListOf(), productId ?: ""
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun setWebDoorCallback(callback: WebDoorCallback?) {
        DoKitReal.setWebDoorCallback(callback)
    }

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

    @JvmStatic
    fun hide() {
        DoKitReal.hide()
    }

    /**
     * 禁用app信息上传开关，该上传信息只为做DoKit接入量的统计，如果用户需要保护app隐私，可调用该方法进行禁用
     */
    @JvmStatic
    fun disableUpload() {
        DoKitReal.disableUpload()
    }

    @JvmStatic
    val isShow: Boolean
        get() = DoKitReal.isShow

    @JvmStatic
    fun setDebug(debug: Boolean) {
        DoKitReal.setDebug(debug)
    }

    /**
     * 是否显示主入口icon
     */
    @JvmStatic
    fun setAlwaysShowMainIcon(alwaysShow: Boolean) {
        DoKitReal.setAlwaysShowMainIcon(alwaysShow)
    }

    /**
     * 设置加密数据库密码
     */
    @JvmStatic
    fun setDatabasePass(map: Map<String, String>) {
        DoKitReal.setDatabasePass(map)
    }


    /**
     * 设置文件管理助手http端口号
     */
    @JvmStatic
    fun setFileManagerHttpPort(port: Int) {
        DoKitReal.setFileManagerHttpPort(port)
    }

    @JvmStatic
    fun setMCIntercept(interceptor: McClientProcessor) {
        DoKitReal.setMCIntercept(interceptor)
    }

    @JvmStatic
    fun setMCWSPort(port: Int) {
        DoKitReal.setMCWSPort(port)
    }

    /**
     *设置dokit的性能监控全局回调
     */
    @JvmStatic
    fun setCallBack(callback: DoKitCallBack) {
        DoKitReal.setCallBack(callback)
    }

    /**
     * 设置扩展网络拦截器的代理对象
     */
    @JvmStatic
    fun setNetExtInterceptor(extInterceptorProxy: DokitExtInterceptor.DokitExtInterceptorProxy) {
        DoKitReal.setNetExtInterceptor(extInterceptorProxy)
    }


}
