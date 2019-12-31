package com.didichuxing.doraemonkit.aop;

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
 * 创建日期：2019-12-13-10:40
 * 描    述：用来通过ASM在编译器进行hook
 * 修订历史：
 * ================================================
 */
public class OkHttpHook {
    public static List<Interceptor> globalInterceptors = new ArrayList<>();
    private static boolean IS_INSTALL = false;

    public static void installInterceptor() {
        if (IS_INSTALL) {
            return;
        }
        globalInterceptors.add(new MockInterceptor());
        globalInterceptors.add(new LargePictureInterceptor());
        globalInterceptors.add(new DoraemonWeakNetworkInterceptor());
        globalInterceptors.add(new DoraemonInterceptor());
        IS_INSTALL = true;
    }
}
