/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.didichuxing.doraemonkit.okgo

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.didichuxing.doraemonkit.okgo.cache.CacheEntity
import com.didichuxing.doraemonkit.okgo.cache.CacheMode
import com.didichuxing.doraemonkit.okgo.cookie.CookieJarImpl
import com.didichuxing.doraemonkit.okgo.https.HttpsUtils
import com.didichuxing.doraemonkit.okgo.interceptor.HttpLoggingInterceptor
import com.didichuxing.doraemonkit.okgo.model.HttpHeaders
import com.didichuxing.doraemonkit.okgo.model.HttpParams
import com.didichuxing.doraemonkit.okgo.request.*
import com.didichuxing.doraemonkit.okgo.utils.HttpUtils
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import java.util.logging.Level

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/12
 * 描    述：dokit内部网络请求的专用 不建议外部使用
 * 修订历史：
 * ================================================
 */
internal class DokitOkGo private constructor() {
    private var context //全局上下文
            : Application? = null
    val delivery //用于在主线程执行的调度器
            : Handler
    private var okHttpClient //ok请求的客户端
            : OkHttpClient

    /**
     * 获取全局公共请求参数
     */
    var commonParams //全局公共请求参数
            : HttpParams? = null
        private set

    /**
     * 获取全局公共请求头
     */
    var commonHeaders //全局公共请求头
            : HttpHeaders? = null
        private set

    /**
     * 超时重试次数
     */
    var retryCount //全局超时重试次数
            : Int
        private set

    /**
     * 获取全局的缓存模式
     */
    var cacheMode //全局缓存模式
            : CacheMode
        private set

    /**
     * 获取全局的缓存过期时间
     */
    var cacheTime //全局缓存过期时间,默认永不过期
            : Long
        private set

    private object OkGoHolder {
        var holder = DokitOkGo()
    }

    /**
     * 必须在全局Application先调用，获取context上下文，否则缓存无法使用
     */
    fun init(app: Application?): DokitOkGo {
        context = app
        return this
    }

    /**
     * 获取全局上下文
     */
    fun getContext(): Context? {
        HttpUtils.checkNotNull(
            context,
            "please call OkGo.getInstance().init() first in application!"
        )
        return context
    }

    fun getOkHttpClient(): OkHttpClient {
        HttpUtils.checkNotNull(
            okHttpClient,
            "please call OkGo.getInstance().setOkHttpClient() first in application!"
        )
        return okHttpClient
    }

    /**
     * 必须设置
     */
    fun setOkHttpClient(okHttpClient: OkHttpClient): DokitOkGo {
        HttpUtils.checkNotNull(okHttpClient, "okHttpClient == null")
        this.okHttpClient = okHttpClient
        return this
    }

    /**
     * 获取全局的cookie实例
     */
    val cookieJar: CookieJarImpl
        get() = okHttpClient.cookieJar() as CookieJarImpl

    /**
     * 超时重试次数
     */
    fun setRetryCount(retryCount: Int): DokitOkGo {
        require(retryCount >= 0) { "retryCount must > 0" }
        this.retryCount = retryCount
        return this
    }

    /**
     * 全局的缓存模式
     */
    fun setCacheMode(cacheMode: CacheMode): DokitOkGo {
        this.cacheMode = cacheMode
        return this
    }

    /**
     * 全局的缓存过期时间
     */
    fun setCacheTime(cacheTime: Long): DokitOkGo {
        var cacheTime = cacheTime
        if (cacheTime <= -1) cacheTime = CacheEntity.CACHE_NEVER_EXPIRE
        this.cacheTime = cacheTime
        return this
    }

    /**
     * 添加全局公共请求参数
     */
    fun addCommonParams(commonParams: HttpParams?): DokitOkGo {
        if (this.commonParams == null) this.commonParams =
            HttpParams()
        commonParams!!.put(commonParams)
        return this
    }

