package com.didichuxing.doraemondemo.old

import android.widget.Button
import androidx.annotation.LayoutRes
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.didichuxing.doraemondemo.R

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/17-11:01
 * 描    述：
 * 修订历史：
 * ================================================
 */
class MainAdapter(@LayoutRes layoutId: Int, data: MutableList<String>) :
    BaseQuickAdapter<String, BaseViewHolder>(layoutId, data) {
    override fun convert(holder: BaseViewHolder, item: String) {
        holder.getView<Button>(R.id.btn).text = item
    }
}
