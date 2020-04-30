package com.didichuxing.doraemonkit.kit.toolpanel

import android.widget.ImageView
import android.widget.TextView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.widget.bravh.BaseMultiItemQuickAdapter
import com.didichuxing.doraemonkit.widget.bravh.module.DraggableModule
import com.didichuxing.doraemonkit.widget.bravh.viewholder.BaseViewHolder

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/29-15:21
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DokitManagerAdapter
    : BaseMultiItemQuickAdapter<MultiKitItem, BaseViewHolder>, DraggableModule {

    constructor(kitViews: MutableList<MultiKitItem>?) : super(kitViews) {
        addItemType(MultiKitItem.TYPE_TITLE, R.layout.dk_item_group_title)
        addItemType(MultiKitItem.TYPE_KIT, R.layout.dk_item_group_kit_new)
    }

    override fun convert(holder: BaseViewHolder, item: MultiKitItem) {
        when (item.itemType) {
            MultiKitItem.TYPE_TITLE -> {
                item.name?.let {
                    holder.getView<TextView>(R.id.tv_title_name).setText(it)
                }
            }
            MultiKitItem.TYPE_KIT -> {
                item.kit?.let {
                    holder.getView<TextView>(R.id.tv_kit_name).setText(it.name)
                    holder.getView<ImageView>(R.id.iv_icon).setImageResource(it.icon)
                }

            }

        }
    }


}