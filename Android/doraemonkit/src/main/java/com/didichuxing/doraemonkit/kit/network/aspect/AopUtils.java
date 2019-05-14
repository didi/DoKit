package com.didichuxing.doraemonkit.kit.network.aspect;

import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.HttpUrlConnectionProxy;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.HttpsUrlConnectionProxy;
import com.didichuxing.doraemonkit.kit.network.okhttp.DoraemonInterceptor;
import com.didichuxing.doraemonkit.kit.network.okhttp.DoraemonWeakNetworkInterceptor;

import java.net.HttpURLConnection;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * @desc:
 */
public class AopUtils {
    public static URLConnection URLOpenConnection(URLConnection connection) {
        if (!NetworkManager.isActive()) {
            return connection;
        }
        if (connection instanceof HttpURLConnection) {
            return new HttpUrlConnectionProxy((HttpURLConnection) connection);
        } else if (connection instanceof HttpsURLConnection) {
            return new HttpsUrlConnectionProxy((HttpsURLConnection) connection);
        } else {
            return connection;
        }
    }

    public static void addInterceptor(OkHttpClient.Builder builder) {
        // 判断当前是否已经添加了拦截器，如果已添加则返回
        for (Interceptor interceptor : builder.interceptors()) {
            if (interceptor instanceof DoraemonInterceptor) {
                return;
            }
        }
        builder.addNetworkInterceptor(new DoraemonWeakNetworkInterceptor())
                .addInterceptor(new DoraemonInterceptor());
    }
}
