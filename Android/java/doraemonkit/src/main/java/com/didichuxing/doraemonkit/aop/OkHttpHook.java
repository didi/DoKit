package com.didichuxing.doraemonkit.aop;

import com.didichuxing.doraemonkit.constant.DoKitConstant;
import com.didichuxing.doraemonkit.kit.core.DokitAbility;
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.AbsDoKitInterceptor;
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DokitCapInterceptor;
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DokitExtInterceptor;
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DokitLargePicInterceptor;
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DokitMockInterceptor;
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DokitWeakNetworkInterceptor;
import com.didichuxing.doraemonkit.util.ReflectUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import static com.baidu.location.e.m.as;

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
//    每个拦截器都有自己的相对优点。

//    应用拦截器
//    不需要担心中间响应，如重定向和重试。
//    总是调用一次，即使从缓存提供HTTP响应。
//    遵守应用程序的原始意图。
//    不注意OkHttp注入的头像If-None-Match。
//    允许短路和不通话Chain.proceed()。
//    允许重试并进行多次呼叫Chain.proceed()。

//    网络拦截器
//    能够对重定向和重试等中间响应进行操作。
//    不调用缓存的响应来短路网络。
//    观察数据，就像通过网络传输一样。
//    访问Connection该请求。


    /**
     * 添加dokit 的拦截器 通过字节码插入
     */
    public static void addDoKitIntercept(OkHttpClient client) {
        List<Interceptor> interceptors = new ArrayList<>(client.interceptors());
        List<Interceptor> networkInterceptors = new ArrayList<>(client.networkInterceptors());
        try {
            if (DoKitConstant.INSTANCE.getDOKIT_MODULE_ABILITIES().get("DoKit_MC") != null) {
                DokitAbility ability = DoKitConstant.INSTANCE.getDOKIT_MODULE_ABILITIES().get("DoKit_MC");
                Object interceptor = ability.getModuleFunctions().get("okhttp_interceptor");
                if (interceptor instanceof AbsDoKitInterceptor) {
                    interceptors.add((AbsDoKitInterceptor) interceptor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        interceptors.add(new DokitMockInterceptor());
        interceptors.add(new DokitLargePicInterceptor());
        interceptors.add(new DokitCapInterceptor());
        interceptors.add(new DokitExtInterceptor());
        networkInterceptors.add(new DokitWeakNetworkInterceptor());
        //需要用反射重新赋值 因为源码中创建了一个不可变的list
        ReflectUtils.reflect(client).field("interceptors", interceptors);
        ReflectUtils.reflect(client).field("networkInterceptors", networkInterceptors);
    }


}
