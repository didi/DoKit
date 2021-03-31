package com.didichuxing.doraemonkit.kit.filemanager.action.file

import com.didichuxing.doraemonkit.util.ConvertUtils
import com.didichuxing.doraemonkit.util.FileIOUtils
import com.didichuxing.doraemonkit.util.FileUtils
import org.json.JSONArray
import org.json.JSONObject
import org.xml.sax.helpers.DefaultHandler
import javax.xml.parsers.SAXParserFactory


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
            if (fileExtension.contains("xml")) {
                if (isXml(content)) {
                    FileIOUtils.writeFileFromString(filePath, content, false)
                    response["code"] = 200
                    response["success"] = true
                    response["message"] = "success"
                } else {
                    response["code"] = 0
                    response["success"] = false
                    response["message"] = "is not xml"
                }
            }
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

    /**
     * 判断是否是xml
     */
    private fun isXml(content: String): Boolean {
        try {
            val factory = SAXParserFactory.newInstance()
            val sp = factory.newSAXParser()
            sp.parse(ConvertUtils.string2InputStream(content, "utf-8"), DefaultHandler())
            return true
        } catch (e: Exception) {
            return false
        }
    }

}