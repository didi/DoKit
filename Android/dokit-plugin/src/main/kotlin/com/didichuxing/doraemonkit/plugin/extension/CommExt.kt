package com.didichuxing.doraemonkit.plugin.extension

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/28-14:56
 * 描    述：
 * 修订历史：
 * ================================================
 */
open class CommExt(
    var gpsSwitch: Boolean = true,
    var networkSwitch: Boolean = true,
    var bigImgSwitch: Boolean = true,
    var webViewSwitch: Boolean = true,
    var didinetSwitch: Boolean = true
) {

    fun gpsSwitch(gpsSwitch: Boolean) {
        this.gpsSwitch = gpsSwitch
    }

    fun networkSwitch(networkSwitch: Boolean) {
        this.networkSwitch = networkSwitch
    }

    fun didinetSwitch(didinetSwitch: Boolean) {
        this.didinetSwitch = didinetSwitch
    }


    fun bigImgSwitch(bigImgSwitch: Boolean) {
        this.bigImgSwitch = bigImgSwitch
    }

    fun webViewSwitch(webViewSwitch: Boolean) {
        this.webViewSwitch = webViewSwitch
    }

    override fun toString(): String {
        return "CommExt(gpsSwitch=$gpsSwitch, networkSwitch=$networkSwitch,didinetSwitch=$didinetSwitch, bigImgSwitch=$bigImgSwitch, webviewSwitch=$webViewSwitch)"
    }


}