package com.didichuxing.doraemonkit.kit.dataclean

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.blankj.utilcode.util.PathUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.core.SettingItem
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter
import com.didichuxing.doraemonkit.util.DataCleanUtil
import com.didichuxing.doraemonkit.util.DokitUtil
import com.didichuxing.doraemonkit.util.FileUtil
import com.didichuxing.doraemonkit.widget.dialog.DialogInfo
import com.didichuxing.doraemonkit.widget.dialog.SimpleDialogListener
import com.didichuxing.doraemonkit.widget.recyclerview.DividerItemDecoration
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar
import java.io.File
import java.util.*

/**
 * Created by wanglikun on 2018/11/17.
 */
class DataCleanFragment : BaseFragment() {
    private lateinit var mSettingList: RecyclerView
    private lateinit var mSettingItemAdapter: SettingItemAdapter
    private lateinit var mItemWrap: LinearLayout
    private lateinit var mBtnClean: Button

    private lateinit var dirs: MutableList<String>

    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_data_clean
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val titleBar = findViewById<HomeTitleBar>(R.id.title_bar)
        titleBar.setListener { finish() }
        mSettingList = findViewById(R.id.setting_list)
        mItemWrap = findViewById(R.id.item_wrap)
        mBtnClean = findViewById(R.id.btn_clean)
        dirs = mutableListOf(DokitUtil.getString(R.string.dk_kit_cache_check_all))
        val innerDirs = File(PathUtils.getInternalAppDataPath()).listFiles()?.filter { file ->
            file.isDirectory
        }?.map { file ->
            file.name
        }?.asIterable()
        dirs.addAll(innerDirs!!)
        dirs.forEach {
            val item: RelativeLayout = LayoutInflater.from(activity).inflate(R.layout.dk_item_data_clean, null) as RelativeLayout
            item.findViewById<TextView>(R.id.tv_name).text = it
            item.findViewById<Switch>(R.id.switch_btn).isChecked = false
            item.setOnClickListener { innerItem ->
                val switch = innerItem.findViewById<Switch>(R.id.switch_btn)
                val name = innerItem.findViewById<TextView>(R.id.tv_name)
                if (name.text == DokitUtil.getString(R.string.dk_kit_cache_check_all)) {
                    if (switch.isChecked) {
                        for (index in 0 until mItemWrap.childCount) {
                            val itemView = mItemWrap.getChildAt(index)
                            itemView.findViewById<Switch>(R.id.switch_btn).isChecked = false
                        }

                    } else {
                        for (index in 0 until mItemWrap.childCount) {
                            val itemView = mItemWrap.getChildAt(index)
                            itemView.findViewById<Switch>(R.id.switch_btn).isChecked = true
                        }
                    }
                } else {
                    switch.isChecked = !switch.isChecked
                }

            }
            mItemWrap.addView(item)
        }
        val layoutManager = LinearLayoutManager(context)
        mSettingList.setLayoutManager(layoutManager)
        val settingItems: MutableList<SettingItem> = ArrayList()
        val settingItem = SettingItem(R.string.dk_kit_data_clean)
        settingItem.rightDesc = DataCleanUtil.getApplicationDataSizeStr(context)
        settingItems.add(settingItem)
        mSettingItemAdapter = SettingItemAdapter(context)
        mSettingItemAdapter.setData(settingItems)
        mBtnClean.setOnClickListener { view ->
            val dialogInfo = DialogInfo()
            dialogInfo.title = getString(R.string.dk_hint)
            dialogInfo.desc = getString(R.string.dk_app_data_clean)
            dialogInfo.listener = object : SimpleDialogListener() {
                override fun onPositive(): Boolean {
                    cleanCache()
                    mSettingItemAdapter.data[0].rightDesc = DataCleanUtil.getApplicationDataSizeStr(context)
                    mSettingItemAdapter.notifyDataSetChanged()
                    return true
                }

                override fun onNegative(): Boolean {
                    return true
                }
            }
            showDialog(dialogInfo)
        }
        mSettingList.setAdapter(mSettingItemAdapter)
        val decoration = DividerItemDecoration(DividerItemDecoration.VERTICAL)
        decoration.setDrawable(resources.getDrawable(R.drawable.dk_divider))
        mSettingList.addItemDecoration(decoration)
    }

    private fun cleanCache() {
        for (index in 1 until mItemWrap.childCount) {
            val item = mItemWrap.getChildAt(index)
            val name = item.findViewById<TextView>(R.id.tv_name)
            val switch = item.findViewById<Switch>(R.id.switch_btn)
            if (switch.isChecked) {
                val file = File(PathUtils.getInternalAppDataPath() + File.separator + name.text)
                if (file.isDirectory) {
                    FileUtil.deleteDirectory(file)
                }
            }
        }
    }
}