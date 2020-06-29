package com.didichuxing.doraemonkit.kit.network.okhttp.bean

import com.didichuxing.doraemonkit.widget.bravh.entity.node.BaseExpandNode
import com.didichuxing.doraemonkit.widget.bravh.entity.node.BaseNode

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-12-15:06
 * 描    述：
 * 修订历史：
 * ================================================
 */
class MockTemplateTitleBean<T : BaseNode>(val name: String, childNode: MutableList<T>) : BaseExpandNode() {
    private val mChildNode: MutableList<T>

    override val childNode: MutableList<BaseNode>
        get() = mChildNode as MutableList<BaseNode>

    init {
        mChildNode = childNode
        isExpanded = false
    }
}