package com.didichuxing.doraemonkit.kit.h5_help

import android.webkit.JavascriptInterface
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.util.ActivityUtils
import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.okhttp_api.OkHttpWrap
import com.didichuxing.doraemonkit.kit.h5_help.bean.JsRequestBean
import com.didichuxing.doraemonkit.kit.h5_help.bean.StorageBean

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/8/25-15:21
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DoKitJSI {


    init {
        JsHookDataManager.jsLocalStorage.clear()
        JsHookDataManager.jsSessionStorage.clear()
    }


    @JavascriptInterface
    fun fetch(
        requestId: String?,
        url: String?,
        method: String?,
        origin: String?,
        headers: String?,
        body: String?
    ) {

        var headerMap: MutableMap<String?, String?> = mutableMapOf()
        headers?.let {
            headerMap = GsonUtils.fromJson(headers, MutableMap::class.java) as MutableMap<String?, String?>
        }

        val httpUrl = OkHttpWrap.createHttpUrl(url)
        val newUrl = if (httpUrl == null) {
            origin + url
        } else {
            url
        }

//        LogHelper.i(
//            "DokitJSI",
//            "requestId==>$requestId,url==>$url,method==>$method,origin==>$origin,headers==>$headerMap,body==>$body"
//        )
        if (JsHookDataManager.jsRequestMap[requestId] == null) {
            JsHookDataManager.jsRequestMap[requestId] =
                JsRequestBean(requestId, newUrl, method, headerMap, null, body)
        } else {
            JsHookDataManager.jsRequestMap[requestId]?.apply {
                this.requestId = requestId
                this.url = newUrl
                this.method = method
                this.headers = headerMap
                this.mimeType = null
                this.body = body
            }
        }
    }

    /**
     * wiki:https://developer.mozilla.org/zh-CN/docs/Web/API/XMLHttpRequest/open
     */
    @JavascriptInterface
    fun open(
        requestId: String?,
        url: String?,
        method: String?,
        origin: String?
    ) {
        val httpUrl = OkHttpWrap.createHttpUrl(url)
        val newUrl = if (httpUrl == null) {
            origin + url
        } else {
            url
        }

        if (JsHookDataManager.jsRequestMap[requestId] == null) {
            JsHookDataManager.jsRequestMap[requestId] =
                JsRequestBean(requestId, newUrl, method, null, null, null)
        } else {
            JsHookDataManager.jsRequestMap[requestId]?.apply {
                this.requestId = requestId
                this.url = newUrl
                this.method = method
            }
        }

    }

    @JavascriptInterface
    fun setRequestHeader(requestId: String?, key: String?, value: String?) {
        JsHookDataManager.jsRequestMap[requestId]?.apply {
            if (this.headers == null) {
                this.headers = mutableMapOf()
                this.headers!![key] = value
            } else {
                this.headers!![key] = value
            }
        }

    }


    @JavascriptInterface
    fun overrideMimeType(requestId: String?, mimeType: String?) {
        JsHookDataManager.jsRequestMap[requestId]?.apply {
            this.mimeType = mimeType
        }

    }

    @JavascriptInterface
    fun send(requestId: String?, body: String?) {
        JsHookDataManager.jsRequestMap[requestId]?.apply {
            this.body = body
        }
    }

    /**
     * localStorage
     */
    @JavascriptInterface
    fun localStorageSetItem(key: String?, value: String?) {
        JsHookDataManager.jsLocalStorage.forEach {
            if (it.key == key) {
                it.value = value
                updateStorageAdapter(H5DoKitView.STORAGE_TYPE_LOCAL)
                return
            }
        }
        JsHookDataManager.jsLocalStorage.add(StorageBean(key, value))
        updateStorageAdapter(H5DoKitView.STORAGE_TYPE_LOCAL)

    }


    /**
     * localStorage
     */
    @JavascriptInterface
    fun localStorageRemoveItem(key: String?) {
        var index = -1
        JsHookDataManager.jsLocalStorage.forEachIndexed { innerIndex, localStorageBean ->
            if (localStorageBean.key == key) {
                index = innerIndex
                return@forEachIndexed
            }
        }
        if (index != -1) {
            JsHookDataManager.jsLocalStorage.removeAt(index)
        }
        updateStorageAdapter(H5DoKitView.STORAGE_TYPE_LOCAL)
    }


    /**
     * localStorage
     */
    @JavascriptInterface
    fun localStorageClear() {
        JsHookDataManager.jsLocalStorage.clear()
        updateStorageAdapter(H5DoKitView.STORAGE_TYPE_LOCAL)
    }


    /**
     * sessionStorage
     */
    @JavascriptInterface
    fun sessionStorageSetItem(key: String?, value: String?) {
        JsHookDataManager.jsSessionStorage.forEach {
            if (it.key == key) {
                it.value = value
                updateStorageAdapter(H5DoKitView.STORAGE_TYPE_Session)
                return
            }
        }
        JsHookDataManager.jsSessionStorage.add(StorageBean(key, value))
        updateStorageAdapter(H5DoKitView.STORAGE_TYPE_Session)

    }


    /**
     * sessionStorage
     */
    @JavascriptInterface
    fun sessionStorageRemoveItem(key: String?) {
        var index = -1
        JsHookDataManager.jsSessionStorage.forEachIndexed { innerIndex, sessionStorageBean ->
            if (sessionStorageBean.key == key) {
                index = innerIndex
                return@forEachIndexed
            }
        }
        if (index != -1) {
            JsHookDataManager.jsSessionStorage.removeAt(index)
        }
        updateStorageAdapter(H5DoKitView.STORAGE_TYPE_Session)
    }


    /**
     * sessionStorage
     */
    @JavascriptInterface
    fun sessionStorageClear() {
        JsHookDataManager.jsSessionStorage.clear()
        updateStorageAdapter(H5DoKitView.STORAGE_TYPE_Session)
    }

    /**
     * 更新本地localStorage的adapter
     */
    private fun updateStorageAdapter(type: Int) {
        DoKit.getDoKitView<H5DoKitView>(
            ActivityUtils.getTopActivity(),
            H5DoKitView::class
        )?.updateAdapter(type)
    }


}
