package com.didichuxing.doraemonkit.kit.network.okhttp.room_db

import com.didichuxing.doraemonkit.widget.bravh.entity.node.BaseNode

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-15-15:44
 * 描    述：mock 上传数据
 * 修订历史：
 * ================================================
 */
abstract class AbsMockApiBean : BaseNode() {
    open var isOpen: Boolean
        get() = false
        set(open) {}

    open val id: String
        get() = ""

    open val selectedSceneId: String
        get() = ""

    open val query: String?
        get() = ""

    open val body: String?
        get() = ""

    open val path: String
        get() = ""

    override val childNode: MutableList<BaseNode>?
        get() = null
}