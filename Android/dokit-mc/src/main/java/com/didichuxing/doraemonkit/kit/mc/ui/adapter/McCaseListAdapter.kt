package com.didichuxing.doraemonkit.kit.mc.ui.adapter

import android.widget.RadioButton
import android.widget.TextView
import com.didichuxing.doraemonkit.mc.R
import com.didichuxing.doraemonkit.widget.brvah.BaseQuickAdapter
import com.didichuxing.doraemonkit.widget.brvah.viewholder.BaseViewHolder

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/7/1-15:53
 * 描    述：
 * 修订历史：
 * ================================================
 */
class McCaseListAdapter(caseList: MutableList<McCaseInfo>) :
    BaseQuickAdapter<McCaseInfo, BaseViewHolder>(R.layout.dk_item_mc_case, caseList) {

    override fun convert(holder: BaseViewHolder, item: McCaseInfo) {
        holder.getView<TextView>(R.id.tv_name).text = "用例名称:${item.caseName}"
        holder.getView<TextView>(R.id.tv_person).text = "采集人:${item.personName}"
        holder.getView<TextView>(R.id.tv_caseid).text = "caseId:${item.caseId}"
        holder.getView<TextView>(R.id.tv_time).text = "采集时间:${item.time}"
        holder.getView<RadioButton>(R.id.rb).isChecked = item.isChecked
    }
}


data class McCaseInfo(
    val caseName: String,
    val personName: String,
    val time: String,
    var isChecked: Boolean,
    val caseId: String
)
