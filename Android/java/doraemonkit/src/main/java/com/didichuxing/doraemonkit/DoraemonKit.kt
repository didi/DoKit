package com.didichuxing.doraemonkit

import android.app.Application
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager.WebDoorCallback

/**
 * Created by jint on 2018/6/22.
 */
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
    fun install(app: Application, mapKits: LinkedHashMap<String, MutableList<AbstractKit>>, productId: String) {
        install(app, mapKits, mutableListOf(), productId)
    }

    @JvmStatic
    fun install(app: Application, listKits: MutableList<AbstractKit>, productId: String) {
        install(app, linkedMapOf(), listKits, productId)
    }

    /**
     * @param app
     * @param mapKits  自定义kits  根据用户传进来的分组 建议优先选择mapKits 两者都传的话会选择mapKits
     * @param listKits  自定义kits 兼容原先老的api
     * @param productId Dokit平台端申请的productId
     */
    @JvmStatic
    private fun install(app: Application, mapKits: LinkedHashMap<String, MutableList<AbstractKit>>? = linkedMapOf(), listKits: MutableList<AbstractKit>? = mutableListOf(), productId: String? = "") {
        APPLICATION = app
        try {
            DoraemonKitReal.install(app, mapKits ?: linkedMapOf(), listKits
                    ?: mutableListOf(), productId ?: "")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun setWebDoorCallback(callback: WebDoorCallback?) {
        DoraemonKitReal.setWebDoorCallback(callback)
    }

    @JvmStatic
    fun show() {
        DoraemonKitReal.show()
    }

    /**
     * 直接显示工具面板页面
     */
    @JvmStatic
    fun showToolPanel() {
        DoraemonKitReal.showToolPanel()
    }

    /**
     * 直接隐藏工具面板
     */
    @JvmStatic
    fun hideToolPanel() {
        DoraemonKitReal.hideToolPanel()
    }

    @JvmStatic
    fun hide() {
        DoraemonKitReal.hide()
    }

    /**
     * 禁用app信息上传开关，该上传信息只为做DoKit接入量的统计，如果用户需要保护app隐私，可调用该方法进行禁用
     */
    @JvmStatic
    fun disableUpload() {
        DoraemonKitReal.disableUpload()
    }

    @JvmStatic
    val isShow: Boolean
        get() = DoraemonKitReal.isShow

    @JvmStatic
    fun setDebug(debug: Boolean) {
        DoraemonKitReal.setDebug(debug)
    }

    /**
     * 是否显示主入口icon
     */
    @JvmStatic
    fun setAwaysShowMainIcon(awaysShow: Boolean) {
        DokitConstant.AWAYS_SHOW_MAIN_ICON = awaysShow
    }

    /**
     * 设置加密数据库密码
     */
    @JvmStatic
    fun setDatabasePass(map: Map<String, String>) {
        DoraemonKitReal.setDatabasePass(map)
    }
}