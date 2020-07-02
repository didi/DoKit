package com.didichuxing.doraemonkit.widget.webview

import android.webkit.WebView
import android.webkit.WebViewClient
import java.util.*

/**
 * Created by guofeng007 on 2020/6/8
 */
class MyWebViewClient : WebViewClient() {
    private val mListeners: MutableList<InvokeListener> = ArrayList()

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        if (url.startsWith("doraemon://invokeNative")) {
            handleInvokeFromJs(url)
            return true
        }
        return false
    }

    private fun handleInvokeFromJs(url: String) {
        for (listener in mListeners) {
            listener.onNativeInvoke(url)
        }
    }

    fun addInvokeListener(listener: InvokeListener) {
        mListeners.add(listener)
    }

    fun removeInvokeListener(listener: InvokeListener) {
        mListeners.remove(listener)
    }

    interface InvokeListener {
        fun onNativeInvoke(url: String?)
    }
}