package com.didichuxing.doraemonkit.kit.filemanager.action.file

import android.util.Xml
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.JsonUtils
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Document
import org.xml.sax.ContentHandler
import org.xml.sax.helpers.DefaultHandler
import org.xmlpull.v1.XmlPullParser
import java.lang.Exception

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/23-15:26
 * 描    述：
 * 修订历史：
 * ================================================
 */
object SaveFileAction {
    fun saveFileRes(content: String, filePath: String): MutableMap<String, Any> {
        val response = mutableMapOf<String, Any>()

        if (FileUtils.isFileExists(filePath)) {
            val fileExtension = FileUtils.getFileExtension(filePath)
            if (fileExtension.contains("json")) {
                var isJsonObject = false
                var isJsonArray = false
                try {
                    JSONObject(content)
                    isJsonObject = true
                } catch (e: Exception) {
                    isJsonObject = false
                }

                try {
                    JSONArray(content)
                    isJsonArray = true
                } catch (e: Exception) {
                    isJsonArray = false
                }
                if (isJsonObject || isJsonArray) {
                    FileIOUtils.writeFileFromString(filePath, content, false)
                    response["code"] = 200
                    response["success"] = true
                    response["message"] = "success"
                } else {
                    response["code"] = 0
                    response["success"] = false
                    response["message"] = "is not json"
                }

            } else {
                FileIOUtils.writeFileFromString(filePath, content, false)
                response["code"] = 200
                response["success"] = true
                response["message"] = "success"
            }
        } else {
            response["code"] = 0
            response["success"] = false
            response["message"] = "is not a file"
        }
        return response
    }

}