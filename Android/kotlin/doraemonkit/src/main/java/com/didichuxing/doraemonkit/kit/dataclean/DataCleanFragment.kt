package com.didichuxing.doraemonkit.kit.dataclean

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.core.SettingItem
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter
import com.didichuxing.doraemonkit.util.DataCleanUtil.cleanApplicationData
import com.didichuxing.doraemonkit.util.DataCleanUtil.getApplicationDataSizeStr
import com.didichuxing.doraemonkit.widget.dialog.DialogInfo
import com.didichuxing.doraemonkit.widget.dialog.SimpleDialogListener
import com.didichuxing.doraemonkit.widget.recyclerview.DividerItemDecoration
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar.OnTitleBarClickListener
import java.util.*

/**
 * Created by luhongyan on 2020/06/13.
 */
class DataCleanFragment : BaseFragment() {
    private var mSettingList: RecyclerView? = null
    private var mSettingItemAdapter: SettingItemAdapter? = null
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_data_clean
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val titleBar = findViewById<HomeTitleBar>(R.id.title_bar)
        titleBar.mListener = object : OnTitleBarClickListener {
            override fun onRightClick() {
                finish()
            }
        }
        mSettingList = findViewById(R.id.setting_list)
        val layoutManager = LinearLayoutManager(context)
        mSettingList!!.setLayoutManager(layoutManager)
        val settingItems: MutableList<SettingItem> = ArrayList()
        val settingItem = SettingItem(R.string.dk_kit_data_clean, R.mipmap.dk_more_icon, false)
        settingItem.rightDesc = getApplicationDataSizeStr(context!!)
        settingItems.add(settingItem)
        mSettingItemAdapter = SettingItemAdapter(context)
        mSettingItemAdapter!!.data = settingItems
        mSettingItemAdapter!!.setOnSettingItemClickListener(object : SettingItemAdapter.OnSettingItemClickListener {
            override fun onSettingItemClick(view: View, data: SettingItem) {
                if (data?.desc == R.string.dk_kit_data_clean) {
                    val dialogInfo = DialogInfo()
                    dialogInfo.title = getString(R.string.dk_hint)
                    dialogInfo.desc = getString(R.string.dk_app_data_clean)
                    dialogInfo.listener = object : SimpleDialogListener() {
                        override fun onPositive(): Boolean {
                            cleanApplicationData(context!!)
                            data.rightDesc = getApplicationDataSizeStr(context!!)
                            mSettingItemAdapter!!.notifyDataSetChanged()
                            return true
                        }

                        override fun onNegative(): Boolean {
                            return true
                        }
                    }
                    showDialog(dialogInfo)
                }
            }

        })
        mSettingList!!.setAdapter(mSettingItemAdapter)
        val decoration = DividerItemDecoration(DividerItemDecoration.VERTICAL)
        decoration.setDrawable(resources.getDrawable(R.drawable.dk_divider))
        mSettingList!!.addItemDecoration(decoration)
    }
}