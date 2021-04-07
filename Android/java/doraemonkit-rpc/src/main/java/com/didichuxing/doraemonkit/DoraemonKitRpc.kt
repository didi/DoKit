package com.didichuxing.doraemonkit

import android.app.Application
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.MCInterceptor
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager
import com.didichuxing.foundation.net.rpc.http.PlatformHttpHook

/**
 * Created by jintai on 2018/6/22.
 */
@Deprecated("请使用DoKitRpc代替")
object DoraemonKitRpc {
    var APPLICATION: Application? = null

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
        DoKitRpc.APPLICATION = app
        try {
            DoKitReal.install(
                app, mapKits ?: linkedMapOf(), listKits
                    ?: mutableListOf(), productId ?: ""
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //平台端 http 拦截器注入
        PlatformHttpHook.installInterceptor()
    }

    @JvmStatic
    fun setWebDoorCallback(callback: WebDoorManager.WebDoorCallback?) {
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
    fun setAwaysShowMainIcon(awaysShow: Boolean) {
        DoKitConstant.AWAYS_SHOW_MAIN_ICON = awaysShow
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
    fun setMCIntercept(interceptor: MCInterceptor) {
        DoKitReal.setMCIntercept(interceptor)
    }

    @JvmStatic
    fun setMCWSPort(port: Int) {
        DoKitReal.setMCWSPort(port)
    }
}