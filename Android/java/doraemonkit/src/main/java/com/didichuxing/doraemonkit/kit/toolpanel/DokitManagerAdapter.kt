package com.didichuxing.doraemonkit.kit.toolpanel

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.widget.brvah.BaseMultiItemQuickAdapter
import com.didichuxing.doraemonkit.widget.brvah.module.DraggableModule
import com.didichuxing.doraemonkit.widget.brvah.viewholder.BaseViewHolder

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/29-15:21
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DokitManagerAdapter(kitViews: MutableList<KitWrapItem>?)
    : BaseMultiItemQuickAdapter<KitWrapItem, BaseViewHolder>(kitViews), DraggableModule {

    init {
        addItemType(KitWrapItem.TYPE_TITLE, R.layout.dk_item_group_title)
        addItemType(KitWrapItem.TYPE_KIT, R.layout.dk_item_group_kit_manager)
    }

    override fun convert(holder: BaseViewHolder, item: KitWrapItem) {
        when (item.itemType) {
            KitWrapItem.TYPE_TITLE -> {
                item.name.let {
                    holder.getView<TextView>(R.id.tv_title_name).text = it
                }
            }
            KitWrapItem.TYPE_KIT -> {
                item.kit?.let {
                    holder.getView<TextView>(R.id.name).setText(it.name)
                    holder.getView<ImageView>(R.id.icon).setImageResource(it.icon)
                    if (DokitManagerFragment.IS_EDIT) {
                        holder.getView<ImageView>(R.id.iv_tag).visibility = View.VISIBLE
                        holder.getView<ImageView>(R.id.iv_tag).apply {
                            if (item.checked) {
                                setImageResource(R.mipmap.dk_kit_item_checked)
                            } else {
                                setImageResource(R.mipmap.dk_kit_item_normal)
                            }
                        }
                        if (item.checked) {
                            holder.getView<View>(R.id.view_mask).visibility = View.GONE
                        } else {
                            holder.getView<View>(R.id.view_mask).visibility = View.VISIBLE
                        }
                    } else {
                        holder.getView<ImageView>(R.id.iv_tag).visibility = View.GONE
                        holder.getView<View>(R.id.view_mask).visibility = View.GONE
                    }


                }

            }

        }
    }


}