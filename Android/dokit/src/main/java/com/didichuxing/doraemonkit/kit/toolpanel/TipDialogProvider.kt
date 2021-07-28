package com.didichuxing.doraemonkit.kit.toolpanel

import android.view.View
import android.widget.TextView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.widget.dialog.DialogListener
import com.didichuxing.doraemonkit.widget.dialog.DialogProvider

/**
 * Created by jint on 2019/4/12
 * 完善健康体检用户信息dialog
 *
 * @author jintai
 */
class TipDialogProvider internal constructor(data: Any?, listener: DialogListener?) : DialogProvider<Any?>(data, listener) {
    private lateinit var mTip: TextView

    override fun getLayoutId(): Int {
        return R.layout.dk_dialog_tip
    }

    override fun findViews(view: View) {
        mTip = view.findViewById(R.id.tv_tip)
    }

    override fun bindData(data: Any?) {
        if (data is String) {
            mTip.text = data
        }
    }
}