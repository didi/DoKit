package com.didichuxing.doraemonkit.kit.fileexplorer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.widget.recyclerview.AbsRecyclerAdapter
import com.didichuxing.doraemonkit.widget.recyclerview.AbsViewBinder

/**
 * @author lostjobs created on 2020/6/14
 */
class TextContentAdapter(context: Context) : AbsRecyclerAdapter<AbsViewBinder<String>, String>(context) {

    override fun createViewHolder(view: View, viewType: Int): AbsViewBinder<String> {
        return TextLineVH(view)
    }

    override fun createView(inflater: LayoutInflater, parent: ViewGroup?, viewType: Int): View {
        return inflater.inflate(R.layout.dk_item_text_content, parent, false)
    }


    class TextLineVH(view: View) : AbsViewBinder<String>(view) {

        private val mTextView = itemView.findViewById<TextView>(R.id.text)

        override fun onBind(data: String, position: Int) {
            mTextView.text = data
        }
    }
}