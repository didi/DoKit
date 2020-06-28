package com.didichuxing.doraemonkit.kit.network.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.network.okhttp.bean.NetworkRecord
import com.didichuxing.doraemonkit.kit.network.utils.ByteUtil
import com.didichuxing.doraemonkit.widget.recyclerview.AbsRecyclerAdapter
import com.didichuxing.doraemonkit.widget.recyclerview.AbsViewBinder
import java.text.SimpleDateFormat
import java.util.*

/**
 * @desc: 抓包列表页适配器
 */
public class NetworkListAdapter(context: Context) : AbsRecyclerAdapter<AbsViewBinder<NetworkRecord>, NetworkRecord>(context), Filterable {
    private var mListener: OnItemClickListener? = null
    private val mSourceList: MutableList<NetworkRecord> = ArrayList()
    override fun createViewHolder(view: View, viewType: Int): AbsViewBinder<NetworkRecord> {
        return ItemViewHolder(view)
    }


    override fun getFilter(): Filter {
        return mFilter
    }

    private inner class ItemViewHolder(view: View) : AbsViewBinder<NetworkRecord>(view) {
        private var url: TextView = view.findViewById(R.id.network_list_url)
        private var method: TextView=view.findViewById(R.id.network_list_method)
        private var code: TextView = view.findViewById(R.id.network_list_code)
        private var time: TextView = view.findViewById(R.id.network_list_time_and_cost)
        private var flow: TextView = view.findViewById(R.id.network_list_flow)
        private val mDateFormat = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS")


        override fun onBind(record: NetworkRecord, position: Int) {
            if (record.mRequest != null) {
                val request = record.mRequest
                url!!.text = request?.url
                val cost: String = if (record.endTime < record.startTime) {
                    Companion.UNKNOWN
                } else {
                    ((record.endTime - record.startTime).toFloat() / 1000f).toString() + "s"
                }
                val startTime = mDateFormat.format(Date(record.startTime))
                time!!.text = context.getString(R.string.dk_kit_network_time_format, startTime, cost)
            } else {
                url!!.text = Companion.UNKNOWN
                time!!.text = context.getString(R.string.dk_kit_network_time_format, Companion.UNKNOWN, Companion.UNKNOWN)
            }
            if (record.mResponse != null && record.mRequest != null) {
                val request = record.mRequest
                val response = record.mResponse
                method!!.text = String.format(Companion.METHOD_FORMAT, request?.method, response?.mimeType)
                code!!.text = String.format(Companion.CODE_FORMAT, response?.status)
            } else {
                code!!.text = Companion.UNKNOWN
                method!!.text = Companion.UNKNOWN
            }
            flow!!.text = String.format(Companion.FLOW_FORMAT, ByteUtil.getPrintSize(record.requestLength), ByteUtil.getPrintSize(record.responseLength))
            itemView.setOnClickListener {
                if (mListener != null) {
                    mListener!!.onClick(record)
                }
            }
        }




    }

    companion object {
        private const val METHOD_FORMAT = "%s>%s"
        private const val FLOW_FORMAT = "↑ %s ↓%s"
        private const val CODE_FORMAT = "[%d]"
        private const val UNKNOWN = "unknown"
    }

    private val mFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val charString = constraint.toString()
            var filteredList: MutableList<NetworkRecord> = ArrayList()
            if (charString.isEmpty()) {
                filteredList = mSourceList
            } else {
                for (record in mSourceList) {
                    //这里根据需求，添加匹配规则
                    if (record.filter(charString)) {
                        filteredList.add(record)
                    }
                }
            }
            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            val filteredList = results.values as MutableCollection<NetworkRecord>
            if (filteredList == null || filteredList.isEmpty()) {
                clear()
            } else {
                data=(filteredList)
            }
            notifyDataSetChanged()
        }
    }



    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onClick(info: NetworkRecord)
    }

    override fun createView(inflater: LayoutInflater, parent: ViewGroup?, viewType: Int): View {
        return inflater.inflate(R.layout.dk_item_network_list, parent, false)
    }


}