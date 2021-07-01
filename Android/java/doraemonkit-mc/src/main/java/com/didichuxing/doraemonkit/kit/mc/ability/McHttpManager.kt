package com.didichuxing.doraemonkit.kit.mc.ability

import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.kit.mc.all.ui.McCaseInfoDialogProvider
import com.didichuxing.doraemonkit.kit.mc.all.ui.McResInfo
import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.util.ToastUtils
import com.didichuxing.doraemonkit.volley.VolleyManager
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/21-17:16
 * 描    述：
 * 修订历史：
 * ================================================
 */
object McHttpManager {
    const val RESPONSE_OK = 200

    const val host = "http://dokit-test.intra.xiaojukeji.com"

    val mHttpInfoMap: MutableMap<String, HttpInfo> by lazy {
        mutableMapOf<String, HttpInfo>()
    }

    val mExcludeKey: List<String> by lazy {
        mutableListOf<String>()
    }


//    /**
//     * 上传数据
//     */
//    @Throws(Exception::class)
//    suspend fun upload(): String {
//        val client = HttpClient(CIO)
//        return client.post<String> {
//            url("")
//            body = GsonUtils.toJson(mUserCaseInfo)
//        }
//    }


    @Throws(Exception::class)
    suspend fun <T> getMcConfig(): McResInfo<T> = suspendCoroutine {
        val jsonObject = JSONObject()
        jsonObject.put("pId", DoKitConstant.PRODUCT_ID)
        val request = JsonObjectRequest(
            "$host/app/multiControl/getConfig",
            jsonObject,
            { response ->
                it.resume(convert2McResInfo(response))
            }, { error ->
                it.resumeWithException(error)
            })
        VolleyManager.add(request)
    }


    @Throws(Exception::class)
    suspend fun <T> mockStart(): McResInfo<T> = suspendCoroutine {
        val request = JsonObjectRequest(
            "$host/app/multiControl/startRecord",
            JSONObject(GsonUtils.toJson(AppInfo())),
            { response ->
                it.resume(convert2McResInfo(response))
            }, { error ->
                it.resumeWithException(error)
            })
        VolleyManager.add(request)
    }


    @Throws(Exception::class)
    suspend fun <T> uploadHttpInfo(httpInfo: HttpInfo): McResInfo<T> = suspendCoroutine {
        val request = JsonObjectRequest(
            "$host/app/multiControl/uploadApiInfo",
            JSONObject(GsonUtils.toJson(httpInfo)),
            { response ->
                it.resume(convert2McResInfo(response))
            }, { error ->
                it.resumeWithException(error)
            })
        VolleyManager.add(request)
    }

    @Throws(Exception::class)
    suspend fun <T> mockStop(caseInfo: McCaseInfoDialogProvider.CaseInfo): McResInfo<T> =
        suspendCoroutine {
            val request = JsonObjectRequest(
                "$host/app/multiControl/endRecord",
                JSONObject(GsonUtils.toJson(caseInfo)),
                { response ->
                    it.resume(convert2McResInfo(response))
                }, { error ->
                    it.resumeWithException(error)
                })
            VolleyManager.add(request)
        }


    @Throws(Exception::class)
    suspend fun <T> mockCaseList(): McResInfo<T> = suspendCoroutine {
        val request = StringRequest(
            Request.Method.GET,
            "$host/app/multiControl/caseList",
            { response ->
                it.resume(convert2McResInfo(JSONObject(response)))
            }, { error ->
                it.resumeWithException(error)
            })
        VolleyManager.add(request)
    }


    @Throws(Exception::class)
    suspend fun <T> httpMatch(requestKey: String): McResInfo<T> = suspendCoroutine {
        val jsonObject = JSONObject()
        jsonObject.put("key", requestKey)
        jsonObject.put("caseId", "caseId")
        val request = JsonObjectRequest(
            "$host/app/multiControl/getCaseApiInfo",
            jsonObject,
            { response ->
                it.resume(convert2McResInfo(response))
            }, { error ->
                it.resumeWithException(error)
            })
        VolleyManager.add(request)
    }


    private fun <T> convert2McResInfo(response: JSONObject): McResInfo<T> {
        return GsonUtils.fromJson(response.toString(), McResInfo::class.java) as McResInfo<T>
    }
}