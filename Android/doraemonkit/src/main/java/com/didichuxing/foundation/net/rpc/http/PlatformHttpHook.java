package com.didichuxing.foundation.net.rpc.http;

import com.didichuxing.foundation.rpc.RpcInterceptor;

import java.util.ArrayList;
import java.util.List;

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
public class PlatformHttpHook {
    public static List<Interceptor> globalInterceptors = new ArrayList<>();

    public static void installInterceptor(Object interceptor) {
        if (interceptor instanceof RpcInterceptor) {
            OkHttpRpc.OkHttpRpcInterceptor rpcInterceptor = new OkHttpRpc.OkHttpRpcInterceptor((RpcInterceptor) interceptor);
            globalInterceptors.add(rpcInterceptor);
        }

    }
}
