package com.didichuxing.doraemonkit.kit.h5_help

import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import android.os.Message
import android.view.KeyEvent
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ResourceUtils
import com.didichuxing.doraemonkit.okhttp_api.OkHttpWrap
import com.didichuxing.doraemonkit.aop.urlconnection.OkhttpClientUtil
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.kit.h5_help.bean.JsRequestBean
import com.didichuxing.doraemonkit.kit.network.NetworkManager
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager
import com.didichuxing.doraemonkit.util.LogHelper
import com.tencent.smtt.export.external.interfaces.*
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import okhttp3.*
import org.jsoup.Jsoup
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
class DokitX5WebViewClient(webViewClient: WebViewClient?, userAgent: String) : WebViewClient() {

    private val TAG = "DokitWebViewClient"
    private val mWebViewClient: WebViewClient? = webViewClient
    private val mUserAgent = userAgent

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
        //开关均被关闭则不进行拦截
        if (!DoKitConstant.H5_JS_INJECT && !DoKitConstant.H5_VCONSOLE_INJECT) {
            return super.shouldInterceptRequest(view, request)
        }
        request?.let { webRequest ->
            //加载页面资源
            if (webRequest.isForMainFrame) {
                LogHelper.i(
                    TAG,
                    "url===>${webRequest.url?.toString()}  method==>${webRequest.method} thread==>${Thread.currentThread().name}"
                )
                val httpUrl = OkHttpWrap.createHttpUrl(webRequest.url?.toString())

                val url = if (OkHttpWrap.toUrl(httpUrl)?.query.isNullOrBlank()) {
                    webRequest.url?.toString() + "?dokit_flag=web"
                } else {
                    webRequest.url?.toString() + "&dokit_flag=web"
                }

                val httpRequest = Request.Builder()
                    .header("User-Agent", mUserAgent)
                    .url(url)
                    .build()
                val response = OkhttpClientUtil.okhttpClient.newCall(httpRequest).execute()

                //注入本地网络拦截js
                var newHtml = if (DoKitConstant.H5_JS_INJECT) {
                    injectJsHook(OkHttpWrap.toResponseBody(response)?.string())
                } else {
                    OkHttpWrap.toResponseBody(response)?.string()
                }
                //注入vConsole的代码
                if (DoKitConstant.H5_VCONSOLE_INJECT) {
                    newHtml = injectVConsoleHook(newHtml)
                }

                return WebResourceResponse(
                    "text/html",
                    response.header("content-encoding", "utf-8"),
                    ConvertUtils.string2InputStream(newHtml, "utf-8")
                )
            } else {
                //加载js网络请求
                if (webRequest.url.toString().contains("dokit_flag")) {
                    val jsRequestId = getUrlQuery(webRequest.url.toString(), "dokit_flag")
                    val jsRequestBean = JsHookDataManager.jsRequestMap[jsRequestId]
                    LogHelper.i(TAG, jsRequestBean.toString())
                    jsRequestBean?.let { requestBean ->
                        val url = OkHttpWrap.createHttpUrl(requestBean.url)
                        val host = OkHttpWrap.toRequestHost(url)
                        //如果是dokit mock host 则不进行拦截
                        if (host.equals(NetworkManager.MOCK_HOST, true)) {
                            JsHookDataManager.jsRequestMap.remove(requestBean.requestId)
                            return null
                        }

                        // web 数据mock
                        return dealMock(requestBean, url, view, request)
                    }

                } else {
                    return super.shouldInterceptRequest(view, request)
                }

            }
        }

