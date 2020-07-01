package com.didichuxing.doraemonkit.kit.network.okhttp.interceptor

import android.text.TextUtils
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.kit.network.NetworkManager
import com.didichuxing.doraemonkit.kit.network.okhttp.bean.NetworkRecord
import com.didichuxing.doraemonkit.kit.network.okhttp.bean.WhiteHostBean
import com.didichuxing.doraemonkit.kit.network.okhttp.ForwardingResponseBody
import com.didichuxing.doraemonkit.kit.network.okhttp.InterceptorUtil
import com.didichuxing.doraemonkit.kit.network.okhttp.OkHttpInspectorRequest
import com.didichuxing.doraemonkit.kit.network.okhttp.OkHttpInspectorResponse
import com.didichuxing.doraemonkit.kit.network.okhttp.core.DefaultResponseHandler
import com.didichuxing.doraemonkit.kit.network.okhttp.core.NetworkInterpreter
import com.didichuxing.doraemonkit.kit.network.okhttp.core.RequestBodyHelper
import com.didichuxing.doraemonkit.util.LogHelper.e
import okhttp3.*
import java.io.IOException
import java.io.InputStream


/**
 * 抓包拦截器
 */
class DoraemonInterceptor : Interceptor {
    val TAG = "DoraemonInterceptor"

    private val mNetworkInterpreter: NetworkInterpreter = NetworkInterpreter.get()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!NetworkManager.isActive) {
            val request = chain.request()
            return try {
                chain.proceed(request)
            } catch (e: Exception) {
                val responseBody = ResponseBody.create(MediaType.parse("application/json;charset=utf-8"), "" + e.message)
                Response.Builder()
                        .code(400)
                        .message(String.format("%s==>Exception:%s", chain.request().url().host(), e.message))
                        .request(request)
                        .body(responseBody)
                        .protocol(Protocol.HTTP_1_1)
                        .build()
            }
        }
        val request = chain.request()
        val requestId: Int = mNetworkInterpreter.nextRequestId()
        var response: Response
        response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            e(TAG, "e===>" + e.message)
            mNetworkInterpreter.httpExchangeFailed(requestId, e.toString())
            val responseBody = ResponseBody.create(MediaType.parse("application/json;charset=utf-8"), "" + e.message)
            return Response.Builder()
                    .code(400)
                    .message(String.format("%s==>Exception:%s", chain.request().url().host(), e.message))
                    .request(request)
                    .body(responseBody)
                    .protocol(Protocol.HTTP_1_1)
                    .build()
        }
        val strContentType = response.header("Content-Type")
        //如果是图片则不进行拦截
        if (InterceptorUtil.isImg(strContentType)) {
            return response
        }
        //白名单过滤
        if (!matchWhiteHost(request)) {
            return response
        }
        val requestBodyHelper = RequestBodyHelper()
        val inspectorRequest = OkHttpInspectorRequest(requestId, request, requestBodyHelper)
        val record: NetworkRecord = mNetworkInterpreter.createRecord(requestId, inspectorRequest)
        val inspectorResponse: NetworkInterpreter.InspectorResponse = OkHttpInspectorResponse(
                requestId,
                request,
                response)
        mNetworkInterpreter.fetchResponseInfo(record, inspectorResponse)
        val body = response.body()
        var responseStream: InputStream? = null
        var contentType: MediaType? = null
        if (body != null) {
            contentType = body.contentType()
            responseStream = body.byteStream()
        }
        responseStream = mNetworkInterpreter.interpretResponseStream(
                contentType?.toString(),
                responseStream,
                DefaultResponseHandler(mNetworkInterpreter, requestId, record))

        if (responseStream != null && body != null) {
            response = response.newBuilder()
                    .body(ForwardingResponseBody(body, responseStream))
                    .build()
        }
        return response
    }

    /**
     * 是否命中白名单规则
     *
     * @return bool
     */
    private fun matchWhiteHost(request: Request): Boolean {
        val whiteHostBeans: List<WhiteHostBean> = DokitConstant.WHITE_HOSTS
        if (whiteHostBeans.isEmpty()) {
            return true
        }
        for (whiteHostBean in whiteHostBeans) {
            if (TextUtils.isEmpty(whiteHostBean.host)) {
                continue
            }
            val realHost = request.url().host()
            //正则判断
            if (whiteHostBean.host.equals(realHost, ignoreCase = true)) {
                return true
            }
        }
        return false
    }


}