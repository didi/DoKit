package com.didichuxing.doraemonkit.widget.dialog

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/1-11:32
 * 描    述：
 * 修订历史：
 * ================================================
 */
interface DialogListener {

    fun onPositive(): Boolean


    fun onNegative(): Boolean


    fun onCancel(): Boolean

}