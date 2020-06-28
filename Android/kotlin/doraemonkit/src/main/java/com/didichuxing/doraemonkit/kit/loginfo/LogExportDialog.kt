package com.didichuxing.doraemonkit.kit.loginfo

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.SettingItem
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter
import com.didichuxing.doraemonkit.widget.dialog.DialogListener
import com.didichuxing.doraemonkit.widget.dialog.DialogProvider

/**
 * @author lostjobs created on 2020/6/28
 */
class LogExportDialog(listener: DialogListener? = null) : DialogProvider<List<SettingItem>>(listOf(SettingItem(desc = R.string.dk_save), SettingItem(desc = R.string.dk_share)), listener) {

    var onButtonClickListener: OnButtonClickListener? = null

    private lateinit var mChooseList: RecyclerView
    private val mAdapter: SettingItemAdapter by lazy {
        SettingItemAdapter(context)
    }

    override val layoutId: Int = R.layout.dk_dialog_file_explorer_choose

    override fun findViews(view: View?) {
        val rootView = view ?: return
        mChooseList = rootView.findViewById(R.id.choose_list)
        mChooseList.layoutManager = LinearLayoutManager(context)
        mChooseList.adapter = mAdapter
    }

    override fun bindData(data: List<SettingItem>) {
        mAdapter.addData(data)
        mAdapter.setOnSettingItemClickListener(object : SettingItemAdapter.OnSettingItemClickListener {
            override fun onSettingItemClick(view: View, data: SettingItem) {
                when (data.desc) {
                    R.string.dk_save -> {
                        onButtonClickListener?.onSaveClick(this@LogExportDialog)
                    }
                    R.string.dk_share -> {
                        onButtonClickListener?.onShareClick(this@LogExportDialog)
                    }
                }
            }
        })
    }

    override val isCancellable: Boolean = false

    interface OnButtonClickListener {
        fun onSaveClick(dialog: LogExportDialog)
        fun onShareClick(dialog: LogExportDialog)
    }
}