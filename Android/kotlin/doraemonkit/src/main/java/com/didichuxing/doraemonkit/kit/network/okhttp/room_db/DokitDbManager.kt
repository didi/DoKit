package com.didichuxing.doraemonkit.kit.network.okhttp.room_db

import android.text.TextUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ThreadUtils.SimpleTask
import com.didichuxing.doraemonkit.constant.DokitConstant.dealDidiPlatformPath
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.MockInterceptor
import com.didichuxing.doraemonkit.util.LogHelper
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-15-16:12
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DokitDbManager<T : AbsMockApiBean> {
    /**
     * key 为path 可能存在path是一样的 所以value为List
     */
    private val mGlobalInterceptApiMaps: MutableMap<String, MutableList<T>?>? = HashMap()

    /**
     * key 为path 可能存在path是一样的 所以value为List
     */
    private val mGlobalTemplateApiMaps: MutableMap<String, MutableList<T>?>? = HashMap()
    var globalTemplateApiBean: MockTemplateApiBean? = null
    val globalInterceptApiMaps: Map<String, MutableList<T>?>?
        get() = mGlobalInterceptApiMaps

    val globalTemplateApiMaps: Map<String, MutableList<T>?>?
        get() = mGlobalTemplateApiMaps


    /**
     * 获取所有的mock intercept apis
     */
    val allInterceptApis: Unit
        get() {
            ThreadUtils.executeByIo(object : SimpleTask<List<T>?>() {
                @Throws(Throwable::class)
                override fun doInBackground(): List<T> {
                    val db: DokitDatabase = DokitViewManager.instance.db
                    return if (db?.mockApiDao() != null) {
                        db.mockApiDao().allInterceptApi as List<T>
                    } else {
                        throw NullPointerException("mDb == null || mDb.mockApiDao()")
                    }
                }


                override fun onFail(t: Throwable) {
                    super.onFail(t)
                    LogHelper.e(TAG, "error====>" + t.message);
                }

                override fun onSuccess(result: List<T>?) {
                    result?.let {
                        list2mapByIntercept(it)
                    }

                }
            })
        }

    //LogHelper.e(TAG, "error====>" + t.getMessage());

    /**
     * 获取所有的mock template apis
     */
    val allTemplateApis: Unit
        get() {
            ThreadUtils.executeByIo(object : SimpleTask<List<T>?>() {
                @Throws(Throwable::class)
                override fun doInBackground(): List<T> {
                    val db: DokitDatabase = DokitViewManager.instance.db
                    return if (db?.mockApiDao() != null) {
                        db.mockApiDao().allTemplateApi as List<T>
                    } else {
                        throw NullPointerException("mDb == null || mDb.mockApiDao()")
                    }
                }

                override fun onFail(t: Throwable) {
                    super.onFail(t)

                    LogHelper.e(TAG, "error====>" + t.message);
                }

                override fun onSuccess(result: List<T>?) {
                    result?.let {
                        list2mapByTemplate(it)
                    }
                }
            })
        }

    /**
     * 数据库中获取指定的 template api
     */
    fun getTemplateApiByIdInDb(id: String): MockTemplateApiBean? {
        return DokitViewManager.instance.db.mockApiDao().findTemplateApiById(id)
    }

    /**
     * 数据库中获取指定的mock intercept api
     */
    fun getInterceptApiByIdInDb(id: String?): MockInterceptApiBean? {
        return DokitViewManager.instance.db.mockApiDao().findInterceptApiById(id)
    }

    /**
     * 内存中中获取指定的mock intercept api
     */
    fun getInterceptApiByIdInMap(path: String, id: String, fromSDK: Int): T? {
        var path = path
        if (mGlobalInterceptApiMaps == null) {
            return null
        }
        //先进行全匹配
        var mGlobalInterceptApis: List<T>? = mGlobalInterceptApiMaps[path]
        if (mGlobalInterceptApis == null) {
            path = dealDidiPlatformPath(path, fromSDK)
            mGlobalInterceptApis = mGlobalInterceptApiMaps[path]
        }

        //再进行滴滴内部匹配
        if (mGlobalInterceptApis == null) {
            return null
        }
        var selectedMockApi: T? = null
        for (mockApi in mGlobalInterceptApis) {
            if (mockApi!!.id == id) {
                selectedMockApi = mockApi
                break
            }
        }
        return selectedMockApi
    }

    /**
     * 内存中获取指定的 template api
     */
    fun getTemplateApiByIdInMap(path: String, id: String, fromSDK: Int): T? {
        var path = path
        if (mGlobalTemplateApiMaps == null) {
            return null
        }
        var mGlobalTemplateApis: List<T>? = mGlobalTemplateApiMaps[path]
        //先进行全匹配
        if (mGlobalTemplateApis == null) {
            path = dealDidiPlatformPath(path, fromSDK)
            mGlobalTemplateApis = mGlobalTemplateApiMaps[path]
        }
        //再进行滴滴内部匹配
        if (mGlobalTemplateApis == null) {
            return null
        }
        var selectedMockApi: T? = null
        for (mockApi in mGlobalTemplateApis) {
            if (mockApi!!.id == id) {
                selectedMockApi = mockApi
                break
            }
        }
        return selectedMockApi
    }

    /**
     * 插入所有的mock intercept  Api 数据
     *
     * @param mockApis
     */
    fun insertAllInterceptApi(mockApis: List<MockInterceptApiBean?>?) {
        ThreadUtils.executeByIo(object : SimpleTask<Any?>() {
            @Throws(Throwable::class)
            override fun doInBackground(): Any? {
                DokitViewManager.instance.db.mockApiDao().insertAllInterceptApi(mockApis)
                //更新本地数据
                return null
            }

            override fun onSuccess(result: Any?) {
            }
        })
    }

    /**
     * 插入所有的mock template  Api 数据
     *
     * @param mockApis
     */
    fun insertAllTemplateApi(mockApis: List<MockTemplateApiBean>) {
        ThreadUtils.executeByIo(object : SimpleTask<Any>() {
            @Throws(Throwable::class)
            override fun doInBackground(): Any? {

                DokitViewManager.instance.db.mockApiDao().insertAllTemplateApi(mockApis)
                return null
            }


            override fun onSuccess(result: Any?) {
                allTemplateApis
            }
        })
    }

    /**
     * 更新某个intercept Api
     *
     * @param mockApi
     */
    fun updateInterceptApi(mockApi: MockInterceptApiBean?) {
        ThreadUtils.executeByIo(object : SimpleTask<Any?>() {
            @Throws(Throwable::class)
            override fun doInBackground(): Any? {
                DokitViewManager.instance.db.mockApiDao().updateInterceptApi(mockApi)
                return null
            }

            override fun onSuccess(result: Any?) {
                //更新本地数据
                allInterceptApis
            }
        })
    }

    /**
     * 更新某个template Api
     *
     * @param mockApi
     */
    fun updateTemplateApi(mockApi: MockTemplateApiBean?) {
        ThreadUtils.executeByIo(object : SimpleTask<Any?>() {
            @Throws(Throwable::class)
            override fun doInBackground(): Any? {
                DokitViewManager.instance.db.mockApiDao().updateTemplateApi(mockApi)
                //更新本地数据
                allTemplateApis
                return null
            }

            override fun onSuccess(result: Any?) {}
        })
    }

    /**
     * 返回选中的场景id
     *
     * @param path
     * @param id
     * @return
     */
    fun getMockInterceptSelectedSceneIdByPathAndId(path: String, id: String): String {
        if (mGlobalInterceptApiMaps!![path] == null) {
            return ""
        }
        var selectedSceneId = ""
        for (mockApi in mGlobalInterceptApiMaps[path]!!) {
            if (mockApi!!.id == id) {
                selectedSceneId = mockApi!!.selectedSceneId
                break
            }
        }
        return selectedSceneId
    }

    /**
     * 返回命中的id
     *
     * @param path
     * @param jsonQuery
     * @param operateType
     * @return
     */
    fun isMockMatched(path: String, jsonQuery: String, jsonRequestBody: String, operateType: Int, fromSDK: Int): String {
        //如果是非字符串类型的请求体 直接不匹配
        if (!TextUtils.isEmpty(jsonQuery) && jsonQuery == MockInterceptor.NOT_STRING_CONTENT_FLAG) {
            return ""
        }
        if (!TextUtils.isEmpty(jsonRequestBody) && jsonRequestBody == MockInterceptor.NOT_STRING_CONTENT_FLAG) {
            return ""
        }
        val mockApi = mockMatched(path, jsonQuery, jsonRequestBody, operateType, fromSDK)
                ?: return ""
        return mockApi.id
    }

    /**
     * 通过path和query查询指定的对象
     *
     * @param path
     * @param jsonQuery
     * @param operateType 1:代表拦截 2：代表模板
     * @return
     */
    private fun mockMatched(path: String, jsonQuery: String, jsonRequestBody: String, operateType: Int, fromSDK: Int): T? {
        var path = path
        var mockApis: List<T>? = null
        if (operateType == MOCK_API_INTERCEPT) {
            //先进行一次全匹配
            mockApis = mGlobalInterceptApiMaps!![path]
            //滴滴内部sdk匹配
            if (mockApis == null) {
                path = dealDidiPlatformPath(path, fromSDK)
                mockApis = mGlobalInterceptApiMaps[path]
            }
        } else if (operateType == MOCK_API_TEMPLATE) {
            //先进行一次全匹配
            mockApis = mGlobalTemplateApiMaps!![path]
            //滴滴内部sdk匹配
            if (mockApis == null) {
                path = dealDidiPlatformPath(path, fromSDK)
                mockApis = mGlobalTemplateApiMaps[path]
            }
        }
        if (mockApis == null) {
            return null
        }
        var matchedMockApi: T? = null
        for (mockApi in mockApis) {
            if (mockApi!!.isOpen && queriesMatched(jsonQuery, mockApi) && bodyMatched(jsonRequestBody, mockApi)) {
                matchedMockApi = mockApi
                break
            }
        }
        return matchedMockApi
    }

    /**
     * queries 是否命中
     *
     * @param jsonQuery
     * @param mockApi
     */
    private fun queriesMatched(jsonQuery: String, mockApi: T): Boolean {
        //{}代表没有配置query
        val mockQuery = mockApi!!.query
        val mockQueryIsEmpty = TextUtils.isEmpty(mockQuery) || "{}" == mockQuery
        //平台没有配置query参数且本地也没有query参数
        if (mockQueryIsEmpty && TextUtils.isEmpty(jsonQuery)) {
            return true
        }

        //本地有参数 平台没有配置参数
        if (!TextUtils.isEmpty(jsonQuery) && mockQueryIsEmpty) {
            return true
        }

        //本地没有参数 但是平台有参数
        if (TextUtils.isEmpty(jsonQuery) && !mockQueryIsEmpty) {
            return false
        }

        //匹配query
        if (!TextUtils.isEmpty(jsonQuery) && !mockQueryIsEmpty) {
            try {
                val jsonQueryLocal = JSONObject(jsonQuery)
                val jsonQueryMock = JSONObject(mockQuery)
                val keys: MutableList<String> = ArrayList()
                //通过平台端的来主动匹配
                val iterator = jsonQueryMock.keys()
                while (iterator.hasNext()) {
                    keys.add(iterator.next())
                }
                var count = 0
                for (index in keys.indices) {
                    val key = keys[index]
                    if (jsonQueryLocal.has(key) && jsonQueryMock.getString(key) == jsonQueryLocal[key]) {
                        count++
                    }
                }
                if (count == keys.size) {
                    return true
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                return false
            }
        }
        return false
    }

    /**
     * body 是否命中
     *
     * @param jsonRequestBody
     * @param mockApi
     */
    private fun bodyMatched(jsonRequestBody: String, mockApi: T): Boolean {
        //{}代表没有配置query
        val mockBody = mockApi!!.body
        val mockQueryIsEmpty = TextUtils.isEmpty(mockBody) || "{}" == mockBody
        //平台没有配置query参数且本地也没有query参数
        if (mockQueryIsEmpty && TextUtils.isEmpty(jsonRequestBody)) {
            return true
        }

        //本地有参数 平台没有配置参数
        if (!TextUtils.isEmpty(jsonRequestBody) && mockQueryIsEmpty) {
            return true
        }

        //本地没有参数 但是平台有参数
        if (TextUtils.isEmpty(jsonRequestBody) && !mockQueryIsEmpty) {
            return false
        }

        //匹配body
        if (!TextUtils.isEmpty(jsonRequestBody) && !mockQueryIsEmpty) {
            try {
                val jsonBodyLocal = JSONObject(jsonRequestBody)
                val jsonBodyMock = JSONObject(mockBody)
                val keys: MutableList<String> = ArrayList()
                val iterator = jsonBodyMock.keys()
                while (iterator.hasNext()) {
                    keys.add(iterator.next())
                }
                var count = 0
                for (index in keys.indices) {
                    val key = keys[index]
                    if (jsonBodyLocal.has(key) && jsonBodyMock.getString(key) == jsonBodyLocal[key]) {
                        count++
                    }
                }
                if (count == keys.size) {
                    return true
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                return false
            }
        }
        return false
    }

    private fun list2mapByIntercept(interceptApiBeans: List<T>) {
        mGlobalInterceptApiMaps!!.clear()
        for (mockApi in interceptApiBeans) {
            if (mGlobalInterceptApiMaps[mockApi!!.path] == null) {
                val mockInterceptApiBeans: MutableList<T> = ArrayList()
                mockInterceptApiBeans.add(mockApi)
                mGlobalInterceptApiMaps[mockApi.path] = mockInterceptApiBeans
            } else {
                mGlobalInterceptApiMaps[mockApi.path]?.add(mockApi)
            }
        }
    }

    private fun list2mapByTemplate(templateApiBeans: List<T>) {
        mGlobalTemplateApiMaps!!.clear()
        for (mockApi in templateApiBeans) {
            if (mGlobalTemplateApiMaps[mockApi!!.path] == null) {
                val mockTemplateApiBeans: MutableList<T> = ArrayList()
                mockTemplateApiBeans.add(mockApi)
                mGlobalTemplateApiMaps[mockApi.path] = mockTemplateApiBeans
            } else {
                mGlobalTemplateApiMaps[mockApi.path]?.add(mockApi)
            }
        }
    }

    companion object {
        private const val TAG = "DokitDbManager"

        /**
         * 静态内部类
         */
        @JvmStatic
        val instance = DokitDbManager<AbsMockApiBean>()


        const val CONTENT_TYPE = "application/json"

        /**
         * 拦截
         */
        const val MOCK_API_INTERCEPT = 1

        /**
         * 模板
         */
        const val MOCK_API_TEMPLATE = 2

        /**
         * 来自滴滴内部SDK
         */
        @JvmField
        var FROM_SDK_DIDI = 100

        /**
         * 来自外部SDK
         */
        var FROM_SDK_OTHER = 101
    }
}