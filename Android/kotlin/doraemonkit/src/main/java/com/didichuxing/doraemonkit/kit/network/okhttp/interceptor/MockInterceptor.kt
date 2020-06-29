package com.didichuxing.doraemonkit.kit.network.okhttp.interceptor

import android.text.TextUtils
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.constant.NetWorkConstant
import com.didichuxing.doraemonkit.kit.health.AppHealthInfoUtil
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo
import com.didichuxing.doraemonkit.kit.network.NetworkManager
import com.didichuxing.doraemonkit.kit.network.okhttp.InterceptorUtil.isImg
import com.didichuxing.doraemonkit.kit.network.okhttp.room_db.DokitDbManager
import com.didichuxing.doraemonkit.kit.network.okhttp.room_db.MockInterceptApiBean
import com.didichuxing.doraemonkit.kit.network.okhttp.room_db.MockTemplateApiBean
import com.didichuxing.doraemonkit.util.DokitUtil.param2Json
import com.didichuxing.doraemonkit.util.DokitUtil.requestBodyToString
import com.didichuxing.doraemonkit.util.LogHelper.e
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.URLDecoder
import java.util.*


/**
 * @author jintai
 * @desc: 接口mock拦截器
 */
class MockInterceptor : Interceptor {
    val TAG = "MockInterceptor"


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest = chain.request()
        val oldResponse = chain.proceed(oldRequest)
        val contentType = oldResponse.header("Content-Type")
        //如果是图片则不进行拦截
        if (isImg(contentType)) {
            return oldResponse
        }
        val url = oldRequest.url()
        val host = url.host()
        //如果是mock平台的接口则不进行拦截
        if (host.equals(NetWorkConstant.MOCK_HOST, ignoreCase = true)) {
            return oldResponse
        }

