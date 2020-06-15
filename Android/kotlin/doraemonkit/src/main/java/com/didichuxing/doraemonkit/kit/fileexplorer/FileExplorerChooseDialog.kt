package com.didichuxing.doraemonkit.kit.fileexplorer

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.SettingItem
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter
import com.didichuxing.doraemonkit.widget.dialog.DialogListener
import com.didichuxing.doraemonkit.widget.dialog.DialogProvider

/**
 * @author lostjobs created on 2020/6/14
 */

typealias onOptionClick = (FileExplorerChooseDialog, FileInfo) -> Unit

class FileExplorerChooseDialog(data: FileInfo, listener: DialogListener? = null) :
        DialogProvider<FileInfo>(data, listener) {

    private var mChooseList: RecyclerView? = null
    private val mSettingItemAdapter by lazy {
        SettingItemAdapter(context)
    }
    private var onDeleteClick: onOptionClick? = null
    private var onShareClick: onOptionClick? = null

    override val layoutId: Int = R.layout.dk_dialog_file_explorer_choose

    override fun findViews(view: View?) {
        mChooseList = view?.findViewById(R.id.choose_list)
        mChooseList?.run {
            layoutManager = LinearLayoutManager(context)
            adapter = mSettingItemAdapter
        }
    }

    override fun bindData(data: FileInfo) {
        if (data.isFile) {
            mSettingItemAdapter.append(SettingItem(R.string.dk_share))
        }
        mSettingItemAdapter.append(SettingItem(R.string.dk_delete))
        mSettingItemAdapter.setOnSettingItemClickListener(object :
                SettingItemAdapter.OnSettingItemClickListener {
            override fun onSettingItemClick(view: View, data: SettingItem) {
                if (data.desc == R.string.dk_delete) {
                    onDeleteClick?.invoke(this@FileExplorerChooseDialog, this@FileExplorerChooseDialog.mData)
                } else if (data.desc == R.string.dk_share) {
                    onShareClick?.invoke(this@FileExplorerChooseDialog, this@FileExplorerChooseDialog.mData)
                }
            }
        })
    }

    fun setOnShareClick(onOptionClick: onOptionClick): FileExplorerChooseDialog {
        this.onShareClick = onOptionClick
        return this
    }

    fun setOnDeleteClick(onOptionClick: onOptionClick): FileExplorerChooseDialog {
        this.onDeleteClick = onOptionClick
        return this
    }

    override val isCancellable: Boolean = true
}