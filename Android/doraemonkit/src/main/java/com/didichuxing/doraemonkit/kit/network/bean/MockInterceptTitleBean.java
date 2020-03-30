package com.didichuxing.doraemonkit.kit.network.bean;

import com.chad.library.adapter.base.entity.node.BaseExpandNode;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.didichuxing.doraemonkit.kit.network.room_db.MockInterceptApiBean;

import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-12-15:06
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MockInterceptTitleBean<T extends BaseNode> extends BaseExpandNode {
    private String mName;
    private List<T> mChildNode;

    public MockInterceptTitleBean(String name, List<T> childNode) {
        this.mName = name;
        this.mChildNode = childNode;
        setExpanded(false);
    }

    public String getName() {
        return mName;
    }


    @Override
    public List<BaseNode> getChildNode() {
        return (List<BaseNode>) mChildNode;
    }
}
