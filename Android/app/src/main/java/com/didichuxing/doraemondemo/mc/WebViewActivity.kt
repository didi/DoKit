package com.didichuxing.doraemondemo.mc

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.didichuxing.doraemondemo.R
import com.didichuxing.doraemonkit.util.LogHelper

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/29-21:58
 * 描    述：
 * 修订历史：
 * ================================================
 */
class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        val webview = findViewById<WebView>(R.id.webview)

        webview.settings.allowFileAccess = true
        webview.settings.allowContentAccess = true
        webview.settings.allowFileAccessFromFileURLs = true
        webview.settings.javaScriptEnabled = true
        webview.settings.domStorageEnabled = true
        webview.settings.databaseEnabled = true
        webview.settings.setAppCacheEnabled(true)
        webview.settings.mixedContentMode =WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        webview.settings.allowUniversalAccessFromFileURLs = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webview, true);
        }

        webview.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                return super.onConsoleMessage(consoleMessage)
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }
        }

        webview.webViewClient = object : WebViewClient() {

            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                return super.shouldInterceptRequest(view, request)
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                LogHelper.e("WEB", "dokit onPageFinished()")
            }

        }

//        val url = "https://www.dokit.cn/#/index/home"
        val url = "https://page.xiaojukeji.com/m/ddPage_0sMxiuk7.html"
        val webViewBuilder = MyTestWebViewBuilder(MyProxyWebView(webview), url)
        webViewBuilder.create()
    }

}
