package com.didichuxing.doraemonkit.kit.mc.ability

import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.kit.mc.all.ui.McCaseInfoDialogProvider
import com.didichuxing.doraemonkit.kit.mc.all.ui.data.McCaseInfo
import com.didichuxing.doraemonkit.kit.mc.all.ui.data.McResInfo
import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.volley.VolleyManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    suspend inline fun <reified T> getMcConfig(): McResInfo<T> = suspendCoroutine {
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
    suspend inline fun <reified T> mockStart(): McResInfo<T> = suspendCoroutine {
        val request = JsonObjectRequest(
            "$host/app/multiControl/startRecord",
            JSONObject(GsonUtils.toJson(AppInfo())),
            { response ->
                it.resume(convert2McResInfo<T>(response))
            }, { error ->
                it.resumeWithException(error)
            })
        VolleyManager.add(request)
    }


    @Throws(Exception::class)
    suspend inline fun <reified T> uploadHttpInfo(httpInfo: HttpInfo): McResInfo<T> =
        suspendCoroutine {
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
    suspend inline fun <reified T> mockStop(caseInfo: McCaseInfoDialogProvider.CaseInfo): McResInfo<T> =
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
    suspend inline fun <reified T> caseList(): McResInfo<T> = suspendCoroutine {
        val request = StringRequest(
            Request.Method.GET,
            "$host/app/multiControl/caseList?pId=${DoKitConstant.PRODUCT_ID}",
            { response ->
                it.resume(convert2McResInfo(JSONObject(response)))
            }, { error ->
                it.resumeWithException(error)
            })
        VolleyManager.add(request)
    }


    @Throws(Exception::class)
    suspend inline fun <reified T> httpMatch(requestKey: String): McResInfo<T> = suspendCoroutine {
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

    inline fun <reified T> convert2McResInfo(json: JSONObject): McResInfo<T> {
        val mcInfo = McResInfo<T>(
            json.getInt("code"),
            json.getString("msg")
        )

        val dataInfo: T? = try {
            val dataJson = json.getJSONObject("data").toString()
            GsonUtils.fromJson(dataJson, T::class.java)
        } catch (e: Exception) {
            null
        }
        mcInfo.data = dataInfo
        return mcInfo
    }
}