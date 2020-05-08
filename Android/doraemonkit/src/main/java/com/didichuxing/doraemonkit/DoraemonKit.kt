package com.didichuxing.doraemonkit

import android.app.Application
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager.WebDoorCallback

/**
 * Created by zhangweida on 2018/6/22.
 */
object DoraemonKit {
    @JvmField
    var APPLICATION: Application? = null
    private const val TAG = "DoraemonKit"


    /**
     * @param app
     * @param mapKits  自定义kits  根据用户传进来的分组 建议优选选择mapKits 两者都传的话会选择mapKits
     * @param selfKits  自定义kits
     * @param productId Dokit平台端申请的productId
     */
    @JvmStatic
    fun install(app: Application, mapKits: LinkedHashMap<String, MutableList<AbstractKit>>? = linkedMapOf(), selfKits: MutableList<AbstractKit>? = mutableListOf(), productId: String = "") {
        APPLICATION = app
        try {
            DoraemonKitReal.install(app, mapKits, selfKits, productId)
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