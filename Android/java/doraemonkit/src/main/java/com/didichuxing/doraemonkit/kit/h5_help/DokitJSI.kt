package com.didichuxing.doraemonkit.kit.h5_help

import android.webkit.JavascriptInterface
import com.didichuxing.doraemonkit.kit.h5_help.bean.JsRequestBean
import com.didichuxing.doraemonkit.util.LogHelper

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/8/25-15:21
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DokitJSI {

    /**
     * wiki:https://developer.mozilla.org/zh-CN/docs/Web/API/XMLHttpRequest/open
     */
    @JavascriptInterface
    fun open(
        requestId: String?,
        url: String?,
        method: String?,
        async: Boolean?,
        user: String?,
        password: String?
    ) {
        if (JsRequestManager.jsRequestMap[requestId] == null) {
            JsRequestManager.jsRequestMap[requestId] =
                JsRequestBean(requestId, url, method, null, null, null)
        } else {
            JsRequestManager.jsRequestMap[requestId]?.apply {
                this.requestId = requestId
                this.url = url
                this.method = method
            }
        }

    }

    @JavascriptInterface
    fun setRequestHeader(requestId: String?, key: String?, vaule: String?) {
        JsRequestManager.jsRequestMap[requestId]?.apply {
            if (this.headers == null) {
                this.headers = mutableMapOf()
                this.headers!![key] = vaule
            } else {
                this.headers!![key] = vaule
            }
        }

    }


    @JavascriptInterface
    fun overrideMimeType(requestId: String?, mimeType: String?) {
        JsRequestManager.jsRequestMap[requestId]?.apply {
            this.mimeType = mimeType
        }

    }

    @JavascriptInterface
    fun send(requestId: String?, body: String?) {
        JsRequestManager.jsRequestMap[requestId]?.apply {
            this.body = body
        }
    }

    @JavascriptInterface
    fun submit() {
        LogHelper.i("DokitJSI", "====submit====")
    }
}