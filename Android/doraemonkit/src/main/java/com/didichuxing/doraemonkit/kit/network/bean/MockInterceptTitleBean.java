package com.didichuxing.doraemonkit.kit.network.bean;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.didichuxing.doraemonkit.kit.network.room_db.MockInterceptApiBean;
import com.didichuxing.doraemonkit.kit.network.ui.InterceptMockAdapter;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-12-15:06
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MockInterceptTitleBean extends AbstractExpandableItem<MockInterceptApiBean> implements MultiItemEntity {
    private String name;


    @Override
    public int getItemType() {
        return InterceptMockAdapter.TYPE_TITLE;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    public MockInterceptTitleBean(String path) {
        this.name = path;

    }

    public String getName() {
        return name;
    }


}
