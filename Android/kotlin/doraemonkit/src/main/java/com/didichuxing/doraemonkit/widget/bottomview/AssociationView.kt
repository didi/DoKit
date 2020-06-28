package com.didichuxing.doraemonkit.widget.bottomview

import android.view.View

/**
 * 可提交的view
 *
 * @author vinda
 * @since 15/5/21
 */
abstract class AssociationView {
    var onStateChangeListener: OnStateChangeListener? = null

    /**
     * 提交
     */
    abstract fun submit(): Any

    /**
     * 取消
     */
    abstract fun cancel()

    /**
     * 获取视图
     *
     * @return
     */
    abstract val view: View?

    /**
     * 能否提交
     *
     * @return
     */
    abstract val isCanSubmit: Boolean
    abstract fun onShow()
    abstract fun onHide()

    interface OnStateChangeListener {
        fun onStateChanged()
    }
}