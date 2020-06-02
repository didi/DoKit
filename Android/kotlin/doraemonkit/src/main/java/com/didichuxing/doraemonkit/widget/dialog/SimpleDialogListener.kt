package com.didichuxing.doraemonkit.widget.dialog

/**
 * Created by wanglikun on 2019/4/12
 */
open class SimpleDialogListener : DialogListener {
    override fun onPositive(): Boolean {
        return false
    }

    override fun onNegative(): Boolean {
        return false
    }

    override fun onCancel(): Boolean {
        return false
    }
}