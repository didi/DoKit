package com.didichuxing.doraemonkit.kit.toolpanel

import androidx.annotation.LayoutRes
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.toolpanel.bean.MorePageGroupBean
import com.didichuxing.doraemonkit.widget.brvah.BaseSectionQuickAdapter
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
class DokitMoreAdapter(
    @LayoutRes headerId: Int,
    @LayoutRes contentId: Int,
    groups: MutableList<MorePageGroupBean.DataBean.GroupBean.ListBean>
) : BaseSectionQuickAdapter<MorePageGroupBean.DataBean.GroupBean.ListBean, BaseViewHolder>(
    headerId,
    contentId,
    groups
) {

    init {
        addChildClickViewIds(R.id.more_item)
    }

    override fun convertHeader(
        helper: BaseViewHolder,
        item: MorePageGroupBean.DataBean.GroupBean.ListBean
    ) {
        helper.setText(R.id.tv_title, item.name)
    }

    override fun convert(
        holder: BaseViewHolder,
        item: MorePageGroupBean.DataBean.GroupBean.ListBean
    ) {
        holder.setText(R.id.tv_name, item.name)
        holder.setText(R.id.tv_desc, item.desc)
    }


}