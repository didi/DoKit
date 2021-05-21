package com.didichuxing.doraemonkit.aop.bigimg.glide;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.SingleRequest;
import com.didichuxing.doraemonkit.util.ReflectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/20-18:19
 * 描    述：注入到com.bumptech.glide.request.SingleRequest#init(方法中)
 * 修订历史：
 * ================================================
 */
public class GlideHook {
    /**
     * hook requestListeners字段
     *
     * @param singleRequest
     * @return
     */

    public static void proxy(Object singleRequest) {
        try {
            List<RequestListener> requestListeners = null;
            if (singleRequest instanceof SingleRequest) {
                requestListeners = ReflectUtils.reflect(singleRequest).field("requestListeners").get();
            }
            //可能存在用户没有引入okhttp的情况
            if (requestListeners == null) {
                requestListeners = new ArrayList<>();
                requestListeners.add(new DokitGlideRequestListener());
            } else {
                requestListeners.add(new DokitGlideRequestListener());
            }
            if (singleRequest instanceof SingleRequest) {
                ReflectUtils.reflect(singleRequest).field("requestListeners",requestListeners);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
