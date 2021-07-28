package com.didichuxing.doraemonkit.kit.network.bean;

import com.didichuxing.doraemonkit.widget.brvah.entity.node.BaseExpandNode;
import com.didichuxing.doraemonkit.widget.brvah.entity.node.BaseNode;

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
public class MockTemplateTitleBean<T extends BaseNode> extends BaseExpandNode {
    private String mName;
    private List<T> mChildNode;


    public MockTemplateTitleBean(String path, List<T> childNode) {
        this.mName = path;
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
