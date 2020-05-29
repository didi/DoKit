package com.didichuxing.doraemonkit.kit.network.room_db;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

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
public interface MockApiDao {
    /**
     * 简单sql语句，查询mock_intercept_api表所有的column
     */
    @Query("SELECT * FROM mock_intercept_api")
    List<MockInterceptApiBean> getAllInterceptApi();

    /**
     * 简单sql语句，查询mock_template_api表所有的column
     */
    @Query("SELECT * FROM mock_template_api")
    List<MockTemplateApiBean> getAllTemplateApi();

    /**
     * 根据指定字段获取对象
     */
    @Query("SELECT * FROM mock_intercept_api WHERE path LIKE :path")
    List<MockInterceptApiBean> findInterceptApiByPath(String path);

    /**
     * 根据指定字段获取对象
     */
    @Query("SELECT * FROM mock_intercept_api WHERE id = :id LIMIT 1")
    MockInterceptApiBean findInterceptApiById(String id);

    /**
     * 根据指定字段获取对象
     */
    @Query("SELECT * FROM mock_template_api WHERE path LIKE :path")
    List<MockTemplateApiBean> findTemplateApiByPath(String path);


    /**
     * 根据指定字段获取对象
     */
    @Query("SELECT * FROM mock_template_api WHERE id = :id LIMIT 1")
    MockTemplateApiBean findTemplateApiById(String id);

    /**
     * 插入mockApi数据列表
     */
    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
    void insertAllInterceptApi(List<MockInterceptApiBean> mockApis);

    /**
     * 插入mockApi数据列表
     */
    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
    void insertAllTemplateApi(List<MockTemplateApiBean> mockApis);

    @Update(onConflict = OnConflictStrategy.ROLLBACK)
    int updateInterceptApi(MockInterceptApiBean mockApi);


    @Update(onConflict = OnConflictStrategy.ROLLBACK)
    int updateTemplateApi(MockTemplateApiBean mockApi);

}
