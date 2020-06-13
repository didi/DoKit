package com.didichuxing.doraemonkit.widget.recyclerview

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * 简单封装的适用于RecyclerView的ViewHolder
 *
 * @author Jin Liang
 * @since 16/1/5
 */
abstract class AbsViewBinder<T>(private val mView: View) : RecyclerView.ViewHolder(mView) {
    private var data: T? = null
    abstract fun bind(t: T, position: Int)
    protected fun onViewClick(view: View?, data: T?) {}

    fun setData(data: T) {
        this.data = data
    }

    protected val context: Context
        protected get() = mView.context

    init {
        mView.setOnClickListener { onViewClick(mView, data) }
    }
}