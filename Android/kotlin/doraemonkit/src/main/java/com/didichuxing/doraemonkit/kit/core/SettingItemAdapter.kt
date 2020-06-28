package com.didichuxing.doraemonkit.kit.core


import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.annotation.StringRes
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.widget.bravh.BaseQuickAdapter
import com.didichuxing.doraemonkit.widget.bravh.viewholder.BaseViewHolder


/**
 * Created by zhanys on 2020/06/08.
 * java版SettingItemAdapter使用BaseQuickAdapter重写
 */
class SettingItemAdapter : BaseQuickAdapter<SettingItem, BaseViewHolder> {
    // 兼容老代码
    constructor(context: Context?) : super(R.layout.dk_item_setting, null)

    constructor(data: MutableList<SettingItem>) : super(R.layout.dk_item_setting, data)

    private var mOnSettingItemClickListener: OnSettingItemClickListener? = null
    private var mOnSettingItemSwitchListener: OnSettingItemSwitchListener? = null

    override fun convert(holder: BaseViewHolder, item: SettingItem) {
        holder.setText(R.id.desc, item.desc)
        if (item.canCheck) {
            holder.getView<CheckBox>(R.id.menu_switch).apply {
                visibility = View.VISIBLE
                isChecked = item.isChecked
                setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                    if (isMatched(item.desc)) {
                        // todo
                        // if (AppHealthInfoUtil.getInstance().isAppHealthRunning()) {
                        //     buttonView.isChecked = true
                        //     return@OnCheckedChangeListener
                        // }
                    }
                    item.isChecked = isChecked
                    mOnSettingItemSwitchListener?.onSettingItemSwitch(buttonView, item, isChecked)
                })
            }
        }
        if (item.icon != 0) {
            holder.setVisible(R.id.right_icon, true)
            holder.setImageResource(R.id.right_icon, item.icon)
        }
        if (!TextUtils.isEmpty(item.rightDesc)) {
            holder.setVisible(R.id.right_desc, true)
            holder.setText(R.id.right_desc, item.rightDesc)
        }
    }

    /**
     * 是否命中
     */
    private fun isMatched(@StringRes desc: Int): Boolean {
        val resources = intArrayOf(
                R.string.dk_weak_network_switch,
                R.string.dk_item_block_switch,
                R.string.dk_crash_capture_switch,
                R.string.dk_cpu_detection_switch,
                R.string.dk_frameinfo_detection_switch,
                R.string.dk_ram_detection_switch)
        var isMatches = false
        for (res in resources) {
            if (res == desc) {
                isMatches = true
                break
            }
        }
        return isMatches
    }

    fun setOnSettingItemClickListener(onSettingItemClickListener: OnSettingItemClickListener?) {
        mOnSettingItemClickListener = onSettingItemClickListener
        setOnItemClickListener { adapter, view, position -> mOnSettingItemClickListener?.onSettingItemClick(view, data[position]) }
    }

    fun setOnSettingItemSwitchListener(onSettingItemSwitchListener: OnSettingItemSwitchListener?) {
        mOnSettingItemSwitchListener = onSettingItemSwitchListener
    }

    interface OnSettingItemClickListener {
        fun onSettingItemClick(view: View, data: SettingItem)
    }

    interface OnSettingItemSwitchListener {
        fun onSettingItemSwitch(view: View, data: SettingItem, on: Boolean)
    }

    /**
     * 列表末尾追加一个元素，兼容旧代码
     */
    fun append(item: SettingItem?) {
        item?.apply { addData(this) }
    }

    /**
     * 追加一个集合
     *
     * @param items
     */
    fun append(items: Collection<SettingItem>?) {
        if (items == null || items.isEmpty()) {
            return
        }
        items.apply { addData(this) }
    }

}
