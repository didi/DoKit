package com.didichuxing.doraemonkit.kit.health

import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.widget.dialog.DialogListener
import com.didichuxing.doraemonkit.widget.dialog.DialogProvider

/**
 * Created by jint on 2019/4/12
 * update by pengyushan 2020-07-06
 * 完善健康体检用户信息dialog
 * @author jintai
 */
class UserInfoDialogProvider internal constructor(data: Any?, listener: DialogListener?) : DialogProvider<Any?>(data, listener) {
    private var mPositive: TextView? = null
    private var mNegative: TextView? = null
    private var mClose: TextView? = null
    private var mCaseName: EditText? = null
    private var mUserName: EditText? = null
    override val layoutId: Int
        get() = R.layout.dk_dialog_userinfo

    override fun findViews(view: View?) {
        view?.let {
            mPositive = view.findViewById(R.id.positive)
            mNegative = view.findViewById(R.id.negative)
            mClose = view.findViewById(R.id.close)
            mCaseName = view.findViewById(R.id.edit_case_name)
            mUserName = view.findViewById(R.id.edit_user_name)
        }
    }

    override fun bindData(data: Any?) {}
    override val positiveView = mPositive

    override val negativeView = mNegative

    override val cancelView = mClose

    /**
     * 上传健康体检数据
     */
    fun uploadAppHealthInfo(uploadAppHealthCallBack: UploadAppHealthCallback?): Boolean {
        if (!userInfoCheck()) {
            ToastUtils.showShort("请填写测试用例和测试人")
            return false
        }
        val caseName = mCaseName!!.text.toString()
        val userName = mUserName!!.text.toString()
        AppHealthInfoUtil.instance.setBaseInfo(caseName, userName)
        //上传数据
        AppHealthInfoUtil.instance.post(uploadAppHealthCallBack)
        return true
    }

    /**
     * 检查用户数据
     */
    private fun userInfoCheck(): Boolean {
        if (mCaseName == null || TextUtils.isEmpty(mCaseName!!.text.toString())) {
            return false
        }
        return !(mUserName == null || TextUtils.isEmpty(mUserName!!.text.toString()))
    }

    override val isCancellable: Boolean
        get() = false
}