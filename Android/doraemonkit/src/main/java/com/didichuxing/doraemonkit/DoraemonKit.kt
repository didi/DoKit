package com.didichuxing.doraemonkit

import android.app.Application
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.group.AbsDokitGroup
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager.WebDoorCallback

/**
 * Created by zhangweida on 2018/6/22.
 */
object DoraemonKit {
    @JvmField
    var APPLICATION: Application? = null
    private const val TAG = "DoraemonKit"

    /**
     * 请使用init 初始化
     *
     * @param app
     */
    @Deprecated("请使用init初始化", ReplaceWith("install(app, null)", "com.didichuxing.doraemonkit.DoraemonKit.install"))
    fun install(app: Application) {
        install(app, null)
    }


    @Deprecated("请使用init初始化", ReplaceWith("install(app, selfKits, \"\")", "com.didichuxing.doraemonkit.DoraemonKit.install"))
    fun install(app: Application, selfKits: List<AbstractKit>?) {
        install(app, selfKits, "")
    }


    /**
     * @param app
     * @param selfKits  自定义kits
     * @param productId Dokit平台端申请的productId
     */
    @JvmStatic
    @Deprecated("请使用init初始化")
    fun install(app: Application, selfKits: List<AbstractKit>?, productId: String?) {
        APPLICATION = app
        try {
            DoraemonKitReal.install(app, selfKits, productId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * in的初始化方式
     */
    fun init(app: Application, selfKits: LinkedHashMap<AbsDokitGroup, MutableList<AbstractKit>>? = null, productId: String? = "") {
        APPLICATION = app
        try {
            DoraemonKitReal.init(app, selfKits, productId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setWebDoorCallback(callback: WebDoorCallback?) {
        DoraemonKitReal.setWebDoorCallback(callback)
    }

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

    val isShow: Boolean
        get() = DoraemonKitReal.isShow

    fun setDebug(debug: Boolean) {
        DoraemonKitReal.setDebug(debug)
    }

    /**
     * 是否显示主入口icon
     */
    fun setAwaysShowMainIcon(awaysShow: Boolean) {
        DokitConstant.AWAYS_SHOW_MAIN_ICON = awaysShow
    }
}