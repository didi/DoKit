package com.didichuxing.doraemonkit.kit.network.room_db;


import com.didichuxing.doraemonkit.widget.brvah.entity.node.BaseNode;

import java.util.List;


/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-15-15:44
 * 描    述：mock 上传数据
 * 修订历史：
 * ================================================
 */
public abstract class AbsMockApiBean extends BaseNode {

    boolean isOpen() {
        return false;
    }

    void setOpen(boolean open) {

    }

    String getId() {
        return "";
    }

    String getSelectedSceneId() {
        return "";
    }

    String getQuery() {
        return "";
    }

    String getBody() {
        return "";
    }


    String getPath() {
        return "";
    }

    @Override
    public List<BaseNode> getChildNode() {
        return null;
    }
}
