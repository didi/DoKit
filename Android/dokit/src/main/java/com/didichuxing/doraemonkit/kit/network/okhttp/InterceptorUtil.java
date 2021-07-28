package com.didichuxing.doraemonkit.kit.network.okhttp;

import com.didichuxing.doraemonkit.kit.network.core.ResourceType;
import com.didichuxing.doraemonkit.kit.network.core.ResourceTypeHelper;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/2/26-17:04
 * 描    述：拦截器工具类
 * 修订历史：
 * ================================================
 */
public class InterceptorUtil {

    public static boolean isImg(String contentType) {
        ResourceTypeHelper resourceTypeHelper = new ResourceTypeHelper();
        ResourceType resourceType = contentType != null ? resourceTypeHelper.determineResourceType(contentType) : null;
        if (resourceType == ResourceType.IMAGE) {
            return true;
        }
        return false;
    }

}
