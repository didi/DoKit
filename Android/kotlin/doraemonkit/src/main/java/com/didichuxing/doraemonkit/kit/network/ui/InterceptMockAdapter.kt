package com.didichuxing.doraemonkit.kit.network.ui

import com.didichuxing.doraemonkit.kit.network.okhttp.bean.MockInterceptTitleBean
import com.didichuxing.doraemonkit.kit.network.okhttp.room_db.MockInterceptApiBean
import com.didichuxing.doraemonkit.widget.bravh.BaseNodeAdapter
import com.didichuxing.doraemonkit.widget.bravh.entity.node.BaseNode
import com.didichuxing.doraemonkit.widget.bravh.module.LoadMoreModule

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-12-15:04
 * 描    述：mock adapter
 * 修订历史：
 * ================================================
 */
class InterceptMockAdapter(nodeList: MutableList<BaseNode>?) : BaseNodeAdapter(nodeList), LoadMoreModule {
    override fun getItemType(data: List<BaseNode>, position: Int): Int {
        val node = data[position]
        if (node is MockInterceptTitleBean<*>) {
            return TYPE_TITLE
        } else if (node is MockInterceptApiBean) {
            return TYPE_CONTENT
        }
        return -1
    }

    companion object {
        const val TAG = "InterceptMockAdapter"
        const val TYPE_TITLE = 100
        const val TYPE_CONTENT = 200
    }

    init {
        addFullSpanNodeProvider(InterceptTitleNodeProvider())
        addNodeProvider(InterceptDetailNodeProvider())
    }
}