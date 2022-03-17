package com.didichuxing.foundation.net.rpc.http;

import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.constant.DoKitModule;
import com.didichuxing.doraemonkit.kit.core.DoKitManager;
import com.didichuxing.doraemonkit.kit.core.DokitAbility;
import com.didichuxing.doraemonkit.kit.network.rpc.AbsDoKitRpcInterceptor;
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
        if (!DoKit.isInit()) {
            return;
        }

        try {
            List<Interceptor> interceptors = new ArrayList<>(client.interceptors());
            List<Interceptor> networkInterceptors = new ArrayList<>(client.networkInterceptors());
            DokitAbility.DokitModuleProcessor processor = DoKitManager.INSTANCE.getModuleProcessor(DoKitModule.MODULE_RPC_MC);
            if (processor != null) {
                Object interceptor = processor.values().get("rpc_interceptor");
                Object interceptorProxy = processor.values().get("rpc_proxy_interceptor");
                if (interceptor instanceof AbsDoKitRpcInterceptor) {
//                    noDuplicateAdd(interceptors, (AbsDoKitRpcInterceptor) interceptor);
                    noDuplicateAdd(interceptors, (AbsDoKitRpcInterceptor) interceptorProxy);
                }
            }
            noDuplicateAdd(interceptors, new RpcMockInterceptor());
            noDuplicateAdd(interceptors, new RpcCapInterceptor());
            noDuplicateAdd(networkInterceptors, new RpcWeakNetworkInterceptor());
            //需要用反射重新赋值 因为源码中创建了一个不可变的list
            ReflectUtils.reflect(client).field("interceptors", interceptors);
            ReflectUtils.reflect(client).field("networkInterceptors", networkInterceptors);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //list判断是否重复添加
    private static void noDuplicateAdd(List<Interceptor> interceptors, AbsDoKitRpcInterceptor interceptor) {
        boolean hasInterceptor = false;
        for (Interceptor i : interceptors) {
            if (i instanceof AbsDoKitRpcInterceptor) {
                if (((AbsDoKitRpcInterceptor) i).getTAG().equals(interceptor.getTAG())) {
                    hasInterceptor = true;
                    break;
                }
            }
        }
        if (!hasInterceptor) {
            interceptors.add(interceptor);
        }

    }
}
