package com.didichuxing.doraemonkit.kit.h5_help

import android.content.Context
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.didichuxing.doraemonkit.util.LogHelper
import kotlin.math.log

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/8/25-14:45
 * 描    述：
 * 修订历史：
 * ================================================
 */
class WebViewClientProxy(
    webViewClient: WebViewClient,
    onOverrideUrlLoading: ((url: String?) -> Unit)
) : WebViewClient() {
    val TAG = "WebViewClientProxy"
    private val mWebViewClient: WebViewClient? = webViewClient
    private val mOnOverrideUrlLoading: ((url: String?) -> Unit)? = onOverrideUrlLoading


    @RequiresApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        mOnOverrideUrlLoading?.invoke(request?.url?.path)
        if (mWebViewClient != null) {
            return mWebViewClient.shouldOverrideUrlLoading(view, request)
        }

        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        mOnOverrideUrlLoading?.invoke(url)
        if (mWebViewClient != null) {
            return mWebViewClient.shouldOverrideUrlLoading(view, url)
        }
        return super.shouldOverrideUrlLoading(view, url)
    }


    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        mOnOverrideUrlLoading?.invoke(url)
        mWebViewClient?.doUpdateVisitedHistory(view, url, isReload)
    }

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        LogHelper.i(TAG, "shouldInterceptRequest===>${request?.url?.path}")
        return super.shouldInterceptRequest(view, request)
    }


}