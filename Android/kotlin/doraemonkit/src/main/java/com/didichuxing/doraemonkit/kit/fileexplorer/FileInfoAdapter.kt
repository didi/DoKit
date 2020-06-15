package com.didichuxing.doraemonkit.kit.fileexplorer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.widget.recyclerview.AbsRecyclerAdapter
import com.didichuxing.doraemonkit.widget.recyclerview.AbsViewBinder

/**
 * @author lostjobs created on 2020/6/14
 */

typealias onItemClick = (View, FileInfo) -> Unit

class FileInfoAdapter(context: Context) :
        AbsRecyclerAdapter<AbsViewBinder<FileInfo>, FileInfo>(context) {

    private var onItemClick: onItemClick? = null
    private var onItemLongClick: onItemClick? = null

    override fun createViewHolder(view: View, viewType: Int): AbsViewBinder<FileInfo> {
        return FileInfoVH(view)
    }

    override fun createView(inflater: LayoutInflater, parent: ViewGroup?, viewType: Int): View {
        return inflater.inflate(R.layout.dk_item_file_info, parent, false)
    }

    fun doOnViewLongClick(onItemLongClick: onItemClick?) {
        this.onItemLongClick = onItemLongClick
    }

    fun doOnViewClick(onItemClick: onItemClick?) {
        this.onItemClick = onItemClick
    }

    inner class FileInfoVH(view: View) : AbsViewBinder<FileInfo>(view) {

        private val mName: TextView = itemView.findViewById(R.id.name)
        private val mIcon: ImageView = itemView.findViewById(R.id.icon)
        private val mMoreBtn: ImageView = itemView.findViewById(R.id.more)

        override fun onViewClick(view: View, data: FileInfo?) {
            super.onViewClick(view, data)
            data?.run {
                onItemClick?.invoke(view, this)
            }
        }

        override fun onViewLongClick(view: View, data: FileInfo?): Boolean {
            val longClick = onItemLongClick
            if (null != data && null != longClick) {
                longClick.invoke(view, data)
                return true
            }
            return super.onViewLongClick(view, data)
        }

        override fun onBind(data: FileInfo, position: Int) {
            mName.text = data.file.name
            if (data.isDirectory) {
                mIcon.setImageResource(R.mipmap.dk_dir_icon)
                mMoreBtn.isVisible = true
            } else {
                mIcon.setImageResource(
                        when {
                            data.isJPG -> {
                                R.mipmap.dk_jpg_icon
                            }
                            data.isDB -> {
                                R.mipmap.dk_file_db
                            }
                            data.isTxt -> {
                                R.mipmap.dk_txt_icon
                            }
                            else -> {
                                R.mipmap.dk_file_icon
                            }
                        }
                )
                mMoreBtn.isVisible = false
            }
        }
    }
}