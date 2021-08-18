package com.didichuxing.doraemonkit.kit.network.room_db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-15-16:06
 * 描    述：注解指定了database的表映射实体数据以及版本等信息
 * 修订历史：
 * ================================================
 */
@Database(entities = {MockInterceptApiBean.class, MockTemplateApiBean.class}, version = 2, exportSchema = false)
public abstract class DokitDatabase extends RoomDatabase {
    abstract MockApiDao mockApiDao();
}
