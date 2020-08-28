package com.didichuxing.doraemondemo

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ResourceUtils
import com.didichuxing.doraemonkit.kit.h5_help.DokitJSI
import com.didichuxing.doraemonkit.kit.h5_help.JsRequestManager
import com.didichuxing.doraemonkit.util.LogHelper
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.jsoup.Jsoup

/**
 * Created by wanglikun on 2018/11/13.
 */
class WebViewActivity : AppCompatActivity() {
    val TAG = "WebViewActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        val webView = findViewById<WebView>(R.id.web_view)
        initWebView(webView)
//        webView.loadUrl("https://page-daily.kuaidadi.com/m/ddPage_0sTyVhyq.html")
        webView.loadUrl("file:///android_asset/dokit_index.html")
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
//        webSettings.allowFileAccessFromFileURLs = true
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
        //webview inject java object

        webView.addJavascriptInterface(DokitJSI(), "dokitJsi")

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
                request?.let {
                    //加载页面资源
                    if (it.isForMainFrame) {
                        LogHelper.i(TAG, "url===>${it.url?.toString()}  method==>${it.method}")

                        val client = OkHttpClient()

                        val request = Request.Builder()
                            .url(it.url?.toString())
                            .build()

                        val response = client.newCall(request).execute()
                        //注入本地js
                        val newHtml = injectJsHook(response.body()?.string())

                        return WebResourceResponse(
                            "text/html",
                            response.header("content-encoding", "utf-8"),
                            ConvertUtils.string2InputStream(newHtml, "utf-8")
                        )
                    } else {
                        //加载js网络请求
                        if (it.url.toString().contains("dokit_flag")) {

                            val jsRequestId = getUrlQuery(it.url.toString(), "dokit_flag")
                            val jsRequestBean = JsRequestManager.jsRequestMap[jsRequestId]
                            LogHelper.i(TAG, jsRequestBean.toString())

                            LogHelper.i(TAG, "js request url===>${it.url.toString()}")
                            val data = JSONObject()
                            data.put("code", 200)
                            data.put("message", "js hook")

                            return WebResourceResponse(
                                jsRequestBean?.mimeType,
                                "UTF-8",
                                ConvertUtils.string2InputStream(data.toString(), "UTF-8")
                            )

                        }

                    }
                }

                return super.shouldInterceptRequest(view, request)
            }

        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                //LogHelper.i(TAG, "consoleMessage===>${consoleMessage?.message()}")
                return super.onConsoleMessage(consoleMessage)
            }
        }
    }


    private fun getUrlQuery(url: String, key: String): String? {
        val httpUrl = HttpUrl.parse(url)

        val querys = httpUrl?.url()?.query?.split("&")
        val queryMap = mutableMapOf<String, String>()

        querys?.forEach {
            val keyAndValue = it.split("=")
            queryMap[keyAndValue[0]] = keyAndValue[1]
        }

        return queryMap[key]

    }

    //get mime type by url
    private fun getMimeType(url: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            when (extension) {
                "js" -> type = "text/javascript"
                "woff" -> type = "application/font-woff"
                "woff2" -> type = "application/font-woff2"
                "ttf" -> type = "application/x-font-ttf"
                "eot" -> type = "application/vnd.ms-fontobject"
                "svg" -> type = "text/javascript"
                else -> type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }
        }
        return type
    }

    /**
     * 注入js代码
     */
    private fun injectJsHook(html: String?): String {
        //读取本地js hook 代码
        val jsHook = ResourceUtils.readAssets2String("dokit_js_hook.html")
        val doc = Jsoup.parse(html)
        doc.outputSettings().prettyPrint(true)
        val elements = doc.getElementsByTag("head")
        if (elements.size > 0) {
            elements[0].prepend(jsHook)
        }
        return doc.toString()
    }


}