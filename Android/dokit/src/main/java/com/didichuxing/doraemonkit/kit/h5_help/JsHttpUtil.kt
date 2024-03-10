package com.didichuxing.doraemonkit.kit.h5_help

import android.text.TextUtils
import android.webkit.WebResourceResponse
import com.didichuxing.doraemonkit.util.ConvertUtils
import com.didichuxing.doraemonkit.util.ToastUtils
import com.didichuxing.doraemonkit.okhttp_api.OkHttpWrap
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.h5_help.bean.JsRequestBean
import com.didichuxing.doraemonkit.kit.network.NetworkManager
import com.didichuxing.doraemonkit.kit.network.bean.WhiteHostBean
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager
import com.didichuxing.doraemonkit.kit.network.room_db.MockInterceptApiBean
import com.didichuxing.doraemonkit.kit.network.room_db.MockTemplateApiBean
import com.didichuxing.doraemonkit.kit.network.utils.bodyContent
import com.didichuxing.doraemonkit.util.DoKitCommUtil
import com.didichuxing.doraemonkit.util.LogHelper
import okhttp3.*
import org.json.JSONObject

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/9/1-11:14
 * 描    述：
 * 修订历史：
 * ================================================
 */
object JsHttpUtil {
    val TAG = "JsHttpUtil"

    /**
     * 将request query 转化成json字符串
     *
     * @return
     */
    fun transformQuery(query: String?): String {
        var json = ""
        if (query.isNullOrBlank()) {
            return json
        }
        try {
            //query 类似 ccc=ccc&ddd=ddd
            json = DoKitCommUtil.param2Json(query)
            //测试是否是json字符串
            JSONObject(json)
        } catch (e: Exception) {
            //e.printStackTrace();
            json = DokitDbManager.IS_NOT_NORMAL_QUERY_PARAMS
            //LogHelper.e(TAG, "===query json====>" + json);
        }
        return json
    }


    /**
     * 将request body 转化成json字符串
     *
     * @return
     */
    fun transformRequestBody(
        method: String?,
        requestBody: String?,
        headers: MutableMap<String?, String?>?
    ): String {
        //form :"application/x-www-form-urlencoded"
        //json :"application/json;"
        var json = ""
        if (method.equals("GET", true) || requestBody.isNullOrBlank()) {
            return json
        }

        try {

            headers?.let {
                val contentType = it["Content-Type"]
                if (contentType.isNullOrBlank()) {
                    return json
                }

                //表单类型的post
                if (contentType.contains(DokitDbManager.MEDIA_TYPE_FORM)) {
                    //类似 ccc=ccc&ddd=ddd
                    json = DoKitCommUtil.param2Json(requestBody)
                    //测试是否是json字符串
                    JSONObject(json)
                } else if (contentType.contains(DokitDbManager.MEDIA_TYPE_JSON)) {
                    json = requestBody
                    JSONObject(json)
                } else {
                    json = DokitDbManager.IS_NOT_NORMAL_BODY_PARAMS
                }
            }
        } catch (e: java.lang.Exception) {
            //e.printStackTrace();
            json = ""
            LogHelper.e(
                TAG, "===body json====>$json"
            )
        }
        return json
    }

    /**
     * 返回null 即代表返回原来的数据
     */
    fun matchedNormalInterceptRule(
        url: HttpUrl,
        path: String,
        interceptMatchedId: String,
        templateMatchedId: String,
        oldRequest: Request,
        oldResponse: Response,
        okHttpClient: OkHttpClient
    ): WebResourceResponse? {
        //判断是否需要重定向数据接口
        //http https

        //判断是否需要重定向数据接口
        //http https
        val scheme = OkHttpWrap.toScheme(url)
        val interceptApiBean = DokitDbManager.getInstance().getInterceptApiByIdInMap(
            path,
            interceptMatchedId,
            DokitDbManager.FROM_SDK_OTHER
        )
        if (interceptApiBean == null) {
            matchedTemplateRule(oldResponse, path, templateMatchedId)
            return null
        }

        interceptApiBean as MockInterceptApiBean
        val selectedSceneId = interceptApiBean.selectedSceneId
        //开关是否被打开
        //开关是否被打开
        if (!interceptApiBean.isOpen) {
            matchedTemplateRule(oldResponse, path, templateMatchedId)
            return null
        }

        //判断是否有选中的场景

        //判断是否有选中的场景
        if (TextUtils.isEmpty(selectedSceneId)) {
            matchedTemplateRule(oldResponse, path, templateMatchedId)
            return null
        }
        val sb = StringBuilder()
        val newUrl: String
        newUrl = if (NetworkManager.MOCK_SCHEME_HTTP.contains(scheme.toLowerCase())) {
            sb.append(NetworkManager.MOCK_SCHEME_HTTP).append(NetworkManager.MOCK_HOST)
                .append("/api/app/scene/").append(selectedSceneId).toString()
        } else {
            sb.append(NetworkManager.MOCK_SCHEME_HTTPS).append(NetworkManager.MOCK_HOST)
                .append("/api/app/scene/").append(selectedSceneId).toString()
        }


        val newRequest = Request.Builder()
            .method("GET", null)
            .url(newUrl).build()
        //需要提前关闭数据流 不然在某些场景下会报错
        oldResponse.close()
        val newResponse: Response = okHttpClient.newCall(newRequest).execute()
        if (OkHttpWrap.toResponseCode(newResponse) == 200) {
            //拦截命中提示
            ToastUtils.showShort("接口别名:==" + interceptApiBean.mockApiName + "==已被拦截")
            //判断新的response是否有数据
            return if (OkHttpWrap.toResponseBody(newResponse) != null) {
                matchedTemplateRule(newResponse, path, templateMatchedId)
                createNormalWebResourceResponse(newResponse)
            } else {
                matchedTemplateRule(oldResponse, path, templateMatchedId)
                null
            }
        }
        matchedTemplateRule(oldResponse, path, templateMatchedId)
        return null
    }

