package com.didichuxing.doraemonkit.kit.loginfo

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.loginfo.util.SearchCriteria
import com.didichuxing.doraemonkit.kit.loginfo.util.TagColorUtil
import com.didichuxing.doraemonkit.kit.loginfo.util.TagColorUtil.getTextColor
import com.didichuxing.doraemonkit.widget.recyclerview.AbsRecyclerAdapter
import com.didichuxing.doraemonkit.widget.recyclerview.AbsViewBinder
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author lostjobs created on 2020/6/28
 */
class LogItemAdapter(context: Context) : AbsRecyclerAdapter<AbsViewBinder<LogLine>, LogLine>(context), Filterable {

    private val mClipBoard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    private var mOriginalValues = ArrayList<LogLine>()
    private val mFilter: ArrayFilter = ArrayFilter()
    var logLevelLimit = Log.VERBOSE


    override fun createViewHolder(view: View, viewType: Int): AbsViewBinder<LogLine> {
        return LogInfoViewHolder(view)
    }

    override fun createView(inflater: LayoutInflater, parent: ViewGroup?, viewType: Int): View {
        return inflater.inflate(R.layout.dk_item_log, parent, false)
    }

    override fun getFilter(): Filter = mFilter

    fun clearLog() {
        if (mOriginalValues.isNotEmpty()) {
            mOriginalValues.clear()
        }
        clear()
        notifyDataSetChanged()
    }

    fun getTrueValues(): MutableCollection<LogLine> {
        return mOriginalValues
    }

    fun addWithFilter(logLine: LogLine, text: Editable?, notify: Boolean) {
        val inputList = Collections.singletonList(logLine)
        val filteredObjects = mFilter.performFilteringOnList(inputList, text)
        mOriginalValues.add(logLine)
        mList.addAll(filteredObjects)

        if (notify) {
            notifyItemRangeInserted(mList.size - filteredObjects.size, filteredObjects.size)
        }
    }

    fun removeFirst(numItemsToRemove: Int) {
        val subList = mOriginalValues.subList(numItemsToRemove, mOriginalValues.size)
        for (i in 0 until numItemsToRemove) {
            mList.remove(mOriginalValues[i])
        }
        mOriginalValues = ArrayList(subList)
        notifyDataSetChanged()
    }


    inner class LogInfoViewHolder(view: View) : AbsViewBinder<LogLine>(view) {
        private val mLogText: TextView = view.findViewById(R.id.log_output_text)
        private val mPid: TextView = view.findViewById(R.id.pid_text)
        private val mTime: TextView = view.findViewById(R.id.timestamp_text)
        private val mTag: TextView = view.findViewById(R.id.tag_text)
        private val mLevel: TextView = view.findViewById(R.id.log_level_text)

        override fun onBind(data: LogLine, position: Int) {
            mLevel.text = data.logLevelText.toString()
            mLevel.setTextColor(TagColorUtil.getLevelColor(context, data.logLevel))
            mLevel.setBackgroundColor(TagColorUtil.getLevelBgColor(context, data.logLevel))

            mPid.text = data.processId.toString()
            mTime.text = data.timestamp
            mLogText.text = data.logOutput

            updateContent(data)
        }

        private fun updateContent(data: LogLine) {
            if (data.expanded && data.processId != -1) {
                mLogText.isSingleLine = false
                mTime.visibility = View.VISIBLE
                mPid.visibility = View.VISIBLE
                mLogText.setTextColor(getTextColor(context, data.logLevel, true))
                mTag.setTextColor(getTextColor(context, data.logLevel, true))
                itemView.setBackgroundColor(Color.BLACK)
            } else {
                mLogText.isSingleLine = true
                mTime.visibility = View.GONE
                mPid.visibility = View.GONE
                itemView.setBackgroundColor(Color.WHITE)
                mLogText.setTextColor(getTextColor(context, data.logLevel, false))
                mTag.setTextColor(getTextColor(context, data.logLevel, false))
            }
        }

        override fun onViewClick(view: View, data: LogLine?) {
            super.onViewClick(view, data)
            val item = data ?: return
            item.expanded = !item.expanded
            updateContent(item)
        }

        override fun onViewLongClick(view: View, data: LogLine?): Boolean {
            val item = data ?: return false
            val clipData = ClipData.newPlainText("Label", item.originalLine())
            mClipBoard.setPrimaryClip(clipData)
            ToastUtils.showShort("copy success")
            return true
        }
    }

    private inner class ArrayFilter : Filter() {

        fun performFilteringOnList(inputList: List<LogLine>?, query: CharSequence?): ArrayList<LogLine> {
            val searchCriteria = SearchCriteria(query)

            // search by log level
            val allValues = ArrayList<LogLine>()
            val logLines = ArrayList(inputList!!)
            for (logLine in logLines) {
                if (logLine != null && logLine.logLevel >= logLevelLimit) {
                    allValues.add(logLine)
                }
            }
            var finalValues: ArrayList<LogLine> = allValues

            // search by criteria
            if (!searchCriteria.isEmpty) {
                val count = allValues.size
                val newValues = ArrayList<LogLine>(count)
                for (i in 0 until count) {
                    val value = allValues[i]
                    // search the logline based on the criteria
                    if (searchCriteria.matches(value)) {
                        newValues.add(value)
                    }
                }
                finalValues = newValues
            }
            return finalValues
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()

            val allValues = performFilteringOnList(mOriginalValues, constraint)

            results.values = allValues
            results.count = allValues.size

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            val result = results?.values ?: return
            mList = (result as List<*>).filterIsInstance(LogLine::class.java).toMutableList()
            if (results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetChanged()
            }
        }

    }

}