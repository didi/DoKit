package com.didichuxing.doraemonkit.widget.recyclerview

import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView

/**
 * 简单封装的适用于RecyclerView的ViewHolder
 *
 * @author Jin Liang
 * @since 16/1/5
 */
abstract class AbsViewBinder<T>(protected val view: View) : RecyclerView.ViewHolder(view) {
    private var data: T? = null


    protected abstract fun getViews()
    fun <V : View?> getView(@IdRes id: Int): V {
        return view.findViewById<View>(id) as V
    }

    abstract fun bind(t: T)
    fun bind(t: T, position: Int) {
        bind(t)
    }

    protected open fun onViewClick(view: View?, data: T?) {}
    fun setData(data: T) {
        this.data = data
    }

    protected val context: Context
        protected get() = view.context

    init {
        getViews()
        view.setOnClickListener { onViewClick(view, data) }
    }
}