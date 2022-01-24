package com.didichuxing.doraemonkit.kit.mc.ability

import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.mc.all.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.all.ui.McCaseInfoDialogProvider
import com.didichuxing.doraemonkit.kit.mc.data.AppInfo
import com.didichuxing.doraemonkit.kit.mc.data.HttpUploadInfo
import com.didichuxing.doraemonkit.kit.mc.data.McResInfo
import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.volley.VolleyManager
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

    const val host = "https://www.dokit.cn"
//    const val host = "https://pre.dokit.cn"
//    const val host = "http://dokit-test.intra.xiaojukeji.com"

    var mExcludeKey: List<String> = mutableListOf()


    suspend inline fun <reified T> getMcConfig(): McResInfo<T> = suspendCoroutine {
        try {
            val jsonObject = JSONObject()
            jsonObject.put("pId", DoKitManager.PRODUCT_ID)
            val request = JsonObjectRequest(
                "$host/app/multiControl/getConfig",
                jsonObject,
                { response ->
                    it.resume(convert2McResInfoWithObj(response))
                }, { error ->
                    it.resumeWithException(error)
                })
            VolleyManager.add(request)
        } catch (e: Exception) {
            it.resumeWithException(e)
        }

    }


    suspend inline fun <reified T> mockStart(): McResInfo<T> = suspendCoroutine {
        try {
            val request = JsonObjectRequest(
                "$host/app/multiControl/startRecord",
                JSONObject(GsonUtils.toJson(AppInfo())),
                { response ->
                    it.resume(convert2McResInfoWithObj(response))
                }, { error ->
                    it.resumeWithException(error)
                })
            VolleyManager.add(request)
        } catch (e: Exception) {
            it.resumeWithException(e)
        }

    }


    suspend inline fun <reified T> uploadHttpInfo(httpInfo: HttpUploadInfo): McResInfo<T> =
        suspendCoroutine {
            try {
                val request = JsonObjectRequest(
                    "$host/app/multiControl/uploadApiInfo",
                    JSONObject(GsonUtils.toJson(httpInfo)),
                    { response ->
                        it.resume(convert2McResInfoWithObj(response))
                    }, { error ->
                        it.resumeWithException(error)
                    })
                VolleyManager.add(request)
            } catch (e: Exception) {
                it.resumeWithException(e)
            }

        }

    suspend inline fun <reified T> mockStop(caseInfo: McCaseInfoDialogProvider.CaseInfo): McResInfo<T> =
        suspendCoroutine {
            try {
                val request = JsonObjectRequest(
                    "$host/app/multiControl/endRecord",
                    JSONObject(GsonUtils.toJson(caseInfo)),
                    { response ->
                        it.resume(convert2McResInfoWithObj(response))
                    }, { error ->
                        it.resumeWithException(error)
                    })
                VolleyManager.add(request)
            } catch (e: Exception) {
                it.resumeWithException(e)
            }

        }


    suspend inline fun <reified T> caseList(): McResInfo<List<T>> = suspendCoroutine {
        try {
            val request = StringRequest(
                Request.Method.GET,
                "$host/app/multiControl/caseList?pId=${DoKitManager.PRODUCT_ID}",
                { response ->
                    it.resume(convert2McResInfoWithList(JSONObject(response)))
                }, { error ->
                    it.resumeWithException(error)
                })
            VolleyManager.add(request)
        } catch (e: Exception) {
            it.resumeWithException(e)
        }

    }


    suspend inline fun <reified T> httpMatch(requestKey: String): McResInfo<T> = suspendCoroutine {
        try {
            val request = StringRequest(
                "$host/app/multiControl/getCaseApiInfo?key=${requestKey}&pId=${DoKitManager.PRODUCT_ID}&caseId=${DoKitMcManager.MC_CASE_ID}",
                { response ->
                    it.resume(convert2McResInfoWithObj(JSONObject(response)))
                }, { error ->
                    it.resumeWithException(error)
                })
            VolleyManager.add(request)
        } catch (e: Exception) {
            it.resumeWithException(e)
        }

    }


    inline fun <reified T> convert2McResInfoWithObj(json: JSONObject): McResInfo<T> {
        return try {
            val mcInfo = McResInfo<T>(
                json.optInt("code", 0),
                json.optString("msg", "has no msg value")
            )

            val dataJson = json.optJSONObject("data")
            val type = GsonUtils.getType(T::class.java)
            if (dataJson != null){
                mcInfo.data = GsonUtils.fromJson(dataJson.toString(), type)
            }
            mcInfo
        } catch (e: Exception) {
            McResInfo(
                0,
                "Gson format error",
                null
            )
        }
    }


    inline fun <reified T> convert2McResInfoWithList(json: JSONObject): McResInfo<List<T>> {
        return try {
            val mcInfo = McResInfo<List<T>>(
                json.optInt("code", 0),
                json.optString("msg", "has no msg value")
            )

            val dataJson = json.optJSONArray("data")
            val type = GsonUtils.getListType(T::class.java)
            mcInfo.data = GsonUtils.fromJson(dataJson.toString(), type)
            mcInfo
        } catch (e: Exception) {
            McResInfo(
                0,
                "Gson format error",
                null
            )
        }
    }
}
