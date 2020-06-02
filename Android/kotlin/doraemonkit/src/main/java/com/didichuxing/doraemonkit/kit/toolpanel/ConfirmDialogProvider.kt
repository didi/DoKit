package com.didichuxing.doraemonkit.kit.toolpanel

import android.view.View
import android.widget.TextView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.widget.dialog.DialogListener
import com.didichuxing.doraemonkit.widget.dialog.DialogProvider

/**
 * Created by jint on 2019/4/12
 * 确认弹框
 *
 * @author jintai
 */
class ConfirmDialogProvider(data: Any?, listener: DialogListener?) : DialogProvider<Any?>(data, listener) {
    private lateinit var mTvContent: TextView
    private lateinit var mTvPositive: TextView
    private lateinit var mTvNegative: TextView

    override val positiveView = mTvPositive
    override val negativeView = mTvNegative
    override val layoutId: Int
        get() = R.layout.dk_dialog_confirm

    override val isCancellable: Boolean
        get() = false


    override fun findViews(view: View?) {
        view?.let {
            mTvContent = view.findViewById(R.id.tv_content)
            mTvPositive = view.findViewById(R.id.positive)
            mTvNegative = view.findViewById(R.id.negative)
        }

    }

    override fun bindData(data: Any?) {
        if (data is String) {
            mTvContent.text = data
        }

    }


}