    private fun createNormalWebResourceResponse(response: Response): WebResourceResponse {
        val bodyContent = response.bodyContent()
        try {
            //mimeType 会影响js的数据展现形式
            JSONObject(bodyContent)
            return WebResourceResponse(
                "application/json",
                "UTF-8",
                ConvertUtils.string2InputStream(bodyContent, "UTF-8")
            )
        } catch (e: Exception) {
            return WebResourceResponse(
                response.header("Content-Type", "application/json"),
                "UTF-8",
                ConvertUtils.string2InputStream(bodyContent, "UTF-8")
            )
        }


    }

    /**
     * 返回null 即代表返回原来的数据
     */
    fun matchedX5InterceptRule(
        url: HttpUrl,
        path: String,
        interceptMatchedId: String,
        templateMatchedId: String,
        oldRequest: Request,
        oldResponse: Response,
        okHttpClient: OkHttpClient
    ): com.tencent.smtt.export.external.interfaces.WebResourceResponse? {
        //判断是否需要重定向数据接口
        //http https

        //判断是否需要重定向数据接口
        //http https
        val scheme = OkHttpWrap.toScheme(url)
        val interceptApiBean = DokitDbManager.getInstance().getInterceptApiByIdInMap(
            path,
            interceptMatchedId,
            DokitDbManager.FROM_SDK_OTHER
        )
        if (interceptApiBean == null) {
            matchedTemplateRule(oldResponse, path, templateMatchedId)
            return null
        }

        interceptApiBean as MockInterceptApiBean
        val selectedSceneId = interceptApiBean.selectedSceneId
        //开关是否被打开
        //开关是否被打开
        if (!interceptApiBean.isOpen) {
            matchedTemplateRule(oldResponse, path, templateMatchedId)
            return null
        }

        //判断是否有选中的场景

        //判断是否有选中的场景
        if (TextUtils.isEmpty(selectedSceneId)) {
            matchedTemplateRule(oldResponse, path, templateMatchedId)
            return null
        }
        val sb = StringBuilder()
        val newUrl: String
        newUrl = if (NetworkManager.MOCK_SCHEME_HTTP.contains(scheme.toLowerCase())) {
            sb.append(NetworkManager.MOCK_SCHEME_HTTP).append(NetworkManager.MOCK_HOST)
                .append("/api/app/scene/").append(selectedSceneId).toString()
        } else {
            sb.append(NetworkManager.MOCK_SCHEME_HTTPS).append(NetworkManager.MOCK_HOST)
                .append("/api/app/scene/").append(selectedSceneId).toString()
        }


        val newRequest = Request.Builder()
            .method("GET", null)
            .url(newUrl).build()
        //需要提前关闭数据流 不然在某些场景下会报错
        oldResponse.close()
        val newResponse: Response = okHttpClient.newCall(newRequest).execute()
        if (OkHttpWrap.toResponseCode(newResponse) == 200) {
            //拦截命中提示
            ToastUtils.showShort("接口别名:==" + interceptApiBean.mockApiName + "==已被拦截")
            //判断新的response是否有数据
            return if (OkHttpWrap.toResponseBody(newResponse) != null) {
                matchedTemplateRule(newResponse, path, templateMatchedId)
                createX5WebResourceResponse(newResponse)
            } else {
                matchedTemplateRule(oldResponse, path, templateMatchedId)
                null
            }
        }
        matchedTemplateRule(oldResponse, path, templateMatchedId)
        return null
    }

