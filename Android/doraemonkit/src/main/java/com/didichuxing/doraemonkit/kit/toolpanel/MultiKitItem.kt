package com.didichuxing.doraemonkit.kit.toolpanel

import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.widget.bravh.entity.MultiItemEntity

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/29-19:33
 * 描    述：
 * 修订历史：
 * ================================================
 */
public data class MultiKitItem(override val itemType: Int, val name: String?, val kit: AbstractKit?) : MultiItemEntity {
    companion object {
        const val TYPE_TITLE = 999
        const val TYPE_KIT = 201
    }


}