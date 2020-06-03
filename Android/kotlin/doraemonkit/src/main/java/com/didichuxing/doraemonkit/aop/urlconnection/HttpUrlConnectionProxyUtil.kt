package com.didichuxing.doraemonkit.aop.urlconnection

import com.didichuxing.doraemonkit.aop.urlconnection.ObsoleteUrlFactory.OkHttpURLConnection
import com.didichuxing.doraemonkit.aop.urlconnection.ObsoleteUrlFactory.OkHttpsURLConnection
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DoraemonInterceptor
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DoraemonWeakNetworkInterceptor
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.LargePictureInterceptor
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.MockInterceptor
import com.didichuxing.doraemonkit.okgo.DokitOkGo
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import java.net.URL
import java.net.URLConnection
import java.util.concurrent.TimeUnit

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-16-14:54
 * 描    述：ams 动态插入代码
 * 修订历史：
 * ================================================
 */
public object HttpUrlConnectionProxyUtil {
    //private static final String TAG = "HttpUrlConnectionProxyUtil";
    private val hosts = arrayOf("amap.com")

    @JvmStatic
    fun proxy(urlConnection: URLConnection): URLConnection {
        try {
            val host = HttpUrl.parse(urlConnection.url.toString())!!.host()
            return if (isIgnore(host)) {
                urlConnection
            } else createOkHttpURLConnection(urlConnection)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return urlConnection
    }

    @Throws(Exception::class)
    private fun createOkHttpURLConnection(urlConnection: URLConnection): URLConnection {
        val builder = OkHttpClient.Builder()
        //不需要再重复添加拦截器 因为已经通过字节码主如果拦截器了
        //addInterceptor(builder);
        val mClient = builder
                .retryOnConnectionFailure(true)
                .readTimeout(DokitOkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .writeTimeout(DokitOkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .connectTimeout(DokitOkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .build()
        val strUrl = urlConnection.url.toString()
        val url = URL(strUrl)
        val protocol = url.protocol.toLowerCase()
        if (protocol.equals("http", ignoreCase = true)) {
            return OkHttpURLConnection(url, mClient)
        }
        return if (protocol.equals("https", ignoreCase = true)) {
            OkHttpsURLConnection(url, mClient)
        } else urlConnection
    }

    private fun addInterceptor(builder: OkHttpClient.Builder) {
        // 判断当前是否已经添加了拦截器，如果已添加则返回
        for (interceptor in builder.interceptors()) {
            if (interceptor is MockInterceptor) {
                return
            }
        }
        builder //添加mock拦截器
                .addInterceptor(MockInterceptor()) //添加大图检测拦截器
                .addInterceptor(LargePictureInterceptor()) //添加dokit拦截器
                .addInterceptor(DoraemonInterceptor()) //添加弱网 拦截器
                .addNetworkInterceptor(DoraemonWeakNetworkInterceptor())
    }

    /**
     * 判断是否过滤指定的host
     *
     * @param host
     * @return
     */
    private fun isIgnore(host: String): Boolean {
        for (jumpHost in hosts) {
            if (host.contains(jumpHost)) {
                return true
            }
        }
        return false
    }
}