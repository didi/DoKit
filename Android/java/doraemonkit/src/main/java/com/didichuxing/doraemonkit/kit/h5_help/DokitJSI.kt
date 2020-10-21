package com.didichuxing.doraemonkit.kit.h5_help

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ActivityUtils
import com.didichuxing.doraemonkit.okhttp_api.OkHttpWrap
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
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
class DokitJSI {


    init {
        JsHookDataManager.jsLocalStorage.clear()
        JsHookDataManager.jsSessionStorage.clear()
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
    fun setRequestHeader(requestId: String?, key: String?, vaule: String?) {
        JsHookDataManager.jsRequestMap[requestId]?.apply {
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
                updateStorageAdapter(H5DokitView.STORAGE_TYPE_LOCAL)
                return
            }
        }
        JsHookDataManager.jsLocalStorage.add(StorageBean(key, value))
        updateStorageAdapter(H5DokitView.STORAGE_TYPE_LOCAL)

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
        updateStorageAdapter(H5DokitView.STORAGE_TYPE_LOCAL)
    }


    /**
     * localStorage
     */
    @JavascriptInterface
    fun localStorageClear() {
        JsHookDataManager.jsLocalStorage.clear()
        updateStorageAdapter(H5DokitView.STORAGE_TYPE_LOCAL)
    }


    /**
     * sessionStorage
     */
    @JavascriptInterface
    fun sessionStorageSetItem(key: String?, value: String?) {
        JsHookDataManager.jsSessionStorage.forEach {
            if (it.key == key) {
                it.value = value
                updateStorageAdapter(H5DokitView.STORAGE_TYPE_Session)
                return
            }
        }
        JsHookDataManager.jsSessionStorage.add(StorageBean(key, value))
        updateStorageAdapter(H5DokitView.STORAGE_TYPE_Session)

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
        updateStorageAdapter(H5DokitView.STORAGE_TYPE_Session)
    }


    /**
     * sessionStorage
     */
    @JavascriptInterface
    fun sessionStorageClear() {
        JsHookDataManager.jsSessionStorage.clear()
        updateStorageAdapter(H5DokitView.STORAGE_TYPE_Session)
    }

    /**
     * 更新本地localStorage的adapter
     */
    private fun updateStorageAdapter(type: Int) {
        val h5DokitView = DokitViewManager.getInstance().getDokitView(
            ActivityUtils.getTopActivity(),
            H5DokitView::class.java.simpleName
        )
        if (h5DokitView != null) {
            h5DokitView as H5DokitView
            h5DokitView.updateAdapter(type)
        }
    }


}