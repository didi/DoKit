package com.didichuxing.doraemonkit.kit.sysinfo

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.PermissionUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.extensions.layoutInflater
import com.didichuxing.doraemonkit.widget.textview.LabelTextView
import java.lang.IllegalArgumentException

/**
 * @author lostjobs created on 2020/6/9
 */
class SysInfoItemAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  companion object {
    const val TYPE_SECTION = 0
    const val TYPE_INFO_ITEM = 1
    const val TYPE_UNKNOWN = -1
  }

  private var mData: ArrayList<SysInfoItem> = ArrayList()

  fun getItem(position: Int): SysInfoItem = mData[position]

  override fun getItemViewType(position: Int): Int {
    return getItem(position).type
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      TYPE_SECTION -> SectionVH(
        parent.layoutInflater.inflate(
          R.layout.dk_item_sys_title,
          parent,
          false
        )
      )
      TYPE_INFO_ITEM -> InfoItemVH(
        parent.layoutInflater.inflate(
          R.layout.dk_item_sys_info,
          parent,
          false
        )
      )
      else -> throw IllegalArgumentException("Illegal ViewType $viewType")
    }
  }

  override fun getItemCount(): Int = mData.size

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = getItem(position)
    when (item.type) {
      TYPE_SECTION -> {
        (holder as? SectionVH)?.bind(item)
      }
      TYPE_INFO_ITEM -> {
        (holder as? InfoItemVH)?.bind(item)
      }
    }
  }

  fun setData(sysInfoItems: MutableList<SysInfoItem>, notify: Boolean = true) {
    mData.clear()
    mData.addAll(sysInfoItems)
    if (notify)
      notifyDataSetChanged()
  }

  fun append(value: MutableList<SysInfoItem>, notify: Boolean = true) {
    mData.addAll(value)
    if (notify)
      notifyDataSetChanged()
  }

  class SectionVH(view: View) : RecyclerView.ViewHolder(view) {

    private var mTextView: TextView = itemView.findViewById(R.id.tv_title)

    fun bind(item: SysInfoItem) {
      mTextView.text = item.title
    }
  }

  class InfoItemVH(view: View) : RecyclerView.ViewHolder(view) {

    private var mLabelText: LabelTextView = itemView.findViewById(R.id.label_text)

    init {
      mLabelText.setOnClickListener {
        val bindItem =
          it.getTag(R.id.dokit_rv_bind_item) as? SysInfoItem ?: return@setOnClickListener
        if (bindItem.isPermission) {
          PermissionUtils.launchAppDetailsSettings()
        }
      }
    }

    fun bind(item: SysInfoItem) {
      mLabelText.setLabel(item.title)
      mLabelText.setText(item.value)
      mLabelText.setTag(R.id.dokit_rv_bind_item, item)
    }
  }
}