package com.didichuxing.doraemonkit.kit.h5_help

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Message
import android.view.KeyEvent
import com.tencent.smtt.export.external.interfaces.*
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

/**
 * didi Create on 2023/3/23 .
 *
 * Copyright (c) 2023/3/23 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2023/3/23 11:29 上午
 * @Description WebViewClient 代理
 */

open class ProxyX5WebViewClient(webViewClient: WebViewClient) : WebViewClient() {

    private val mWebViewClient: WebViewClient = webViewClient


    override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
        return mWebViewClient.shouldOverrideUrlLoading(view, url)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return mWebViewClient.shouldOverrideUrlLoading(view, request)
    }

    override fun shouldInterceptRequest(p0: WebView?, p1: WebResourceRequest?, p2: Bundle?): WebResourceResponse {
        return mWebViewClient.shouldInterceptRequest(p0, p1, p2)
    }

    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        return mWebViewClient.shouldInterceptRequest(view, url)
    }

    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        return mWebViewClient.shouldInterceptRequest(view, request)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        mWebViewClient.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        mWebViewClient.onPageFinished(view, url)
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        mWebViewClient.onLoadResource(view, url)
    }

    override fun onPageCommitVisible(view: WebView?, url: String?) {
        mWebViewClient.onPageCommitVisible(view, url)
    }

    override fun onTooManyRedirects(view: WebView?, cancelMsg: Message?, continueMsg: Message?) {
        mWebViewClient.onTooManyRedirects(view, cancelMsg, continueMsg)
    }

    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
        mWebViewClient.onReceivedError(view, errorCode, description, failingUrl)
    }

    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        mWebViewClient.onReceivedError(view, request, error)
    }

    override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
        mWebViewClient.onReceivedHttpError(view, request, errorResponse)
    }

    override fun onFormResubmission(view: WebView?, dontResend: Message?, resend: Message?) {
        mWebViewClient.onFormResubmission(view, dontResend, resend)
    }

    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        mWebViewClient.doUpdateVisitedHistory(view, url, isReload)
    }

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        mWebViewClient.onReceivedSslError(view, handler, error)
    }

    override fun onReceivedClientCertRequest(view: WebView?, request: ClientCertRequest?) {
        mWebViewClient.onReceivedClientCertRequest(view, request)
    }

    override fun onReceivedHttpAuthRequest(view: WebView?, handler: HttpAuthHandler?, host: String?, realm: String?) {
        mWebViewClient.onReceivedHttpAuthRequest(view, handler, host, realm)
    }

    override fun onReceivedLoginRequest(view: WebView?, realm: String?, account: String?, args: String?) {
        mWebViewClient.onReceivedLoginRequest(view, realm, account, args)
    }

    override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
        return mWebViewClient.shouldOverrideKeyEvent(view, event)
    }

    override fun onUnhandledKeyEvent(view: WebView?, event: KeyEvent?) {
        mWebViewClient.onUnhandledKeyEvent(view, event)
    }

    override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
        mWebViewClient.onScaleChanged(view, oldScale, newScale)
    }

    override fun onDetectedBlankScreen(p0: String?, p1: Int) {
        mWebViewClient.onDetectedBlankScreen(p0, p1)
    }
}
