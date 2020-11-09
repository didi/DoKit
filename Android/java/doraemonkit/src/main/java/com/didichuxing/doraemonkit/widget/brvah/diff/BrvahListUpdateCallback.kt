package com.didichuxing.doraemonkit.widget.brvah.diff

import androidx.recyclerview.widget.ListUpdateCallback
import com.didichuxing.doraemonkit.widget.brvah.BaseQuickAdapter

class BrvahListUpdateCallback(private val mAdapter: BaseQuickAdapter<*, *>) : ListUpdateCallback {

    override fun onInserted(position: Int, count: Int) {
        mAdapter.notifyItemRangeInserted(position + mAdapter.headerLayoutCount, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        mAdapter.notifyItemRangeRemoved(position + mAdapter.headerLayoutCount, count)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        mAdapter.notifyItemMoved(fromPosition + mAdapter.headerLayoutCount, toPosition + mAdapter.headerLayoutCount)
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        mAdapter.notifyItemRangeChanged(position + mAdapter.headerLayoutCount, count, payload)
    }

}