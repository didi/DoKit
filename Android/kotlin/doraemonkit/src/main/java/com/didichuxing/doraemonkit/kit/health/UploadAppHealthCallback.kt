package com.didichuxing.doraemonkit.kit.health

import com.didichuxing.doraemonkit.okgo.model.Response

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020-01-07-15:42
 * 描    述：
 * 修订历史：update pengyushan 2020-07-06 转化为kotlin
 * ================================================
 */
interface UploadAppHealthCallback {
    /**
     * @param response
     */
    fun onSuccess(response: Response<String>?)

    /**
     * @param response
     */
    fun onError(response: Response<String>?)
}