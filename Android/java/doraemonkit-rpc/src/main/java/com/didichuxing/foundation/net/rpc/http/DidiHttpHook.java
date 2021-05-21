package com.didichuxing.foundation.net.rpc.http;

import com.didichuxing.doraemonkit.kit.network.rpc.RpcCapInterceptor;
import com.didichuxing.doraemonkit.kit.network.rpc.RpcMockInterceptor;
import com.didichuxing.doraemonkit.kit.network.rpc.RpcWeakNetworkInterceptor;
import com.didichuxing.doraemonkit.util.ReflectUtils;

import java.util.ArrayList;
import java.util.List;

import didihttp.DidiHttpClient;
import didihttp.Interceptor;


/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-13-10:40
 * 描    述：用来通过ASM在编译器进行hook
 * 修订历史：
 * ================================================
 */
public class DidiHttpHook {

    /**
     * 添加 didi 的拦截器 通过字节码插入
     */
    public static void addRpcIntercept(DidiHttpClient client) {
        List<Interceptor> interceptors = new ArrayList<>(client.interceptors());
        List<Interceptor> networkInterceptors = new ArrayList<>(client.networkInterceptors());
        interceptors.add(new RpcMockInterceptor());
        interceptors.add(new RpcCapInterceptor());
        networkInterceptors.add(new RpcWeakNetworkInterceptor());
        //需要用反射重新赋值 因为源码中创建了一个不可变的list
        ReflectUtils.reflect(client).field("interceptors", interceptors);
        ReflectUtils.reflect(client).field("networkInterceptors", networkInterceptors);
    }
}
