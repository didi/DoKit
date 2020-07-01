package com.didichuxing.doraemonkit.kit.network.okhttp

import com.didichuxing.doraemonkit.kit.network.okhttp.core.ResourceType
import com.didichuxing.doraemonkit.kit.network.okhttp.core.ResourceTypeHelper

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/2/26-17:04
 * 描    述：拦截器工具类
 * 修订历史：
 * ================================================
 */
object InterceptorUtil {
    fun isImg(contentType: String?): Boolean {
        val resourceTypeHelper = ResourceTypeHelper()
        val resourceType = if (contentType != null) resourceTypeHelper.determineResourceType(contentType) else null
        return if (resourceType == ResourceType.IMAGE) {
            true
        } else false
    }
}