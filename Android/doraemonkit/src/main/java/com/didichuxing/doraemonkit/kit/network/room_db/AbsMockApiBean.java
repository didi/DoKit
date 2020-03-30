package com.didichuxing.doraemonkit.kit.network.room_db;



import com.chad.library.adapter.base.entity.MultiItemEntity;


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
