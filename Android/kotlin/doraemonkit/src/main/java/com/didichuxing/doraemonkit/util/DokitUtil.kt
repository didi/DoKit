package com.didichuxing.doraemonkit.util

import androidx.annotation.StringRes
import com.didichuxing.doraemonkit.DoraemonKit
import com.didichuxing.doraemonkit.util.LogHelper.e
import okhttp3.RequestBody
import okio.Buffer
import java.io.IOException

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/27-15:08
 * 描    述：
 * 修订历史：
 * ================================================
 */
object DokitUtil {
    fun getString(@StringRes stringId: Int): String {
        return DoraemonKit.APPLICATION!!.getString(stringId)
    }

    @StringRes
    fun getStringId(str: String): Int {
        try {
            val r = DoraemonKit.APPLICATION!!.resources
            return r.getIdentifier(str, "string", DoraemonKit.APPLICATION!!.packageName)
        } catch (e: Exception) {
            e("getStringId", "getStringId===>$str")
        }
        return -1
    }

    fun requestBodyToString(requestBody: RequestBody): String {
        try {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            return buffer.readUtf8()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 字符串对转json
     *
     * @param param
     * @return
     */
    fun param2Json(param: String): String {
        var param = param
        param = param.replace("=".toRegex(), "\":\"")
        param = param.replace("&".toRegex(), "\",\"")
        return "{\"$param\"}"
    }
}