    private fun createX5WebResourceResponse(response: Response): com.tencent.smtt.export.external.interfaces.WebResourceResponse {
        val bodyContent = response.bodyContent()
        try {
            //mimeType 会影响js的数据展现形式
            JSONObject(bodyContent)
            return com.tencent.smtt.export.external.interfaces.WebResourceResponse(
                "application/json",
                "UTF-8",
                ConvertUtils.string2InputStream(bodyContent, "UTF-8")
            )
        } catch (e: Exception) {
            return com.tencent.smtt.export.external.interfaces.WebResourceResponse(
                response.header("Content-Type", "application/json"),
                "UTF-8",
                ConvertUtils.string2InputStream(bodyContent, "UTF-8")
            )
        }

    }


    /**
     *是否命中模板功能
     */
    fun matchedTemplateRule(response: Response, path: String, templateMatchedId: String) {

        //命中模板规则
        if (TextUtils.isEmpty(templateMatchedId)) {
            return
        }
        val templateApiBean = DokitDbManager.getInstance().getTemplateApiByIdInMap(
            path,
            templateMatchedId,
            DokitDbManager.FROM_SDK_OTHER
        )
        //LogHelper.i("MOCK_TEMPLATE", "path=====>" + path + "isOpen===>" + templateApiBean.isOpen());
        //LogHelper.i("MOCK_TEMPLATE", "path=====>" + path + "isOpen===>" + templateApiBean.isOpen());
        templateApiBean?.let {
            it as MockTemplateApiBean
            if (it.isOpen) {
                //保存老的response 数据到数据库
                saveResponse2DB(response, it)
            }
        }

    }

    /**
     * 保存匹配中的数据到本地数据库
     *
     * @param response
     * @param mockApi
     * @throws Exception
     */
    private fun saveResponse2DB(response: Response, mockApi: MockTemplateApiBean) {
        if (OkHttpWrap.toResponseCode(response) != 200) {
            return
        }
        if (OkHttpWrap.toResponseBody(response) == null) {
            return
        }
        try {
            val host = OkHttpWrap.toResponseHost(response)
            //LogHelper.i(TAG, "host====>" + host);
            //这里不能直接使用response.body().string()的方式输出日志
            //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
            //个新的response给应用层处理
            val responseBody = response.peekBody(Long.MAX_VALUE)
            val strResponseBody = responseBody.string()
            if (TextUtils.isEmpty(strResponseBody)) {
                return
            }
            if (host == NetworkManager.MOCK_HOST) {
                mockApi.responseFrom = MockTemplateApiBean.RESPONSE_FROM_MOCK
            } else {
                mockApi.responseFrom = MockTemplateApiBean.RESPONSE_FROM_REAL
            }
            mockApi.strResponse = strResponseBody
            //更新本地数据库
            DokitDbManager.getInstance().updateTemplateApi(mockApi)
            //拦截命中提示
            ToastUtils.showShort("模板别名:==" + mockApi.mockApiName + "==已被保存")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun createOkHttpRequest(requestBean: JsRequestBean, userAgent: String): Request {
        requestBean.headers?.let {
            if (!it.containsKey("content-type")) {
                it["content-type"] = "application/json"
            }

            if (!it.containsKey("User-Agent")) {
                it["User-Agent"] = userAgent
            }
        }
        val builder = Headers.Builder()
        requestBean.headers?.forEach {
            builder.add(it.key!!, it.value!!)
        }


        val headers = builder.build()
        return when (requestBean.method?.toUpperCase()) {
            "GET" -> {
                Request.Builder()
                    .url(requestBean.url!!)
                    .headers(headers)
                    .get()
                    .build()
            }
            "POST" -> {
                var contentType: String? = ""
                contentType = requestBean.headers?.get("Content-Type")
                if (contentType.isNullOrBlank()) {
                    contentType = requestBean.headers?.get("content-type")
                }


                val requestBody =
                    OkHttpWrap.toRequestBody(requestBean.body, OkHttpWrap.toMediaType(contentType))

                Request.Builder()
                    .url(requestBean.url!!)
                    .headers(headers)
                    .post(requestBody!!)
                    .build()
            }
            else -> {
                Request.Builder()
                    .url(requestBean.url!!)
                    .headers(headers)
                    .get()
                    .build()
            }
        }

    }

    /**
     * 是否命中白名单规则
     *
     * @return bool
     */
    fun matchWhiteHost(request: Request): Boolean {
        val whiteHostBeans: List<WhiteHostBean> = DoKitManager.WHITE_HOSTS
        if (whiteHostBeans.isEmpty()) {
            return true
        }

        for (whiteHostBean in whiteHostBeans) {
            if (TextUtils.isEmpty(whiteHostBean.host)) {
                continue
            }
            val realHost = OkHttpWrap.toRequestHost(request)
            //正则判断
            if (whiteHostBean.host.equals(realHost, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

}
