package com.didichuxing.doraemonkit.kit.network.okhttp.interceptor

import com.didichuxing.doraemonkit.kit.weaknetwork.WeakNetworkManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 用于模拟弱网的拦截器
 *
 *
 * Created by jint on 2019-05-09 16:29
 *
 * @author didi
 */
class DoraemonWeakNetworkInterceptor : Interceptor {
    private val TAG = "DoraemonWeakNetworkInterceptor"

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!WeakNetworkManager.get().isActive) {
            val request = chain.request()
            return chain.proceed(request)
        }
        return when (WeakNetworkManager.get().type) {
            WeakNetworkManager.TYPE_TIMEOUT ->//超时
                WeakNetworkManager.get().simulateTimeOut(chain)
            WeakNetworkManager.TYPE_SPEED_LIMIT ->//限速
                WeakNetworkManager.get().simulateSpeedLimit(chain)
            else ->//断网
                WeakNetworkManager.get().simulateOffNetwork(chain)
        }
    }
}