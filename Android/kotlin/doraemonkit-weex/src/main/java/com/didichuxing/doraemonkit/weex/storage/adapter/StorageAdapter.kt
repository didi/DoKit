package com.didichuxing.doraemonkit.weex.storage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.didichuxing.doraemonkit.weex.R
import com.didichuxing.doraemonkit.weex.storage.StorageInfo
import com.didichuxing.doraemonkit.widget.recyclerview.AbsRecyclerAdapter
import com.didichuxing.doraemonkit.widget.recyclerview.AbsViewBinder

/**
 * Transformed by alvince on 2020/7/1
 *
 * @author haojianglong
 * @date 2019-06-18
 */
class StorageAdapter(context: Context) : AbsRecyclerAdapter<StorageAdapter.ViewHolder, StorageInfo>(context) {

    interface OnItemClickListener {
        /** 点击事件 */
        fun onItemClick(info: StorageInfo)
    }

    companion object {
        private const val TITLE_BACKGROUND_COLOR: Int = 0xFFBBBBBB.toInt()
        private const val NORMAL_BACKGROUND_COLOR: Int = 0xFFCDCDCD.toInt()

        private const val PRE_HOLDER_KEY: String = "key"
        private const val PRE_HOLDER_VALUE = "value"
    }

    var onItemClickListener: OnItemClickListener? = null

    init {
        append(StorageInfo(PRE_HOLDER_KEY, PRE_HOLDER_VALUE))
    }

    override fun createViewHolder(view: View, viewType: Int): ViewHolder =
        ViewHolder(view) {
            onItemClickListener?.onItemClick(it)
        }

    override fun createView(inflater: LayoutInflater, parent: ViewGroup?, viewType: Int): View =
        inflater.inflate(R.layout.dk_item_storage_watch, parent, false)


    class ViewHolder(itemView: View, private val onHolderSelect: (StorageInfo) -> Unit) :
        AbsViewBinder<StorageInfo>(itemView) {

        private val keyTextView: TextView?
            get() = itemView.findViewById(R.id.tv_tip_key)

        private val valueTextView: TextView?
            get() = itemView.findViewById(R.id.tv_tip_value)

        override fun onBind(data: StorageInfo, position: Int) {
            if (adapterPosition == 0) {
                TITLE_BACKGROUND_COLOR
            } else {
                NORMAL_BACKGROUND_COLOR
            }.also { color ->
                keyTextView?.setBackgroundColor(color)
                valueTextView?.setBackgroundColor(color)
            }

            keyTextView?.text = data.key
            valueTextView?.text = data.value

            itemView.setOnClickListener {
                data.takeIf { adapterPosition != 0 }
                    ?.also {
                        onHolderSelect.invoke(it)
                    }
            }
        }
    }

}