package com.didichuxing.doraemonkit.kit.webdoor

import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.widget.bravh.BaseQuickAdapter
import com.didichuxing.doraemonkit.widget.bravh.viewholder.BaseViewHolder

/**
 * Created by guofeng007 on 2020/6/8
 */
class WebDoorHistoryAdapter(layoutResId: Int, data: MutableList<String>? = null) : BaseQuickAdapter<String, BaseViewHolder>(layoutResId, data) {

    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.content, item)

    }
}