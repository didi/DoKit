package com.didichuxing.doraemonkit.kit.network.ui;

import androidx.annotation.NonNull;

import com.didichuxing.doraemonkit.widget.brvah.BaseNodeAdapter;
import com.didichuxing.doraemonkit.widget.brvah.entity.node.BaseNode;
import com.didichuxing.doraemonkit.widget.brvah.module.LoadMoreModule;
import com.didichuxing.doraemonkit.kit.network.bean.MockInterceptTitleBean;
import com.didichuxing.doraemonkit.kit.network.room_db.MockInterceptApiBean;

import java.util.List;


/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-12-15:04
 * 描    述：mock adapter
 * 修订历史：
 * ================================================
 */
public class InterceptMockAdapter extends BaseNodeAdapter implements LoadMoreModule {
    public static final String TAG = "InterceptMockAdapter";
    public final static int TYPE_TITLE = 100;
    public final static int TYPE_CONTENT = 200;

    public InterceptMockAdapter(List<BaseNode> nodeList) {
        super(nodeList);
        addFullSpanNodeProvider(new InterceptTitleNodeProvider());
        addNodeProvider(new InterceptDetailNodeProvider());
    }

    @Override
    protected int getItemType(@NonNull List<? extends BaseNode> data, int position) {
        BaseNode node = data.get(position);
        if (node instanceof MockInterceptTitleBean) {
            return InterceptMockAdapter.TYPE_TITLE;
        } else if (node instanceof MockInterceptApiBean) {
            return InterceptMockAdapter.TYPE_CONTENT;
        }
        return -1;
    }
}
