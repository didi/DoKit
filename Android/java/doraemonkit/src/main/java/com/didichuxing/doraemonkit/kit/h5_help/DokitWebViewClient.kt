package com.didichuxing.doraemonkit.kit.h5_help

import android.app.Activity
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Message
import android.view.KeyEvent
import android.webkit.*
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ResourceUtils
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.kit.network.NetworkManager
import com.didichuxing.doraemonkit.kit.network.core.DefaultResponseHandler
import com.didichuxing.doraemonkit.kit.network.core.NetworkInterpreter
import com.didichuxing.doraemonkit.kit.network.core.RequestBodyHelper
import com.didichuxing.doraemonkit.kit.network.okhttp.OkHttpInspectorRequest
import com.didichuxing.doraemonkit.kit.network.okhttp.OkHttpInspectorResponse
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager
import com.didichuxing.doraemonkit.kit.network.utils.bodyContent
import com.didichuxing.doraemonkit.okgo.DokitOkGo
import com.didichuxing.doraemonkit.util.LogHelper
import okhttp3.*
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.InputStream
import java.net.URLDecoder

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/8/28-17:22
 * 描    述：切面dokit
 * 修订历史：
 * ================================================
 */
class DokitWebViewClientProxy(webViewClient: WebViewClient?) : WebViewClient() {

    private val TAG = "DokitWebViewClientProxy"
    private val mWebViewClient: WebViewClient? = webViewClient
    private val mNetworkInterpreter = NetworkInterpreter.get()
    private val mOkhttpClient = OkHttpClient()

    /**
     * 更新悬浮窗上的链接
     */
    private fun updateH5DokitUrl(view: WebView?, url: String?) {
        view?.let { it ->
            if (it.context is Activity) {
                val activity = it.context as Activity
                val absDokitView: AbsDokitView? = DokitViewManager.getInstance()
                    .getDokitView(activity, H5DokitView::class.java.simpleName)
                absDokitView?.let { h5DokitView ->
                    (h5DokitView as H5DokitView).updateUrl(url)
                }

            }
        }
    }

    override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
        updateH5DokitUrl(view, url)
        if (mWebViewClient != null) {
            return mWebViewClient.shouldOverrideUrlLoading(view, url)
        }