        return super.shouldInterceptRequest(view, request)
    }

    /**
     * 处理数据mock的相关逻辑
     */
    private fun dealMock(
        requestBean: JsRequestBean,
        url: HttpUrl?,
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        url?.let { httpUrl ->
            try {
                val path = URLDecoder.decode(OkHttpWrap.toEncodedPath(httpUrl), "utf-8")
                val queries = OkHttpWrap.toHttpQuery(httpUrl)
                val jsonQuery = JsHttpUtil.transformQuery(queries)
                val jsonRequestBody = JsHttpUtil.transformRequestBody(
                    requestBean.method,
                    requestBean.body,
                    requestBean.headers
                )

                val interceptMatchedId =
                    DokitDbManager.getInstance().isMockMatched(
                        path,
                        jsonQuery,
                        jsonRequestBody,
                        DokitDbManager.MOCK_API_INTERCEPT,
                        DokitDbManager.FROM_SDK_OTHER
                    )

                val templateMatchedId =
                    DokitDbManager.getInstance().isMockMatched(
                        path,
                        jsonQuery,
                        jsonRequestBody,
                        DokitDbManager.MOCK_API_TEMPLATE,
                        DokitDbManager.FROM_SDK_OTHER
                    )

                //如果interceptMatchedId和templateMatchedId都为null 直接不进行操作
                if (interceptMatchedId.isNullOrBlank() && templateMatchedId.isNullOrBlank()) {
                    //web 抓包
                    if (NetworkManager.isActive()) {
                        try {
                            //构建okhttp用来抓包
                            val newRequest: Request =
                                JsHttpUtil.createOkHttpRequest(requestBean, mUserAgent)

                            if (JsHttpUtil.matchWhiteHost(newRequest)) {
                                //发送模拟请求
                                OkhttpClientUtil.okhttpClient.newCall(newRequest).execute()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    return super.shouldInterceptRequest(view, request)
                }

                val newRequest: Request =
                    JsHttpUtil.createOkHttpRequest(requestBean, mUserAgent)
                //发送模拟请求
                val newResponse =
                    OkhttpClientUtil.okhttpClient.newCall(newRequest).execute()
                //是否命中拦截规则
                if (!interceptMatchedId.isNullOrBlank()) {
                    JsHookDataManager.jsRequestMap.remove(requestBean.requestId)
                    return JsHttpUtil.matchedX5InterceptRule(
                        httpUrl,
                        path,
                        interceptMatchedId,
                        templateMatchedId,
                        newRequest,
                        newResponse,
                        OkhttpClientUtil.okhttpClient
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
        JsHookDataManager.jsRequestMap.remove(requestBean.requestId)

        return super.shouldInterceptRequest(view, request)
    }


    private fun getUrlQuery(url: String, key: String): String? {
        val httpUrl = OkHttpWrap.createHttpUrl(url)

        val queries = OkHttpWrap.toUrl(httpUrl)?.query?.split("&")
        val queryMap = mutableMapOf<String, String>()

        queries?.forEach {
            val keyAndValue = it.split("=")
            queryMap[keyAndValue[0]] = keyAndValue[1]
        }

        return queryMap[key]

    }

    /**
     * 注入hook js 哇共诺请求的代码
     */
    private fun injectJsHook(html: String?): String {
        //读取本地js hook 代码
        val jsHook = ResourceUtils.readAssets2String("h5help/dokit_js_hook.html")
        val doc = Jsoup.parse(html)
        doc.outputSettings().prettyPrint(true)
        val elements = doc.getElementsByTag("head")
        if (elements.size > 0) {
            elements[0].prepend(jsHook)
        }
        return doc.toString()
    }

    /**
     * 注入 vConsole的代码
     */
    private fun injectVConsoleHook(html: String?): String {
        //读取本地js hook 代码
        val vconsoleHook = ResourceUtils.readAssets2String("h5help/dokit_js_vconsole_hook.html")
        val doc = Jsoup.parse(html)
        doc.outputSettings().prettyPrint(true)
        val elements = doc.getElementsByTag("head")
        if (elements.size > 0) {
            elements[elements.size - 1].append(vconsoleHook)
        }
        return doc.toString()
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


    override fun onReceivedSslError(
        p0: WebView?,
        p1: SslErrorHandler?,
        p2: com.tencent.smtt.export.external.interfaces.SslError?
    ) {

        if (mWebViewClient != null) {
            return mWebViewClient.onReceivedSslError(p0, p1, p2)
        }
        super.onReceivedSslError(p0, p1, p2)
    }

    override fun onDetectedBlankScreen(p0: String?, p1: Int) {
        if (mWebViewClient != null) {
            return mWebViewClient.onDetectedBlankScreen(p0, p1)
        }
        super.onDetectedBlankScreen(p0, p1)
    }
}