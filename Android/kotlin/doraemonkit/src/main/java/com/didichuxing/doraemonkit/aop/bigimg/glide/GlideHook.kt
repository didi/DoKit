package com.didichuxing.doraemonkit.aop.bigimg.glide

import com.blankj.utilcode.util.ReflectUtils
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.SingleRequest
import java.util.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/20-18:19
 * 描    述：注入到com.bumptech.glide.request.SingleRequest#init(方法中)
 * 修订历史：
 * ================================================
 */
public object GlideHook {
    /**
     * hook requestListeners字段
     *
     * @param singleRequest
     * @return
     */
    @JvmStatic
    public fun proxy(singleRequest: Any?) {
        try {
            var requestListeners: MutableList<RequestListener<*>?>? = null
            if (singleRequest is SingleRequest<*>) {
                requestListeners = ReflectUtils.reflect(singleRequest).field("requestListeners").get()
            }
            //可能存在用户没有引入okhttp的情况
            if (requestListeners == null) {
                requestListeners = ArrayList()
                requestListeners.add(DokitGlideRequestListener<Any?>())
            } else {
                requestListeners.add(DokitGlideRequestListener<Any?>())
            }
            if (singleRequest is SingleRequest<*>) {
                ReflectUtils.reflect(singleRequest).field("requestListeners", requestListeners)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}