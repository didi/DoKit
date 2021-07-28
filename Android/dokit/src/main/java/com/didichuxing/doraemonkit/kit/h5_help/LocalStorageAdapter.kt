package com.didichuxing.doraemonkit.kit.h5_help

import android.widget.TextView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.h5_help.bean.StorageBean
import com.didichuxing.doraemonkit.widget.brvah.BaseQuickAdapter
import com.didichuxing.doraemonkit.widget.brvah.viewholder.BaseViewHolder

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/9/2-18:25
 * 描    述：
 * 修订历史：
 * ================================================
 */
class LocalStorageAdapter(
    list: MutableList<StorageBean>
) : BaseQuickAdapter<StorageBean, BaseViewHolder>(
    R.layout.dk_item_localstorage,
    list
) {
    override fun convert(holder: BaseViewHolder, item: StorageBean) {
        holder.getView<TextView>(R.id.tv_key).text = item.key
        holder.getView<TextView>(R.id.tv_value).text = item.value
    }
}