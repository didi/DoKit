package com.didichuxing.doraemonkit.plugin.extension

import org.gradle.api.Action

/**
 * Created by jint on 07/10/2018.
 *
 * DoKit plugin 配置实例  @see DoKitExtension
 *
 * 支持DSL语法可以在插件中直接配置
 */
open class DoKitExtension(
    var pluginEnable: Boolean = true,
    var logEnable: Boolean = true,
    var thirdLibEnable: Boolean = true,

    var gpsEnable: Boolean = true,
    var gps: GpsExtension = GpsExtension(),

    var networkEnable: Boolean = true,
    var network: NetworkExtension = NetworkExtension(),

    var bigImageEnable: Boolean = true,
    var bigImage: BigImageExtension = BigImageExtension(),

    var webViewEnable: Boolean = true,
    var webView: WebViewExtension = WebViewExtension(),

    var slowMethodEnable: Boolean = true,
    var slowMethod: SlowMethodExtension = SlowMethodExtension()
) {


    /**
     * gps 支持 DSL 语法
     *
     * @param action
     */
    fun gps(action: Action<GpsExtension>) {
        action.execute(gps)
    }

    /**
     * network 支持 DSL 语法
     *
     * @param action
     */
    fun network(action: Action<NetworkExtension>) {
        action.execute(network)
    }

    /**
     * bigImage 支持 DSL 语法
     *
     * @param action
     */
    fun bigImage(action: Action<BigImageExtension>) {
        action.execute(bigImage)
    }
    
    fun webView(action: Action<WebViewExtension>) {
        action.execute(webView)
    }

    /**
     * 让slowMethod 支持 DSL 语法
     *
     * @param action
     */
    fun slowMethod(action: Action<SlowMethodExtension>) {
        action.execute(slowMethod)
    }




    override fun toString(): String {
        return "DoKitExtension(pluginEnable=$pluginEnable, logEnable=$logEnable, thirdLibEnable=$thirdLibEnable, gpsEnable=$gpsEnable, gps=$gps, networkEnable=$networkEnable, network=$network, bigImageEnable=$bigImageEnable, bigImage=$bigImage, webViewEnable=$webViewEnable, webView=$webView, slowMethodEnable=$slowMethodEnable, slowMethod=$slowMethod)"
    }


}
