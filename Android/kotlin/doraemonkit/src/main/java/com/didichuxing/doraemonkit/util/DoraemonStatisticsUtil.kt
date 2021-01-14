package com.didichuxing.doraemonkit.util

import android.content.Context
import com.blankj.utilcode.util.AppUtils
import com.didichuxing.doraemonkit.BuildConfig
import com.didichuxing.doraemonkit.constant.NetWorkConstant
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

/**
 * Created by wanglikun on 2018/12/21.
 */
object DoraemonStatisticsUtil {
    private const val TAG = "DoraemonStatisticsUtil"

    @Throws(Exception::class)
    fun uploadUserInfo(context: Context?) {
        val appId: String = AppUtils.getAppPackageName()
        val appName: String = AppUtils.getAppName()
        val type = "Android"
        //0 代表内部版本  1代表外部版本
        val from = "1"
        val mediaType = MediaType.parse("application/json; charset=utf-8")
        val jsonObject = JSONObject()
        try {
            jsonObject.put("appId", appId)
            jsonObject.put("appName", appName)
            jsonObject.put("version", "" + BuildConfig.DOKIT_VERSION)
            jsonObject.put("type", type)
            jsonObject.put("from", from)
            jsonObject.put("language", Locale.getDefault().displayLanguage)
        } catch (e: JSONException) {
            LogHelper.e(TAG, e.toString())
        }
        val client = OkHttpClient()
        val requestBody = RequestBody.create(mediaType,
                jsonObject.toString())
        val request: Request = Request.Builder()
                .url(NetWorkConstant.APP_START_DATA_PICK_URL)
                .post(requestBody)
                .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                response.close()
            }
        })
    }
}