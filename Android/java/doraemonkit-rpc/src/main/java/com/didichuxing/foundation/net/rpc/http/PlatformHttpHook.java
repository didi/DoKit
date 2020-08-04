package com.didichuxing.foundation.net.rpc.http;

import android.util.Log;

import com.blankj.utilcode.util.ReflectUtils;
import com.didichuxing.doraemonkit.kit.network.rpc.RpcMockInterceptor;
import com.didichuxing.doraemonkit.kit.network.rpc.RpcMonitorInterceptor;
import com.didichuxing.doraemonkit.kit.network.rpc.RpcWeakNetworkInterceptor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
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
public class PlatformHttpHook {
    public static List<Interceptor> globalInterceptors = new ArrayList<>();
    public static List<Interceptor> globalNetworkInterceptors = new ArrayList<>();
    private static boolean IS_INSTALL = false;

    public static void installInterceptor() {
        if (IS_INSTALL) {
            return;
        }
        try {
            //可能存在用户没有引入滴滴内部网络库的情况
            OkHttpRpc.OkHttpRpcInterceptor rpcMockInterceptor = new OkHttpRpc.OkHttpRpcInterceptor(new RpcMockInterceptor());
            OkHttpRpc.OkHttpRpcInterceptor rpcMonitorInterceptor = new OkHttpRpc.OkHttpRpcInterceptor(new RpcMonitorInterceptor());
            globalInterceptors.add(rpcMockInterceptor);
            globalInterceptors.add(rpcMonitorInterceptor);
            Interceptor weakNetworkInterceptor = new RpcWeakNetworkInterceptor();
            globalNetworkInterceptors.add(weakNetworkInterceptor);
            IS_INSTALL = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param builder        真实的对象为DidiHttpClient.Builder
     * @param didiHttpClient 真实的对象为DidiHttpClient
     */
    public static void performDidiHttpOneParamBuilderInit(Object builder, Object didiHttpClient) {
        try {
            if (builder instanceof DidiHttpClient.Builder) {
                DidiHttpClient.Builder localBuild = (DidiHttpClient.Builder) builder;

                //防止注入失败
                localBuild.interceptors().addAll(globalInterceptors);
                localBuild.networkInterceptors().addAll(globalNetworkInterceptors);
                //判断去重
                List<Interceptor> interceptors = removeDuplicate(localBuild.interceptors());
                List<Interceptor> networkInterceptors = removeDuplicate(localBuild.networkInterceptors());
                ReflectUtils.reflect(localBuild).field("interceptors", interceptors);
                ReflectUtils.reflect(localBuild).field("networkInterceptors", networkInterceptors);
                //Log.i("Doraemon", "====performDidiHttpOneParamBuilderInit===");
            }
        } catch (Exception e) {
            Log.i("Doraemon", "" + e.getMessage());
        }

    }

    /**
     * 保证顺序并去重
     *
     * @param list
     * @return
     */
    private static List<Interceptor> removeDuplicate(List<Interceptor> list) {
        //保证顺序并去重
        LinkedHashSet<Interceptor> h = new LinkedHashSet<>(list);
        list.clear();
        list.addAll(h);
        return list;
    }
}
