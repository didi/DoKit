package com.didichuxing.doraemonkit.kit.blockmonitor

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.blockmonitor.bean.BlockInfo
import com.didichuxing.doraemonkit.widget.recyclerview.AbsRecyclerAdapter
import com.didichuxing.doraemonkit.widget.recyclerview.AbsViewBinder

class BlockListAdapter(context: Context?) : AbsRecyclerAdapter<AbsViewBinder<BlockInfo?>, BlockInfo?>(context!!) {
    private var mListener: OnItemClickListener? = null

    override fun createViewHolder(view: View, viewType: Int): AbsViewBinder<BlockInfo?> {
        return ItemViewHolder(view)
    }

    override fun createView(inflater: LayoutInflater, parent: ViewGroup?, viewType: Int): View {
        return inflater!!.inflate(R.layout.dk_item_block_list, parent, false)
    }

    private inner class ItemViewHolder(view: View) : AbsViewBinder<BlockInfo?>(view) {
        private val tvTime: TextView = view.findViewById(R.id.time)
        private val tvTitle: TextView = view.findViewById(R.id.title)


        override fun bind(info: BlockInfo?, position: Int) {
            info?.let {
                val index: String = (this@BlockListAdapter.itemCount - position).toString() + ". "
                val title = index + info.concernStackString + " " +
                        context.getString(R.string.dk_block_class_has_blocked, info.timeCost.toString())
                tvTitle.text = title
                val time = DateUtils.formatDateTime(context,
                        info.time, DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE)
                tvTime.text = time
                itemView.setOnClickListener {
                    mListener?.onClick(info)
                }
            }

        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onClick(info: BlockInfo?)
    }


}