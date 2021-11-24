package com.kronos.dokit.pthread.ui

import android.text.TextUtils
import android.view.ViewGroup
import android.widget.TextView
import com.didichuxing.doraemonkit.widget.brvah.BaseQuickAdapter
import com.didichuxing.doraemonkit.widget.brvah.viewholder.BaseViewHolder
import com.kronos.dokit.pthread.PThreadEntity
import com.kronos.dokit.pthread.R
import com.kronos.dokit.pthread.gone
import com.kronos.dokit.pthread.visible

/**
 * @Author LiABao
 * @Since 2020/9/3
 */
class ThreadAdapter : BaseQuickAdapter<PThreadEntity, BaseViewHolder>(R.layout.recycler_view_thread_hook) {
    override fun convert(holder: BaseViewHolder, item: PThreadEntity) {
        if (!TextUtils.isEmpty(item.java)) {
            holder.itemView.findViewById<ViewGroup>(R.id.javaStackLayout).visible()
            holder.itemView.findViewById<TextView>(R.id.javaStackTv).text = item.java
        } else {
            holder.itemView.findViewById<ViewGroup>(R.id.javaStackLayout).gone()
        }
        if (!TextUtils.isEmpty(item.native)) {
            holder.itemView.findViewById<ViewGroup>(R.id.nativeStackLayout).visible()
            holder.itemView.findViewById<TextView>(R.id.nativeStackTv).text = item.native
        } else {
            holder.itemView.findViewById<ViewGroup>(R.id.nativeStackLayout).gone()
        }
        holder.itemView.findViewById<TextView>(R.id.threadCountTv).text = item.count.toString()
        holder.itemView.findViewById<TextView>(R.id.threadSampleTv).text = item.threads.firstOrNull()?.name ?: ""
        holder.itemView.findViewById<TextView>(R.id.positionTv).text = "第${holder.adapterPosition + 1}项"
        //   holder.itemView.findViewById<TextView>(R.id.threadStackTv).text = item.traceElement()
    }
}
