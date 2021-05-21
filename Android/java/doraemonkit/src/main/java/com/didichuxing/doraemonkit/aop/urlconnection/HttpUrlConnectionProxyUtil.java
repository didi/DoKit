package com.didichuxing.doraemonkit.aop.urlconnection;

import android.net.Uri;

import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DokitCapInterceptor;
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DokitExtInterceptor;
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DokitWeakNetworkInterceptor;
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DokitLargePicInterceptor;
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DokitMockInterceptor;

import java.net.URL;
import java.net.URLConnection;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-16-14:54
 * 描    述：ams 动态插入代码
 * 修订历史：
 * ================================================
 */
public class HttpUrlConnectionProxyUtil {
    //private static final String TAG = "HttpUrlConnectionProxyUtil";
    private static String[] hosts = new String[]{"amap.com"};

    public static URLConnection proxy(URLConnection urlConnection) {
        try {
            String host = HttpUrl.parse(urlConnection.getURL().toString()).host();
            if (isIgnore(host)) {
                return urlConnection;
            }
            return createOkHttpURLConnection(urlConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlConnection;
    }


    private static URLConnection createOkHttpURLConnection(URLConnection urlConnection) throws Exception {

//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        //不需要再重复添加拦截器 因为已经通过字节码主如果拦截器了
//        //addInterceptor(builder);
//        OkHttpClient mClient = builder
//                .retryOnConnectionFailure(true)
//                .readTimeout(DokitOkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
//                .writeTimeout(DokitOkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
//                .connectTimeout(DokitOkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
//                .build();

        //对url进行encode
        String strUrl = encodeUrl(urlConnection.getURL().toString());
        //Log.i("decode", decodeUrl(strUrl));
        URL url = new URL(strUrl);
        String protocol = url.getProtocol().toLowerCase();
        if (protocol.equalsIgnoreCase("http")) {
            return new ObsoleteUrlFactory.OkHttpURLConnection(url, OkhttpClientUtil.INSTANCE.getOkhttpClient());
        }

        if (protocol.equalsIgnoreCase("https")) {
            return new ObsoleteUrlFactory.OkHttpsURLConnection(url, OkhttpClientUtil.INSTANCE.getOkhttpClient());
        }

        return urlConnection;

    }

    public static String encodeUrl(String url) {
        return Uri.encode(url, "-![.:/,%?&=]");
    }

    public static String decodeUrl(String url) {
        return Uri.decode(url);
    }

    private static void addInterceptor(OkHttpClient.Builder builder) {
        // 判断当前是否已经添加了拦截器，如果已添加则返回
        for (Interceptor interceptor : builder.interceptors()) {
            if (interceptor instanceof DokitMockInterceptor) {
                return;
            }
        }

        builder
                //添加mock拦截器
                .addInterceptor(new DokitMockInterceptor())
                //添加大图检测拦截器
                .addInterceptor(new DokitLargePicInterceptor())
                //添加dokit拦截器
                .addInterceptor(new DokitCapInterceptor())
                //添加弱网 拦截器
                .addNetworkInterceptor(new DokitWeakNetworkInterceptor())
                // 添加扩展拦截器
                .addInterceptor(new DokitExtInterceptor());
    }

    /**
     * 判断是否过滤指定的host
     *
     * @param host
     * @return
     */
    private static boolean isIgnore(String host) {
        for (String jumpHost : hosts) {
            if (host.contains(jumpHost)) {
                return true;
            }
        }
        return false;
    }
}
