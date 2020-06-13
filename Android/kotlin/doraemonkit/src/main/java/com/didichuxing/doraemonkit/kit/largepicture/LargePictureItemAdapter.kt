package com.didichuxing.doraemonkit.kit.largepicture

import android.text.TextUtils
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.SettingItem
import com.didichuxing.doraemonkit.widget.bravh.BaseQuickAdapter
import com.didichuxing.doraemonkit.widget.bravh.viewholder.BaseViewHolder

/**
 * @author:  maple
 * @time:  2020/6/8 - 14:35
 * @desc: 大图配置列表
 */
class LargePictureItemAdapter()
    : BaseQuickAdapter<SettingItem, BaseViewHolder>(R.layout.dk_item_setting, mutableListOf()) {
    private var mOnSettingItemClickListener: LargePictureItemAdapter.OnSettingItemClickListener? = null
    private var mOnSettingItemSwitchListener: LargePictureItemAdapter.OnSettingItemSwitchListener? = null
    override fun convert(holder: BaseViewHolder, settingItem: SettingItem) {

        val mMenuSwitch = holder.getView<CheckBox>(R.id.menu_switch)
        val mDesc = holder.getView<TextView>(R.id.desc)
        val mIcon = holder.getView<ImageView>(R.id.right_icon)
        val mRightDesc = holder.getView<TextView>(R.id.right_desc)
        mDesc.setText(settingItem.desc)
        if (settingItem.canCheck) {
            mMenuSwitch.visibility = View.VISIBLE
            mMenuSwitch.isChecked = settingItem.isChecked
            mMenuSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                settingItem.isChecked = isChecked
                mOnSettingItemSwitchListener!!.onSettingItemSwitch(mMenuSwitch, settingItem, isChecked)
            }
        }
        if (settingItem.icon != 0) {
            mIcon.visibility = View.VISIBLE
            mIcon.setImageResource(settingItem.icon)
        }
        if (!TextUtils.isEmpty(settingItem.rightDesc)) {
            mRightDesc.visibility = View.VISIBLE
            mRightDesc.text = settingItem.rightDesc
        }
        holder.itemView.setOnClickListener { v->
            mOnSettingItemClickListener!!.onSettingItemClick(v, settingItem)
        }
    }

    fun setOnSettingItemClickListener(onSettingItemClickListener: OnSettingItemClickListener) {
        mOnSettingItemClickListener = onSettingItemClickListener
    }

    fun setOnSettingItemSwitchListener(onSettingItemSwitchListener: OnSettingItemSwitchListener) {
        mOnSettingItemSwitchListener = onSettingItemSwitchListener
    }

    interface OnSettingItemClickListener {
        fun onSettingItemClick(view: View?, data: SettingItem?)
    }

    interface OnSettingItemSwitchListener {
        fun onSettingItemSwitch(view: View?, data: SettingItem?, on: Boolean)
    }
}