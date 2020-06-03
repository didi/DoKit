package com.didichuxing.doraemonkit.widget.dialog

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.didichuxing.doraemonkit.R

/**
 * Created by wanglikun on 2019/4/12
 */
class CommonDialogProvider(data: DialogInfo?, listener: DialogListener?) : DialogProvider<DialogInfo?>(data, listener) {
    private lateinit var mPositive: TextView
    private lateinit var mNegative: TextView
    private lateinit var mTitle: TextView
    private lateinit var mDesc: TextView
    override val layoutId: Int
        get() = R.layout.dk_dialog_common

    override fun findViews(view: View?) {
        view?.let {
            mPositive = it.findViewById(R.id.positive)
            mNegative = it.findViewById(R.id.negative)
            mTitle = it.findViewById(R.id.title)
            mDesc = it.findViewById(R.id.desc)
        }

    }


    override fun bindData(data: DialogInfo?) {
        data?.let {
            mTitle.text = it.title
            if (TextUtils.isEmpty(it.desc)) {
                mDesc.visibility = View.GONE
            } else {
                mDesc.visibility = View.VISIBLE
                mDesc.text = it.desc
            }
        }

    }


    override val positiveView: View?
        get() = mPositive

    override val negativeView: View?
        get() = mNegative

    override val isCancellable: Boolean
        get() = false
}