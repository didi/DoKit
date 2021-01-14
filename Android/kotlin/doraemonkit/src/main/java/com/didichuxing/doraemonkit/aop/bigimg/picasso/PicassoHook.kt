package com.didichuxing.doraemonkit.aop.bigimg.picasso

import com.blankj.utilcode.util.ReflectUtils
import com.squareup.picasso.Request
import java.util.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/23-13:45
 * 描    述：
 * 修订历史：
 * ================================================
 */
public object PicassoHook {
    /**
     * 注入到com.squareup.picasso.Request 构造方法中
     */
    @JvmStatic
    public fun proxy(request: Any?) {
        try {
            if (request is Request) {
                val requestObj = request
                var transformations = requestObj.transformations
                if (transformations == null) {
                    transformations = ArrayList()
                    transformations.add(DokitPicassoTransformation(requestObj.uri, requestObj.resourceId))
                } else {
                    transformations.clear()
                    transformations.add(DokitPicassoTransformation(requestObj.uri, requestObj.resourceId))
                }
                ReflectUtils.reflect(request).field("transformations", transformations)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}