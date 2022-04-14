package com.didichuxing.doraemonkit.kit.mc.ui.adapter

import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.didichuxing.doraemonkit.kit.test.mock.data.CaseInfo
import com.didichuxing.doraemonkit.mc.R
import com.didichuxing.doraemonkit.widget.dialog.DialogListener
import com.didichuxing.doraemonkit.widget.dialog.DialogProvider

/**
 * Created by jint on 2019/4/12
 * 完善健康体检用户信息dialog
 * @author jintai
 */
class McCaseInfoDialogProvider internal constructor(data: Any?, listener: DialogListener?) :
    DialogProvider<Any?>(data, listener) {
    private lateinit var mPositive: TextView
    private lateinit var mNegative: TextView
    private lateinit var mCaseName: EditText
    private lateinit var mPersonName: EditText
    override fun getLayoutId(): Int {
        return R.layout.dk_dialog_mc_case_info
    }

    override fun findViews(view: View) {
        mPositive = view.findViewById(R.id.positive)
        mNegative = view.findViewById(R.id.negative)
        mCaseName = view.findViewById(R.id.edit_case_name)
        mPersonName = view.findViewById(R.id.edit_user_name)
    }


    override fun getPositiveView(): View {
        return mPositive
    }

    override fun getNegativeView(): View {
        return mNegative
    }

    override fun getCancelView(): View? {
        return null
    }

    fun getCaseInfo(): CaseInfo {
        return CaseInfo(
            caseName = mCaseName.text.toString(),
            personName = mPersonName.text.toString()
        )
    }

    override fun isCancellable(): Boolean {
        return false
    }


}
