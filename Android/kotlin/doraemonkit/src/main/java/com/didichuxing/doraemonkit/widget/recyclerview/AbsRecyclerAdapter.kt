package com.didichuxing.doraemonkit.widget.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * 内置一个List的通用、简化的适用于RecyclerView的Adapter。
 *
 *
 *
 * @author Jin Liang
 * @since 16/1/6
 */
abstract class AbsRecyclerAdapter<T : AbsViewBinder<V>, V>(var context: Context?) : RecyclerView.Adapter<T>() {


    companion object {
        private const val TAG = "AbsRecyclerAdapter"
    }

    protected var mList: MutableList<V> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T {
        val view = createView(LayoutInflater.from(parent.context), parent, viewType)
        return createViewHolder(view, viewType)
    }

    protected abstract fun createViewHolder(view: View, viewType: Int): T

    /**
     * 如果是通过LayoutInflater创建的View,不要绑定到父View,RecyclerView会负责添加。
     *
     * @param inflater
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract fun createView(inflater: LayoutInflater, parent: ViewGroup?, viewType: Int): View

    override fun onBindViewHolder(holder: T, position: Int) {
        val data = mList.get(position)
        holder.bindData(data)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    /**
     * 列表末尾追加一个元素
     *
     * @param item
     */
    fun append(item: V?) {
        if (item == null) {
            return
        }
        mList.add(item)
        notifyDataSetChanged()
    }

    /**
     * 在特定位置增加一个元素
     *
     * @param item
     * @param position
     */
    fun append(item: V?, position: Int) {
        var position = position
        if (item == null) {
            return
        }
        if (position < 0) {
            position = 0
        } else if (position > mList.size) {
            position = mList.size
        }
        mList.add(position, item)
        notifyDataSetChanged()
    }

    /**
     * 追加一个集合
     *
     * @param items
     */
    fun append(items: Collection<V>?) {
        if (items == null || items.isEmpty()) {
            return
        }
        mList.addAll(items)
        notifyDataSetChanged()
    }

    /**
     * 清空集合
     */
    fun clear() {
        if (mList.isEmpty()) {
            return
        }
        mList.clear()
        notifyDataSetChanged()
    }

    /**
     * 删除一个元素
     *
     * @param item
     */
    fun remove(item: V?) {
        if (item == null) {
            return
        }
        if (mList.contains(item)) {
            mList.remove(item)
            notifyDataSetChanged()
        }
    }

    /**
     * 删除一个元素
     *
     * @param index
     */
    fun remove(index: Int) {
        if (index < mList.size) {
            mList.removeAt(index)
            notifyDataSetChanged()
        }
    }

    /**
     * 删除一个集合
     *
     * @param items
     */
    fun remove(items: Collection<V>?) {
        if (items == null || items.size == 0) {
            return
        }
        if (mList.removeAll(items)) {
            notifyDataSetChanged()
        }
    }

    /**
     * 替换数据集合
     *
     * @param items
     */
    var data: Collection<V>?
        get() = ArrayList(mList)
        set(items) {
            if (items == null || items.size == 0) {
                return
            }
            if (mList.size > 0) {
                mList.clear()
            }
            mList.addAll(items)
            notifyDataSetChanged()
        }

}