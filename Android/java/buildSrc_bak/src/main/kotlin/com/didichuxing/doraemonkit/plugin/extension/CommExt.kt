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
open class CommExt(var gpsSwitch: Boolean = true,
                   var networkSwitch: Boolean = true,
                   var bigImgSwitch: Boolean = true) {

    fun gpsSwitch(gpsSwitch: Boolean) {
        this.gpsSwitch = gpsSwitch
    }

    fun networkSwitch(networkSwitch: Boolean) {
        this.networkSwitch = networkSwitch
    }


    fun bigImgSwitch(bigImgSwitch: Boolean) {
        this.bigImgSwitch = bigImgSwitch
    }

    override fun toString(): String {
        return "CommExt(gpsSwitch=$gpsSwitch, networkSwitch=$networkSwitch, bigImgSwitch=$bigImgSwitch)"
    }


}