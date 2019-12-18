package com.didichuxing.doraemonkit.kit.network.room_db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.didichuxing.doraemonkit.kit.network.ui.InterceptMockAdapter;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-15-15:44
 * 描    述：mock 上传数据
 * 修订历史：
 * ================================================
 */
public interface AbsMockApiBean extends MultiItemEntity {

    boolean isOpen();

    void setOpen(boolean open);

    String getId();

    String getSelectedSceneId();

    String getQuery();


    String getPath();

}
