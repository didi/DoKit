package com.didichuxing.doraemonkit.kit.network.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.didichuxing.doraemonkit.R

class ListDropDownAdapter(private val context: Context, val list: List<String>) : BaseAdapter() {
    private var checkItemPosition = 0

    fun setCheckItem(position: Int) {
        checkItemPosition = position
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return list.size
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView != null) {
            viewHolder = convertView.tag as ViewHolder
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.dk_item_default_drop_down, null)
            viewHolder = ViewHolder(convertView)
            convertView.tag = viewHolder
        }
        fillValue(position, viewHolder)
        return convertView!!
    }

    override fun getItem(p0: Int): Any? {
        return null
    }

    private fun fillValue(position: Int, viewHolder: ViewHolder) {
        viewHolder.mText.text = list[position]
        if (checkItemPosition != -1) {
            if (checkItemPosition == position) {
                viewHolder.mText.setTextColor(context.resources.getColor(R.color.dk_drop_down_selected))
                viewHolder.mText.setBackgroundResource(R.color.dk_check_bg)
            } else {
                viewHolder.mText.setTextColor(context.resources.getColor(R.color.dk_drop_down_unselected))
                viewHolder.mText.setBackgroundResource(R.color.dk_color_FFFFFF)
            }
        }
    }

    internal class ViewHolder(contenView: View) {
        var mText: TextView

        init {
            mText = contenView.findViewById(R.id.text)
        }
    }

}