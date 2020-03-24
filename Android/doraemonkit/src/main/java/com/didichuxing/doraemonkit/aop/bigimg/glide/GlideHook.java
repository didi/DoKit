package com.didichuxing.doraemonkit.aop.bigimg.glide;

import android.support.annotation.Nullable;

import com.bumptech.glide.request.RequestListener;

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

    public static List<RequestListener> proxy(@Nullable List<RequestListener> requestListeners) {
        try {
            //可能存在用户没有引入okhttp的情况
            if (requestListeners == null) {
                requestListeners = new ArrayList<>();
                requestListeners.add(new DokitGlideRequestListener());
            } else {
                requestListeners.add(new DokitGlideRequestListener());
            }
            return requestListeners;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
