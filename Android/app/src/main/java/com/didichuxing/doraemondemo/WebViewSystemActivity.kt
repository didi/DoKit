package com.didichuxing.doraemondemo

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by jintai on 2018/11/13.
 */
class WebViewSystemActivity : AppCompatActivity() {

    val TAG = "WebViewActivity"
    lateinit var mWebView: WebView
    val url = "https://xingyun.xiaojukeji.com/docs/dokit"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_webview)
        mWebView = findViewById<WebView>(R.id.normal_web_view)
        initWebView(mWebView)

        mWebView.loadUrl(url)

    }


    @SuppressLint("JavascriptInterface")
    private fun initWebView(webView: WebView) {
        val webSettings: WebSettings = webView.settings
        webSettings.pluginState = WebSettings.PluginState.ON
        webSettings.javaScriptEnabled = true
        webSettings.allowFileAccess = false
        webSettings.loadsImagesAutomatically = true
        webSettings.useWideViewPort = true
        webSettings.builtInZoomControls = false
        webSettings.defaultTextEncodingName = "UTF-8"
        webSettings.domStorageEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.javaScriptCanOpenWindowsAutomatically = false
        webSettings.allowFileAccessFromFileURLs = true
        webSettings.allowUniversalAccessFromFileURLs = true

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webView.removeJavascriptInterface("searchBoxJavaBridge_")
            webView.removeJavascriptInterface("accessibilityTraversal")
            webView.removeJavascriptInterface("accessibility")
        }


        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                return super.shouldInterceptRequest(view, request)
            }

        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                val message = consoleMessage!!.message()
                val lineNumber = consoleMessage.lineNumber()
                val sourceID = consoleMessage.sourceId()
                val messageLevel = consoleMessage.message()

                Log.i(
                    TAG, String.format(
                        "[%s] sourceID: %s lineNumber: %n message: %s",
                        messageLevel, sourceID, lineNumber, message
                    )
                )

                //Log.i(TAG, "consoleMessage===>${consoleMessage?.message()}")
                return super.onConsoleMessage(consoleMessage)
            }
        }
    }


    override fun onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack()
        } else {
            super.onBackPressed()
        }
    }

}