        //path  /test/upload/img
        val path = URLDecoder.decode(url.encodedPath(), "utf-8")
        val queries = url.query()
        val jsonQuery = transformQuery(queries)
        val jsonRequestBody = transformRequestBody(oldRequest.body())
        //LogHelper.i(TAG, "realJsonQuery===>" + jsonQuery);
        //LogHelper.i(TAG, "realJsonRequestBody===>" + jsonRequestBody);
        val interceptMatchedId: String = DokitDbManager.instance.isMockMatched(path, jsonQuery, jsonRequestBody, DokitDbManager.MOCK_API_INTERCEPT, DokitDbManager.FROM_SDK_OTHER)
        val templateMatchedId: String = DokitDbManager.instance.isMockMatched(path, jsonQuery, jsonRequestBody, DokitDbManager.MOCK_API_TEMPLATE, DokitDbManager.FROM_SDK_OTHER)
        try {
            //网络的健康体检功能 统计流量大小
            if (DokitConstant.APP_HEALTH_RUNNING) {
                addNetWokInfoInAppHealth(oldRequest, oldResponse)
            }

            //是否命中拦截规则
            if (!TextUtils.isEmpty(interceptMatchedId)) {
                return matchedInterceptRule(url, path, interceptMatchedId, templateMatchedId, oldRequest, oldResponse, chain)
            }

            //是否命中模板规则
            matchedTemplateRule(oldResponse, path, templateMatchedId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return oldResponse
    }


    val MEDIA_TYPE_FORM = "application/x-www-form-urlencoded"
    val MEDIA_TYPE_JSON = "application/json"


    /**
     * 将request query 转化成json字符串
     *
     * @return
     */
    private fun transformQuery(query: String?): String {
        var json = ""
        if (TextUtils.isEmpty(query)) {
            return json
        }
        try {
            //query 类似 ccc=ccc&ddd=ddd
            json = param2Json(query!!)
            //测试是否是json字符串
            JSONObject(json)
        } catch (e: Exception) {
            //e.printStackTrace();
            json = NOT_STRING_CONTENT_FLAG
            //LogHelper.e(TAG, "===query json====>" + json);
        }
        return json
    }


    /**
     * 将request body 转化成json字符串
     *
     * @return
     */
    private fun transformRequestBody(requestBody: RequestBody?): String {
        //form :"application/x-www-form-urlencoded"
        //json :"application/json;"
        var json = ""
        if (requestBody == null || requestBody.contentType() == null) {
            return json
        }
        try {
            val strBody = requestBodyToString(requestBody)
            if (TextUtils.isEmpty(strBody)) {
                return ""
            }
            json = if (requestBody.contentType().toString().toLowerCase().contains(MEDIA_TYPE_FORM)) {
                val form = requestBodyToString(requestBody)
                //类似 ccc=ccc&ddd=ddd
                param2Json(form)
            } else if (requestBody.contentType().toString().toLowerCase().contains(MEDIA_TYPE_JSON)) {
                requestBodyToString(requestBody)
                //类似 {"ccc":"ccc","ddd":"ddd"}
            } else {
                NOT_STRING_CONTENT_FLAG
            }
            //测试是否是json字符串
            JSONObject(json)
        } catch (e: Exception) {
            //e.printStackTrace();
            json = NOT_STRING_CONTENT_FLAG
            e(TAG, "===body json====>$json")
        }
        return json
    }


    /**
     * 动态添加网络拦截
     *
     * @param request
     * @param response
     */
    private fun addNetWokInfoInAppHealth(request: Request, response: Response) {
        try {
            var upSize: Long = -1
            var downSize: Long = -1
            if (request.body() != null) {
                upSize = request.body()!!.contentLength()
            }
            if (response.body() != null) {
                downSize = response.body()!!.contentLength()
            }
            if (upSize < 0 && downSize < 0) {
                return
            }
            upSize = if (upSize > 0) upSize else 0
            downSize = if (downSize > 0) downSize else 0
            val activityName = ActivityUtils.getTopActivity().javaClass.canonicalName
            var networkBean: AppHealthInfo.DataBean.NetworkBean? = activityName?.let { AppHealthInfoUtil.instance.getNetWorkInfo(it) }
            val networkValuesBean: AppHealthInfo.DataBean.NetworkBean.NetworkValuesBean = AppHealthInfo.DataBean.NetworkBean.NetworkValuesBean()
            networkValuesBean.code = "" + response.code()
            networkValuesBean.up = "" + upSize
            networkValuesBean.down = "" + downSize
            networkValuesBean.method = request.method()
            networkValuesBean.time = "" + TimeUtils.getNowMills()
            networkValuesBean.url = request.url().toString()
            if (networkBean == null) {
                networkBean = AppHealthInfo.DataBean.NetworkBean()
                networkBean.page = activityName
                val networkValuesBeans: MutableList<AppHealthInfo.DataBean.NetworkBean.NetworkValuesBean> = ArrayList<AppHealthInfo.DataBean.NetworkBean.NetworkValuesBean>()
                networkValuesBeans.add(networkValuesBean)
                networkBean.values = networkValuesBeans
                AppHealthInfoUtil.instance.addNetWorkInfo(networkBean)
            } else {
                var networkValuesBeans: MutableList<AppHealthInfo.DataBean.NetworkBean.NetworkValuesBean>? = networkBean.values
                if (networkValuesBeans == null) {
                    networkValuesBeans = ArrayList<AppHealthInfo.DataBean.NetworkBean.NetworkValuesBean>()
                    networkValuesBeans!!.add(networkValuesBean)
                    networkBean.values = networkValuesBeans
                } else {
                    networkValuesBeans.add(networkValuesBean)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 命中拦截规则
     * 返回新的response
     *
     * @param interceptMatchedId
     * @return
     */
    @Throws(Exception::class)
    private fun matchedInterceptRule(url: HttpUrl, path: String, interceptMatchedId: String, templateMatchedId: String, oldRequest: Request, oldResponse: Response, chain: Interceptor.Chain): Response {
        //判断是否需要重定向数据接口
        //http https
        val scheme = url.scheme()
        val interceptApiBean: MockInterceptApiBean = DokitDbManager.instance.getInterceptApiByIdInMap(path, interceptMatchedId, DokitDbManager.FROM_SDK_OTHER) as MockInterceptApiBean
        if (interceptApiBean == null) {
            matchedTemplateRule(oldResponse, path, templateMatchedId)
            return oldResponse
        }
        val selectedSceneId: String? = interceptApiBean.selectedSceneId
        //开关是否被打开
        if (!interceptApiBean.isOpen) {
            matchedTemplateRule(oldResponse, path, templateMatchedId)
            return oldResponse
        }

        //判断是否有选中的场景
        if (TextUtils.isEmpty(selectedSceneId)) {
            matchedTemplateRule(oldResponse, path, templateMatchedId)
            return oldResponse
        }
        val sb = StringBuilder()
        val newUrl: String
        newUrl = if (NetWorkConstant.MOCK_SCHEME_HTTP.contains(scheme.toLowerCase())) {
            sb.append(NetWorkConstant.MOCK_SCHEME_HTTP).append(NetWorkConstant.MOCK_HOST).append("/api/app/scene/").append(selectedSceneId).toString()
        } else {
            sb.append(NetWorkConstant.MOCK_SCHEME_HTTPS).append(NetWorkConstant.MOCK_HOST).append("/api/app/scene/").append(selectedSceneId).toString()
        }

        //LogHelper.i("MOCK_INTERCEPT", "path===>" + path + "  newUrl=====>" + newUrl);
        val newRequest = Request.Builder()
                .method("GET", null)
                .url(newUrl).build()
        //需要提前关闭数据流 不然在某些场景下会报错
        oldResponse.close()
        val newResponse = chain.proceed(newRequest)
        if (newResponse.code() == 200) {
            //判断新的response是否有数据
            return if (newResponseHasData(newResponse)) {
                matchedTemplateRule(newResponse, path, templateMatchedId)
                //拦截命中提示
                ToastUtils.showShort("接口别名:==" + interceptApiBean.mockApiName.toString() + "==已被拦截")
                newResponse
            } else {
                matchedTemplateRule(oldResponse, path, templateMatchedId)
                oldResponse
            }
        }
        matchedTemplateRule(oldResponse, path, templateMatchedId)
        return oldResponse
    }

    /**
     * 命中模板规则
     * 保存正常接口的response到数据库
     *
     * @return
     */
    @Throws(Exception::class)
    private fun matchedTemplateRule(oldResponse: Response, path: String, templateMatchedId: String) {
        //命中模板规则
        if (TextUtils.isEmpty(templateMatchedId)) {
            return
        }
        val templateApiBean: MockTemplateApiBean = DokitDbManager.instance.getTemplateApiByIdInMap(path, templateMatchedId, DokitDbManager.FROM_SDK_OTHER) as MockTemplateApiBean
                ?: return
        //LogHelper.i("MOCK_TEMPLATE", "path=====>" + path + "isOpen===>" + templateApiBean.isOpen());
        if (templateApiBean.isOpen) {
            //保存老的response 数据到数据库
            saveResponse2DB(oldResponse, templateApiBean)
        }
    }


    /**
     * 保存匹配中的数据到本地数据库
     *
     * @param response
     * @param mockApi
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun saveResponse2DB(response: Response, mockApi: MockTemplateApiBean) {
        if (response.code() != 200) {
            return
        }
        if (response.body() == null) {
            return
        }
        val host = response.request().url().host()
        //LogHelper.i(TAG, "host====>" + host);
        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理
        val responseBody = response.peekBody(1024 * 1024.toLong())
        val strResponseBody = responseBody.string()
        if (TextUtils.isEmpty(strResponseBody)) {
            return
        }
        if (host == NetWorkConstant.MOCK_HOST) {
            mockApi.responseFrom = MockTemplateApiBean.RESPONSE_FROM_MOCK
        } else {
            mockApi.responseFrom = MockTemplateApiBean.RESPONSE_FROM_REAL
        }
        mockApi.strResponse = strResponseBody
        //更新本地数据库
        DokitDbManager.instance.updateTemplateApi(mockApi)
        //拦截命中提示
        ToastUtils.showShort("模板别名:==" + mockApi.mockApiName.toString() + "==已被保存")
    }

    /**
     * 新的mock 接口是否有数据
     *
     * @return
     */
    @Throws(Exception::class)
    private fun newResponseHasData(response: Response): Boolean {
        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理
        return if (response.body() == null) {
            false
        } else true
    }

    companion object {
        /**
         * 请求体非字符串类型标识
         */
        const val NOT_STRING_CONTENT_FLAG = "is not string content"
    }

}