package com.didichuxing.doraemonkit.aop

import android.util.Log
import com.blankj.utilcode.util.ReflectUtils
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DoraemonInterceptor
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DoraemonWeakNetworkInterceptor
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.LargePictureInterceptor
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.MockInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-13-10:40
 * 描    述：用来通过ASM在编译器进行hook
 * 修订历史：
 * ================================================
 */
internal object OkHttpHook {
    //    每个拦截器都有自己的相对优点。
    //    应用拦截器
    //    不需要担心中间响应，如重定向和重试。
    //    总是调用一次，即使从缓存提供HTTP响应。
    //    遵守应用程序的原始意图。
    //    不注意OkHttp注入的头像If-None-Match。
    //    允许短路和不通话Chain.proceed()。
    //    允许重试并进行多次呼叫Chain.proceed()。
    //    网络拦截器
    //    能够对重定向和重试等中间响应进行操作。
    //    不调用缓存的响应来短路网络。
    //    观察数据，就像通过网络传输一样。
    //    访问Connection该请求。
    @JvmField
    public val globalInterceptors: MutableList<Interceptor> = mutableListOf()


    @JvmField
    public val globalNetworkInterceptors: MutableList<Interceptor> = mutableListOf()


    private var IS_INSTALL = false

    fun installInterceptor() {
        if (IS_INSTALL) {
            return
        }
        try {
            //可能存在用户没有引入okhttp的情况
            globalInterceptors.add(MockInterceptor())
            globalInterceptors.add(LargePictureInterceptor())
            globalInterceptors.add(DoraemonInterceptor())
            globalNetworkInterceptors.add(DoraemonWeakNetworkInterceptor())
            IS_INSTALL = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * @param builder      真实的对象为okHttpClient.Builder
     * @param okHttpClient 真实的对象为okHttpClient
     */
    @JvmStatic
    fun performOkhttpOneParamBuilderInit(builder: Any?, okHttpClient: Any?) {
        try {
            if (builder is OkHttpClient.Builder) {
                val localBuild = builder
                val interceptors = removeDuplicate(localBuild.interceptors())
                val networkInterceptors = removeDuplicate(localBuild.networkInterceptors())
                ReflectUtils.reflect(localBuild).field("interceptors", interceptors)
                ReflectUtils.reflect(localBuild).field("networkInterceptors", networkInterceptors)
            }
        } catch (e: Exception) {
            Log.i("Doraemon", "" + e.message)
        }
    }

    /**
     * 保证顺序并去重
     *
     * @param list
     * @return
     */
    private fun removeDuplicate(list: MutableList<Interceptor>): MutableList<Interceptor> {
        //保证顺序并去重
        val h: LinkedHashSet<Interceptor> = LinkedHashSet(list)
        list.clear()
        list.addAll(h)
        return list
    }
}