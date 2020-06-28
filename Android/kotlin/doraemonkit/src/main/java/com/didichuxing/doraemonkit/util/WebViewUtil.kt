package com.didichuxing.doraemonkit.util

import android.content.Context
import android.webkit.WebView
import com.didichuxing.doraemonkit.config.GpsMockConfig
import com.didichuxing.doraemonkit.model.LatLng
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * @author lostjobs created on 2020/6/27
 */
object WebViewUtil {


    private fun assetFileToString(context: Context, urlString: String): String {
        val result = StringBuilder()
        BufferedReader(InputStreamReader(context.assets.open(urlString))).useLines {
            it.forEach { line->
                result.append(line).append("\n")
            }
        }
        return result.toString()
    }

    fun webVIewLoadLocalHtml(webView: WebView, jsPath: String) {
        val htmlData = assetFileToString(webView.context, jsPath)
        webView.loadDataWithBaseURL("http://localhost", htmlData, "text/html", "utf-8", null)
        webView.postDelayed({
            val latLng = GpsMockConfig.getMockLocation() ?: LatLng(.0, .0)
            val url = String.format("javascript:init(%s,%s)", latLng.latitude, latLng.longitude)
            webView.loadUrl(url)
        }, 1000)
    }
}