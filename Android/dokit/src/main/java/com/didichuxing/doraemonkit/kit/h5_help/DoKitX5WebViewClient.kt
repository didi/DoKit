package com.didichuxing.doraemonkit.kit.h5_help

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.util.ConvertUtils
import com.didichuxing.doraemonkit.util.ResourceUtils
import com.didichuxing.doraemonkit.okhttp_api.OkHttpWrap
import com.didichuxing.doraemonkit.aop.urlconnection.OkhttpClientUtil
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView
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
class DoKitX5WebViewClient(webViewClient: WebViewClient, userAgent: String) : ProxyX5WebViewClient(webViewClient) {

    private val TAG = "DoKitWebViewClient"
    private val mUserAgent = userAgent

    /**
     * 更新悬浮窗上的链接
     */
    private fun updateH5DokitUrl(view: WebView?, url: String?) {
        view?.let { it ->
            if (it.context is Activity) {
                val activity = it.context as Activity
                val absDokitView: AbsDoKitView? = DoKit.getDoKitView<H5DoKitView>(activity, H5DoKitView::class)
                absDokitView?.let { h5DokitView ->
                    (h5DokitView as H5DoKitView).updateUrl(url)
                }

            }
        }
    }

    override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
        updateH5DokitUrl(view, url)
        return super.shouldOverrideUrlLoading(view, url)
    }

    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        updateH5DokitUrl(view, url)
        super.doUpdateVisitedHistory(view, url, isReload)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        updateH5DokitUrl(view, request?.url?.path)
        return super.shouldOverrideUrlLoading(view, request)
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        //开关均被关闭则不进行拦截
        if (!DoKitManager.H5_JS_INJECT && !DoKitManager.H5_VCONSOLE_INJECT) {
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
                var newHtml = if (DoKitManager.H5_JS_INJECT) {
                    injectJsHook(OkHttpWrap.toResponseBody(response)?.string())
                } else {
                    OkHttpWrap.toResponseBody(response)?.string()
                }
                //注入vConsole的代码
                if (DoKitManager.H5_VCONSOLE_INJECT) {
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
    private fun dealMock(requestBean: JsRequestBean, url: HttpUrl?, view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
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

}
