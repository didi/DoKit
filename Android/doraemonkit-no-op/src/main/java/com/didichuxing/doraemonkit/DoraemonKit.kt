package com.didichuxing.doraemonkit

import android.app.Application
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.McClientProcessor
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DokitExtInterceptor
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager


/**
 * Created by jint on 2018/6/22.
 */
object DoraemonKit {
    @JvmStatic
    fun install(app: Application) {
    }

    @JvmStatic
    fun install(app: Application, productId: String) {
    }

    @JvmStatic
    fun install(app: Application, mapKits: LinkedHashMap<String, MutableList<AbstractKit>>) {
    }

    @JvmStatic
    fun install(
        app: Application,
        mapKits: LinkedHashMap<String, MutableList<AbstractKit>>,
        productId: String
    ) {
    }

    @JvmStatic
    fun install(app: Application, listKits: MutableList<AbstractKit>) {
    }

    @JvmStatic
    fun install(app: Application, listKits: MutableList<AbstractKit>, productId: String) {
    }

    /**
     * @param app
     * @param mapKits  自定义kits  根据用户传进来的分组 建议优先选择mapKits 两者都传的话会选择mapKits
     * @param listKits  自定义kits 兼容原先老的api
     * @param productId Dokit平台端申请的productId
     */


    @JvmStatic
    fun setWebDoorCallback(callback: WebDoorManager.WebDoorCallback?) {
    }

    @JvmStatic
    fun show() {
    }

    /**
     * 直接显示工具面板页面
     */
    @JvmStatic
    fun showToolPanel() {
    }

    /**
     * 直接隐藏工具面板
     */
    @JvmStatic
    fun hideToolPanel() {
    }

    @JvmStatic
    fun hide() {
    }

    /**
     * 禁用app信息上传开关，该上传信息只为做DoKit接入量的统计，如果用户需要保护app隐私，可调用该方法进行禁用
     */
    @JvmStatic
    fun disableUpload() {
    }

    @JvmStatic
    val isShow: Boolean
        get() = false

    @JvmStatic
    fun setDebug(debug: Boolean) {
    }

    /**
     * 是否显示主入口icon
     */
    @JvmStatic
    fun setAlwaysShowMainIcon(alwaysShow: Boolean) {
    }

    /**
     * 设置加密数据库密码
     */
    @JvmStatic
    fun setDatabasePass(map: Map<String, String>) {
    }

    /**
     * 设置文件管理助手http端口号
     */
    @JvmStatic
    fun setFileManagerHttpPort(port: Int) {
    }

    @JvmStatic
    fun setMCIntercept(interceptor: McClientProcessor) {
    }

    /**
     * 设置扩展网络拦截器的代理对象
     */
    @JvmStatic
    fun setNetExtInterceptor(extInterceptorProxy: DokitExtInterceptor.DokitExtInterceptorProxy) {
    }

    /**
     *设置dokit的性能监控全局回调
     */
    @JvmStatic
    fun setCallBack(callback: DoKitCallBack) {
    }
}