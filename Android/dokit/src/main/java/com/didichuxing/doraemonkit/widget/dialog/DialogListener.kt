package com.didichuxing.doraemonkit.widget.dialog

/**
 * Created by wanglikun on 2019/4/12
 */
interface DialogListener {
    fun onPositive(dialogProvider: DialogProvider<*>): Boolean
    fun onNegative(dialogProvider: DialogProvider<*>): Boolean
    fun onCancel(dialogProvider: DialogProvider<*>) {}
}