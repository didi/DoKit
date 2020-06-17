package com.didichuxing.doraemonkit.widget.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

/**
 * Created by wanglikun on 2019/4/12
 */
abstract class DialogProvider<T>(protected val mData: T, private var mDialogListener: DialogListener? = null) {
    var host: DialogFragment? = null
    private var mView: View? = null

    open val isCancellable = true

    val context: Context?
        get() = if (host == null) {
            null
        } else host!!.context

    abstract val layoutId: Int


    fun createView(inflater: LayoutInflater, parent: ViewGroup?): View? {
        mView = inflater.inflate(layoutId, parent, false)
        return mView
    }

    fun onViewCreated(view: View?) {
        findViews(view)
        registerForListeners()
        bindData(mData)
    }

    open fun bindData(data: T) {}

    protected abstract fun findViews(view: View?)

    private fun registerForListeners() {
        val positiveView = positiveView
        positiveView?.setOnClickListener { onPositive() }
        val negativeView = negativeView
        negativeView?.setOnClickListener { onNegative() }
        val cancelView = cancelView
        cancelView?.setOnClickListener { cancel() }
    }

    private fun onPositive() {
        var dismiss = true
        if (mDialogListener != null) {
            dismiss = mDialogListener!!.onPositive()
        }
        if (dismiss) {
            dismiss()
        }
    }

    private fun onNegative() {
        var dismiss = true
        if (mDialogListener != null) {
            dismiss = mDialogListener!!.onNegative()
        }
        if (dismiss) {
            dismiss()
        }
    }

    fun show(childFragmentManager: FragmentManager?) {
        host!!.show(childFragmentManager!!, null)
    }

    fun dismiss() {
        host!!.dismiss()
    }

    protected fun cancel() {
        dismiss()
        if (mDialogListener != null) {
            mDialogListener!!.onCancel()
        }
    }

    fun onCancel() {
        if (mDialogListener != null) {
            mDialogListener!!.onCancel()
        }
    }

    open val positiveView: View?
        get() = null

    open val negativeView: View?
        get() = null

    val cancelView: View?
        get() = null


}