package com.didichuxing.doraemonkit.kit.toolpanel

import android.widget.TextView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.widget.brvah.BaseQuickAdapter
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
class DokitSettingAdapter(datas: MutableList<String>)
    : BaseQuickAdapter<String, BaseViewHolder>(R.layout.dk_item_main_setting, datas) {

    override fun convert(holder: BaseViewHolder, name: String) {
        holder.getView<TextView>(R.id.tv_name).setText(name)
    }
}