package com.didichuxing.doraemonkit.kit.toolpanel

import android.os.Process
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.didichuxing.doraemonkit.BuildConfig
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.SharedPrefsKey
import com.didichuxing.doraemonkit.util.*
import com.didichuxing.doraemonkit.widget.brvah.BaseMultiItemQuickAdapter
import com.didichuxing.doraemonkit.widget.brvah.viewholder.BaseViewHolder

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/29-15:21
 * 描    述：
 * 修订历史：
 * ================================================
 */
class ToolPanelAdapter(kitViews: MutableList<KitWrapItem>?) :
    BaseMultiItemQuickAdapter<KitWrapItem, BaseViewHolder>(kitViews) {

    init {
        addItemType(KitWrapItem.TYPE_TITLE, R.layout.dk_item_group_title)
        addItemType(KitWrapItem.TYPE_KIT, R.layout.dk_item_kit)
        addItemType(KitWrapItem.TYPE_MODE, R.layout.dk_item_group_mode)
        addItemType(KitWrapItem.TYPE_EXIT, R.layout.dk_item_group_exit)
        addItemType(KitWrapItem.TYPE_VERSION, R.layout.dk_item_group_version)
    }

    override fun convert(holder: BaseViewHolder, item: KitWrapItem) {
        when (item.itemType) {
            KitWrapItem.TYPE_TITLE -> {
                item.name.let {
                    if (it == DoKitCommUtil.getString(R.string.dk_category_platform)) {
                        holder.getView<TextView>(R.id.tv_sub_title_name).visibility = View.VISIBLE
                        holder.getView<TextView>(R.id.tv_sub_title_name).text = "(www.dokit.cn)"
                    } else {
                        holder.getView<TextView>(R.id.tv_sub_title_name).visibility = View.GONE
                    }
                    holder.getView<TextView>(R.id.tv_title_name).text = it
                }
            }
            KitWrapItem.TYPE_KIT -> {
                item.kit?.let {
                    holder.getView<TextView>(R.id.name).setText(it.name)
                    holder.getView<ImageView>(R.id.icon).setImageResource(it.icon)
                }

            }

            KitWrapItem.TYPE_MODE -> {
                val radioGroup = holder.getView<RadioGroup>(R.id.rb_group)
                val rbNormal = holder.getView<RadioButton>(R.id.rb_normal)
                val rbSystem = holder.getView<RadioButton>(R.id.rb_system)
                radioGroup.setOnCheckedChangeListener { _, checkedId ->
                    if (checkedId == R.id.rb_normal) {
                        //选中normal
                        DoKitSPUtil.putString(SharedPrefsKey.FLOAT_START_MODE, "normal")
                    } else {
                        //选中系统
                        DoKitSPUtil.putString(SharedPrefsKey.FLOAT_START_MODE, "system")
                    }
                }

                rbNormal.setOnClickListener {
                    rbNormal.postDelayed({
                        AppUtils.relaunchApp()
                        Process.killProcess(Process.myPid())
                        System.exit(1)
                    }, 500)
                }

                rbSystem.setOnClickListener {
                    rbSystem.postDelayed({
                        AppUtils.relaunchApp()
                        Process.killProcess(Process.myPid())
                        System.exit(1)
                    }, 500)
                }

                val floatMode = DoKitSPUtil.getString(SharedPrefsKey.FLOAT_START_MODE, "normal")
                if (floatMode == "normal") {
                    rbNormal.isChecked = true
                } else {
                    rbSystem.isChecked = true
                }
            }

            KitWrapItem.TYPE_EXIT -> {
                holder.getView<TextView>(R.id.close).setOnClickListener {
                    DoKit.hideToolPanel()
                    DoKit.hide()
                }

            }

            KitWrapItem.TYPE_VERSION -> {
                val name = holder.getView<TextView>(R.id.version)
                //适配无法准确获取底部导航栏高度的bug
                if (name.parent != null) {
                    (name.parent as ViewGroup).setPadding(0, 0, 0, BarUtils.getNavBarHeight())
                }
                val version: String = DoKitCommUtil.getString(R.string.dk_kit_version)
                name.text = String.format(version, BuildConfig.DOKIT_VERSION)

            }
        }
    }


}
