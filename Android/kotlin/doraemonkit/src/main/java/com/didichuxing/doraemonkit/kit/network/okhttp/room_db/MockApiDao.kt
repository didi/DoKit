package com.didichuxing.doraemonkit.kit.network.okhttp.room_db

import androidx.room.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-15-15:44
 * 描    述：mock 数据查询
 * 修订历史：
 * ================================================
 */
@Dao
interface MockApiDao {
    /**
     * 简单sql语句，查询mock_intercept_api表所有的column
     */
    @get:Query("SELECT * FROM mock_intercept_api")
    val allInterceptApi: List<MockInterceptApiBean?>?

    /**
     * 简单sql语句，查询mock_template_api表所有的column
     */
    @get:Query("SELECT * FROM mock_template_api")
    val allTemplateApi: List<MockTemplateApiBean?>?

    /**
     * 根据指定字段获取对象
     */
    @Query("SELECT * FROM mock_intercept_api WHERE path LIKE :path")
    fun findInterceptApiByPath(path: String?): List<MockInterceptApiBean?>?

    /**
     * 根据指定字段获取对象
     */
    @Query("SELECT * FROM mock_intercept_api WHERE id = :id LIMIT 1")
    fun findInterceptApiById(id: String?): MockInterceptApiBean?

    /**
     * 根据指定字段获取对象
     */
    @Query("SELECT * FROM mock_template_api WHERE path LIKE :path")
    fun findTemplateApiByPath(path: String?): List<MockTemplateApiBean?>?

    /**
     * 根据指定字段获取对象
     */
    @Query("SELECT * FROM mock_template_api WHERE id = :id LIMIT 1")
    fun findTemplateApiById(id: String?): MockTemplateApiBean?

    /**
     * 插入mockApi数据列表
     */
    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
    fun insertAllInterceptApi(mockApis: List<MockInterceptApiBean?>?)

    /**
     * 插入mockApi数据列表
     */
    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
    fun insertAllTemplateApi(mockApis: List<MockTemplateApiBean?>?)

    @Update(onConflict = OnConflictStrategy.ROLLBACK)
    fun updateInterceptApi(mockApi: MockInterceptApiBean?): Int

    @Update(onConflict = OnConflictStrategy.ROLLBACK)
    fun updateTemplateApi(mockApi: MockTemplateApiBean?): Int
}