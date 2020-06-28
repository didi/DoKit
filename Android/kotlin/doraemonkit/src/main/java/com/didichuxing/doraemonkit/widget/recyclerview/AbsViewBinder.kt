package com.didichuxing.doraemonkit.widget.recyclerview

import android.content.Context
import android.view.View
<<<<<<< HEAD
import androidx.annotation.IdRes
=======
>>>>>>> origin/kotlin
import androidx.recyclerview.widget.RecyclerView

/**
 * 简单封装的适用于RecyclerView的ViewHolder
 *
 * @author Jin Liang
 * @since 16/1/5
 */
abstract class AbsViewBinder<T>(private val mView: View) : RecyclerView.ViewHolder(mView) {
    protected var data: T? = null
    fun bindData(data: T) {
        this.data = data
        onBind(data, adapterPosition)
    }

    protected abstract fun onBind(data: T, position: Int)
    protected open fun onViewClick(view: View, data: T?) {}
    protected open fun onViewLongClick(view: View, data: T?): Boolean {
        return false
    }


    protected val context: Context
        get() = mView.context

    init {
        mView.setOnClickListener { onViewClick(mView, data) }
        mView.setOnLongClickListener { onViewLongClick(itemView, data) }
    }
}