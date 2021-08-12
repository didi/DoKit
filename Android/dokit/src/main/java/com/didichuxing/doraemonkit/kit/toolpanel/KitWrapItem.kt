package com.didichuxing.doraemonkit.kit.toolpanel

import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.widget.brvah.entity.MultiItemEntity

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/29-19:33
 * 描    述：
 * 修订历史：
 * ================================================
 */
data class KitWrapItem(override val itemType: Int, val name: String, var checked: Boolean = false, var groupName: String = "", val kit: AbstractKit?) : MultiItemEntity, Cloneable {
    companion object {
        const val TYPE_TITLE = 999
        const val TYPE_KIT = 201
        const val TYPE_MODE = 202
        const val TYPE_EXIT = 203
        const val TYPE_VERSION = 204
    }

    public override fun clone(): KitWrapItem {
        val item = super.clone() as KitWrapItem
        item.checked = this.checked
        item.groupName = this.groupName
        return item
    }

}