    /**
     * 添加全局公共请求参数
     */
    fun addCommonHeaders(commonHeaders: HttpHeaders?): DokitOkGo {
        if (this.commonHeaders == null) this.commonHeaders =
            HttpHeaders()
        commonHeaders!!.put(commonHeaders)
        return this
    }

    /**
     * 根据Tag取消请求
     */
    fun cancelTag(tag: Any?) {
        if (tag == null) return
        for (call in getOkHttpClient().dispatcher().queuedCalls()) {
            if (tag == call.request().tag()) {
                call.cancel()
            }
        }
        for (call in getOkHttpClient().dispatcher().runningCalls()) {
            if (tag == call.request().tag()) {
                call.cancel()
            }
        }
    }

    /**
     * 取消所有请求请求
     */
    fun cancelAll() {
        for (call in getOkHttpClient().dispatcher().queuedCalls()) {
            call.cancel()
        }
        for (call in getOkHttpClient().dispatcher().runningCalls()) {
            call.cancel()
        }
    }

    companion object {
        const val DEFAULT_MILLISECONDS: Long = 60000 //默认的超时时间
        @JvmField
        var REFRESH_TIME: Long = 300 //回调刷新时间（单位ms）
        @JvmStatic
        val instance: DokitOkGo
            get() = OkGoHolder.holder

        /**
         * get请求
         */
        @JvmStatic
        operator fun <T> get(url: String?): GetRequest<T> {
            return GetRequest(url)
        }

        /**
         * post请求
         */
        @JvmStatic
        fun <T> post(url: String?): PostRequest<T> {
            return PostRequest(url)
        }

        /**
         * put请求
         */
        fun <T> put(url: String?): PutRequest<T> {
            return PutRequest(url)
        }

        /**
         * head请求
         */
        fun <T> head(url: String?): HeadRequest<T> {
            return HeadRequest(url)
        }

        /**
         * delete请求
         */
        fun <T> delete(url: String?): DeleteRequest<T> {
            return DeleteRequest(url)
        }

        /**
         * options请求
         */
        fun <T> options(url: String?): OptionsRequest<T> {
            return OptionsRequest(url)
        }

        /**
         * patch请求
         */
        @JvmStatic
        fun <T> patch(url: String?): PatchRequest<T> {
            return PatchRequest(url)
        }

        /**
         * trace请求
         */
        fun <T> trace(url: String?): TraceRequest<T> {
            return TraceRequest(url)
        }

        /**
         * 根据Tag取消请求
         */
        fun cancelTag(client: OkHttpClient?, tag: Any?) {
            if (client == null || tag == null) return
            for (call in client.dispatcher().queuedCalls()) {
                if (tag == call.request().tag()) {
                    call.cancel()
                }
            }
            for (call in client.dispatcher().runningCalls()) {
                if (tag == call.request().tag()) {
                    call.cancel()
                }
            }
        }

        /**
         * 取消所有请求请求
         */
        fun cancelAll(client: OkHttpClient?) {
            if (client == null) return
            for (call in client.dispatcher().queuedCalls()) {
                call.cancel()
            }
            for (call in client.dispatcher().runningCalls()) {
                call.cancel()
            }
        }
    }

    init {
        delivery = Handler(Looper.getMainLooper())
        retryCount = 3
        cacheTime = CacheEntity.CACHE_NEVER_EXPIRE
        cacheMode = CacheMode.NO_CACHE
        val builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor("OkGo")
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY)
        loggingInterceptor.setColorLevel(Level.INFO)
        builder.addInterceptor(loggingInterceptor)
        //builder.retryOnConnectionFailure(true);
        builder.readTimeout(
            DEFAULT_MILLISECONDS,
            TimeUnit.MILLISECONDS
        )
        builder.writeTimeout(
            DEFAULT_MILLISECONDS,
            TimeUnit.MILLISECONDS
        )
        builder.connectTimeout(
            DEFAULT_MILLISECONDS,
            TimeUnit.MILLISECONDS
        )
        val sslParams = HttpsUtils.getSslSocketFactory()
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
        builder.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier)
        okHttpClient = builder.build()
    }
}