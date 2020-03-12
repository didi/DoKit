package com.didichuxing.foundation.net.rpc.http;

import com.didichuxing.doraemonkit.kit.network.rpc.RpcMockInterceptor;
import com.didichuxing.doraemonkit.kit.network.rpc.RpcMonitorInterceptor;
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
    private static boolean IS_INSTALL = false;

    public static void installInterceptor() {
        if (IS_INSTALL) {
            return;
        }
        try {
            //可能存在用户没有引入滴滴内部网络库的情况
            OkHttpRpc.OkHttpRpcInterceptor rpcMockInterceptor = new OkHttpRpc.OkHttpRpcInterceptor((new RpcMockInterceptor()));
            OkHttpRpc.OkHttpRpcInterceptor rpcMonitorInterceptor = new OkHttpRpc.OkHttpRpcInterceptor((new RpcMonitorInterceptor()));
            globalInterceptors.add(rpcMockInterceptor);
            globalInterceptors.add(rpcMonitorInterceptor);
            IS_INSTALL = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
