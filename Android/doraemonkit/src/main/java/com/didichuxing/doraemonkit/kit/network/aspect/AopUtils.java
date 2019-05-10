package com.didichuxing.doraemonkit.kit.network.aspect;

import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.HttpUrlConnectionProxy;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.HttpsUrlConnectionProxy;
import com.didichuxing.doraemonkit.kit.network.okhttp.DoraemonInterceptor;
import com.didichuxing.doraemonkit.kit.network.okhttp.DoraemonWeakNetworkInterceptor;

import java.net.HttpURLConnection;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

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
        builder.addNetworkInterceptor(new DoraemonWeakNetworkInterceptor())
                .addInterceptor(new DoraemonInterceptor());
    }
}