        return super.shouldOverrideUrlLoading(view, url)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        request?.let { webRequest ->
            //加载页面资源
            if (webRequest.isForMainFrame) {
                LogHelper.i(
                    TAG,
                    "url===>${webRequest.url?.toString()}  method==>${webRequest.method}"
                )
                val httpUrl = HttpUrl.parse(webRequest.url?.toString())

                val url = if (httpUrl?.url()?.query.isNullOrBlank()) {
                    webRequest.url?.toString() + "?dokit_flag=web"
                } else {
                    webRequest.url?.toString() + "&dokit_flag=web"
                }

                val response = DokitOkGo.get<String>(url).execute()
                //注入本地js
                val newHtml = injectJsHook(response.body()?.string())

                return WebResourceResponse(
                    "text/html",
                    response.header("content-encoding", "utf-8"),
                    ConvertUtils.string2InputStream(newHtml, "utf-8")
                )
            } else {
                //加载js网络请求
                if (webRequest.url.toString().contains("dokit_flag")) {
                    val jsRequestId = getUrlQuery(webRequest.url.toString(), "dokit_flag")
                    val jsRequestBean = JsRequestManager.jsRequestMap[jsRequestId]
                    LogHelper.i(TAG, jsRequestBean.toString())
                    jsRequestBean?.let { requestBean ->
                        val url = HttpUrl.parse(requestBean.url)
                        val host = url?.host()
                        //如果是dokit mock host 则不进行拦截
                        if (host.equals(NetworkManager.MOCK_HOST, true)) {
                            JsRequestManager.jsRequestMap.remove(requestBean.requestId)
                            return null
                        }
                        //web 抓包
                        if (NetworkManager.isActive()) {
                            try {
                                //构建okhttp用来抓包
                                val newRequest: Request =
                                    JsHttpUtil.createOkHttpRequest(requestBean)

                                if (!JsHttpUtil.matchWhiteHost(newRequest)) {
                                    //发送模拟请求
                                    mOkhttpClient.newCall(newRequest).execute()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        // web 数据mock
                        url?.let { httpUrl ->
                            try {
                                val path = URLDecoder.decode(httpUrl.encodedPath(), "utf-8")
                                val queries = httpUrl.query()
                                val jsonQuery = JsHttpUtil.transformQuery(queries)
                                val jsonRequestBody = JsHttpUtil.transformRequestBody(
                                    requestBean.method,
                                    requestBean.body,
                                    requestBean.headers
                                )

                                val interceptMatchedId = DokitDbManager.getInstance().isMockMatched(
                                    path,
                                    jsonQuery,
                                    jsonRequestBody,
                                    DokitDbManager.MOCK_API_INTERCEPT,
                                    DokitDbManager.FROM_SDK_OTHER
                                )

                                val templateMatchedId = DokitDbManager.getInstance().isMockMatched(
                                    path,
                                    jsonQuery,
                                    jsonRequestBody,
                                    DokitDbManager.MOCK_API_TEMPLATE,
                                    DokitDbManager.FROM_SDK_OTHER
                                )
                                val newRequest: Request =
                                    JsHttpUtil.createOkHttpRequest(requestBean)
                                //发送模拟请求
                                val newResponse = mOkhttpClient.newCall(newRequest).execute()
                                //是否命中拦截规则
                                if (!interceptMatchedId.isNullOrBlank()) {
                                    JsRequestManager.jsRequestMap.remove(requestBean.requestId)
                                    return JsHttpUtil.matchedInterceptRule(
                                        httpUrl,
                                        path,
                                        interceptMatchedId,
                                        templateMatchedId,
                                        newRequest,
                                        newResponse,
                                        mOkhttpClient
                                    )
                                }

                                //是否命中模板规则
                                if (!templateMatchedId.isNullOrBlank()) {
                                    JsHttpUtil.matchedTemplateRule(
                                        newResponse,
                                        path,
                                        templateMatchedId
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }

                        JsRequestManager.jsRequestMap.remove(requestBean.requestId)
                        return null
                    }

                } else {
                    return super.shouldInterceptRequest(view, request)
                }

            }
        }

        return super.shouldInterceptRequest(view, request)
    }

    /**
     * 数据mock相关操作
     */
    private fun intercept() {

    }


    private fun getUrlQuery(url: String, key: String): String? {
        val httpUrl = HttpUrl.parse(url)

        val queries = httpUrl?.url()?.query?.split("&")
        val queryMap = mutableMapOf<String, String>()

        queries?.forEach {
            val keyAndValue = it.split("=")
            queryMap[keyAndValue[0]] = keyAndValue[1]
        }

        return queryMap[key]

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


    @RequiresApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        updateH5DokitUrl(view, request?.url?.path)
        if (mWebViewClient != null) {
            return mWebViewClient.shouldOverrideUrlLoading(view, request)
        }
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        if (mWebViewClient != null) {
            return mWebViewClient.shouldInterceptRequest(view, url)
        }
        return super.shouldInterceptRequest(view, url)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        if (mWebViewClient != null) {
            return mWebViewClient.onPageStarted(view, url, favicon)
        }
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        if (mWebViewClient != null) {
            return mWebViewClient.onPageFinished(view, url)
        }
        super.onPageFinished(view, url)
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        if (mWebViewClient != null) {
            return mWebViewClient.onLoadResource(view, url)
        }
        super.onLoadResource(view, url)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPageCommitVisible(view: WebView?, url: String?) {
        if (mWebViewClient != null) {
            return mWebViewClient.onPageCommitVisible(view, url)
        }
        super.onPageCommitVisible(view, url)
    }

    override fun onTooManyRedirects(view: WebView?, cancelMsg: Message?, continueMsg: Message?) {
        if (mWebViewClient != null) {
            return mWebViewClient.onTooManyRedirects(view, cancelMsg, continueMsg)
        }
        super.onTooManyRedirects(view, cancelMsg, continueMsg)
    }

    override fun onReceivedError(
        view: WebView?,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        if (mWebViewClient != null) {
            return mWebViewClient.onReceivedError(view, errorCode, description, failingUrl)
        }
        super.onReceivedError(view, errorCode, description, failingUrl)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        if (mWebViewClient != null) {
            return mWebViewClient.onReceivedError(view, request, error)
        }
        super.onReceivedError(view, request, error)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        if (mWebViewClient != null) {
            return mWebViewClient.onReceivedHttpError(view, request, errorResponse)
        }
        super.onReceivedHttpError(view, request, errorResponse)
    }

    override fun onFormResubmission(view: WebView?, dontResend: Message?, resend: Message?) {
        if (mWebViewClient != null) {
            return mWebViewClient.onFormResubmission(view, dontResend, resend)
        }
        super.onFormResubmission(view, dontResend, resend)
    }

    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        updateH5DokitUrl(view, url)
        if (mWebViewClient != null) {
            return mWebViewClient.doUpdateVisitedHistory(view, url, isReload)
        }
        super.doUpdateVisitedHistory(view, url, isReload)
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        if (mWebViewClient != null) {
            return mWebViewClient.onReceivedSslError(view, handler, error)
        }
        super.onReceivedSslError(view, handler, error)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onReceivedClientCertRequest(view: WebView?, request: ClientCertRequest?) {
        if (mWebViewClient != null) {
            return mWebViewClient.onReceivedClientCertRequest(view, request)
        }
        super.onReceivedClientCertRequest(view, request)
    }

    override fun onReceivedHttpAuthRequest(
        view: WebView?,
        handler: HttpAuthHandler?,
        host: String?,
        realm: String?
    ) {
        if (mWebViewClient != null) {
            return mWebViewClient.onReceivedHttpAuthRequest(view, handler, host, realm)
        }
        super.onReceivedHttpAuthRequest(view, handler, host, realm)
    }

    override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
        if (mWebViewClient != null) {
            return mWebViewClient.shouldOverrideKeyEvent(view, event)
        }
        return super.shouldOverrideKeyEvent(view, event)
    }

    override fun onUnhandledKeyEvent(view: WebView?, event: KeyEvent?) {
        if (mWebViewClient != null) {
            return mWebViewClient.onUnhandledKeyEvent(view, event)
        }
        super.onUnhandledKeyEvent(view, event)
    }

    override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
        if (mWebViewClient != null) {
            return mWebViewClient.onScaleChanged(view, oldScale, newScale)
        }
        super.onScaleChanged(view, oldScale, newScale)
    }

    override fun onReceivedLoginRequest(
        view: WebView?,
        realm: String?,
        account: String?,
        args: String?
    ) {
        if (mWebViewClient != null) {
            return mWebViewClient.onReceivedLoginRequest(view, realm, account, args)
        }
        super.onReceivedLoginRequest(view, realm, account, args)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
        if (mWebViewClient != null) {
            return mWebViewClient.onRenderProcessGone(view, detail)
        }
        return super.onRenderProcessGone(view, detail)
    }

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    override fun onSafeBrowsingHit(
        view: WebView?,
        request: WebResourceRequest?,
        threatType: Int,
        callback: SafeBrowsingResponse?
    ) {
        if (mWebViewClient != null) {
            return mWebViewClient.onSafeBrowsingHit(view, request, threatType, callback)
        }
        super.onSafeBrowsingHit(view, request, threatType, callback)
    }


}