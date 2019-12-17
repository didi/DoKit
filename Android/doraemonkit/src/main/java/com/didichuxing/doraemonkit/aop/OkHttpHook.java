package com.didichuxing.doraemonkit.aop;

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


    public static void installInterceptor(Interceptor interceptor) {
        if (interceptor != null) {
            globalInterceptors.add(interceptor);
        }
    }
}
