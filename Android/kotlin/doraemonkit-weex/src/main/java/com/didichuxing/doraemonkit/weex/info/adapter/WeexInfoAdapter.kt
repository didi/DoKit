package com.didichuxing.doraemonkit.weex.info.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import com.didichuxing.doraemonkit.weex.R
import com.didichuxing.doraemonkit.weex.info.WeexInfo
import com.didichuxing.doraemonkit.weex.util.getDimensionPixel

/**
 * Transformed by alvince on 2020/6/30
 *
 * @author haojianglong
 * @date 2019-06-25
 */
class WeexInfoAdapter(private val context: Context) : BaseAdapter() {

    private val weexInfoList = mutableListOf<WeexInfo>()

    override fun getItem(position: Int): WeexInfo? = position
        .takeIf { it in 0 until count }
        ?.let { weexInfoList[it] }

    override fun getItemId(position: Int): Long = 0L

    override fun getCount(): Int = weexInfoList.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.dk_fragment_info_item, null)
        return view.also { v ->
            val holder = v.tag
                ?.let { it as? ViewHolder }
                ?: ViewHolder().apply {
                    keyText = v.findViewById(R.id.info_item_key)
                    valueText = v.findViewById(R.id.info_item_value)
                    v.tag = this
                }
            holder.apply {
                getItem(position)?.also { info ->
                    keyText?.bind(info.key)
                    valueText?.bind(info.value)
                }
                v.layoutParams = RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    context.getDimensionPixel(R.dimen.dk_title_height)
                )
            }
        }
    }

    fun setWeexInfos(list: List<WeexInfo>) {
        weexInfoList.apply {
            clear()
            addAll(list)
        }
    }

    private fun TextView.bind(text: String?) {
        (text ?: "").also { t ->
            if (TextUtils.isEmpty(text)) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
                this.text = t
            }
        }
    }


    internal class ViewHolder {
        var keyText: TextView? = null
        var valueText: TextView? = null
    }

}