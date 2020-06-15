package com.didichuxing.doraemonkit.kit.fileexplorer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.SpInputType
import com.didichuxing.doraemonkit.kit.fileexplorer.widget.SpInputView
import com.didichuxing.doraemonkit.widget.recyclerview.AbsRecyclerAdapter
import com.didichuxing.doraemonkit.widget.recyclerview.AbsViewBinder

/**
 * @author lostjobs created on 2020/6/14
 */

typealias onSpBeanChanged = (SpBean) -> Unit

class SpAdapter(context: Context) : AbsRecyclerAdapter<AbsViewBinder<SpBean>, SpBean>(context) {

    private var onSpBeanChanged: onSpBeanChanged? = null

    override fun createViewHolder(view: View, viewType: Int): AbsViewBinder<SpBean> {
        return SharedPreferenceVH(view)
    }

    override fun createView(inflater: LayoutInflater, parent: ViewGroup?, viewType: Int): View {
        return inflater.inflate(R.layout.dk_item_sp_input, parent, false)
    }

    fun doOnSpBeanChanged(onSpBeanChanged: onSpBeanChanged){
        this.onSpBeanChanged = onSpBeanChanged
    }

    inner class SharedPreferenceVH(view: View) : AbsViewBinder<SpBean>(view), SpInputView.OnDataChangeListener {

        private val key: TextView = itemView.findViewById(R.id.tv_sp_key)
        private val type: TextView = itemView.findViewById(R.id.tv_sp_type)
        private val valueView: SpInputView = itemView.findViewById(R.id.input_sp_value)


        override fun onBind(data: SpBean, position: Int) {
            if (data.cls.simpleName == SpInputType.HASHSET) return
            key.text = data.key
            type.text = data.cls.simpleName
            valueView.setInput(data, this)
        }

        override fun onDataChanged() {
            valueView.refresh()
            data?.run {
                onSpBeanChanged?.invoke(this)
            }
        }
    }
}