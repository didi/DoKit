package com.didichuxing.doraemondemo.mc

import android.widget.Button
import android.widget.TextView
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.didichuxing.doraemondemo.R

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/7-14:48
 * 描    述：
 * 修订历史：
 * ================================================
 */
class RVAdapter(layoutResId: Int, data: MutableList<String>? = null) :
    BaseQuickAdapter<String, BaseViewHolder>(layoutResId, data) {
    companion object {
        const val TAG = "DemoAdapter"
    }

    override fun convert(holder: BaseViewHolder, item: String) {
        holder.getView<TextView>(R.id.tv).text = item
        holder.getView<Button>(R.id.btn).setOnClickListener {

            val position = holder.adapterPosition
            ToastUtils.showShort("rv item inner btn click==$position")
        }
    }
}