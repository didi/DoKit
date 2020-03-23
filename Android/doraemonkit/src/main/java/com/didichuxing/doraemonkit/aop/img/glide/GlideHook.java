package com.didichuxing.doraemonkit.aop.img.glide;

import com.bumptech.glide.request.RequestListener;
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DoraemonInterceptor;
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DoraemonWeakNetworkInterceptor;
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.LargePictureInterceptor;
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.MockInterceptor;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/20-18:19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class GlideHook {
    public static List<RequestListener> globalRequestListeners = new ArrayList<>();
    private static boolean IS_INSTALL = false;

    public static void install() {
        if (IS_INSTALL) {
            return;
        }
        try {
            //可能存在用户没有引入okhttp的情况
            globalRequestListeners.add(new DokitGlideListener());
            IS_INSTALL = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
