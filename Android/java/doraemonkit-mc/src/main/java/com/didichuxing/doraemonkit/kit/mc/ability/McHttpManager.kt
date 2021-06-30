package com.didichuxing.doraemonkit.kit.mc.ability

import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.kit.mc.all.ui.McCaseInfoDialogProvider
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
    suspend fun mcConfig() = suspendCoroutine<JSONObject> {
        val jsonObject = JSONObject()
        jsonObject.put("pId", DoKitConstant.PRODUCT_ID)
        val request = JsonObjectRequest(
            "",
            jsonObject,
            { response ->
                it.resume(response)
            }, { error ->
                it.resumeWithException(error)
            })
        VolleyManager.add(request)
    }


    @Throws(Exception::class)
    suspend fun mockStart() = suspendCoroutine<JSONObject> {
        val request = JsonObjectRequest(
            "",
            JSONObject(GsonUtils.toJson(AppInfo())),
            { response ->
                it.resume(response)
            }, { error ->
                it.resumeWithException(error)
            })
        VolleyManager.add(request)
    }


    @Throws(Exception::class)
    suspend fun uploadHttpInfo(httpInfo: HttpInfo) = suspendCoroutine<JSONObject> {

        val request = JsonObjectRequest(
            "",
            JSONObject(GsonUtils.toJson(httpInfo)),
            { response ->
                it.resume(response)
            }, { error ->
                it.resumeWithException(error)
            })
        VolleyManager.add(request)
    }

    @Throws(Exception::class)
    suspend fun mockStop(caseInfo: McCaseInfoDialogProvider.CaseInfo) =
        suspendCoroutine<JSONObject> {


            val request = JsonObjectRequest(
                "",
                JSONObject(GsonUtils.toJson(caseInfo)),
                { response ->
                    it.resume(response)
                }, { error ->
                    it.resumeWithException(error)
                })
            VolleyManager.add(request)
        }


    @Throws(Exception::class)
    suspend fun mockCaseList() = suspendCoroutine<String> {
        val request = StringRequest(
            Request.Method.GET,
            "",
            { response ->
                it.resume(response)
            }, { error ->
                it.resumeWithException(error)
            })
        VolleyManager.add(request)
    }


    @Throws(Exception::class)
    suspend fun httpMatch(requestKey: String) = suspendCoroutine<JSONObject> {
        val jsonObject = JSONObject()
        jsonObject.put("key", requestKey)
        jsonObject.put("caseId", "caseId")
        val request = JsonObjectRequest(
            "",
            jsonObject,
            { response ->
                it.resume(response)
            }, { error ->
                it.resumeWithException(error)
            })
        VolleyManager.add(request)
    }
}