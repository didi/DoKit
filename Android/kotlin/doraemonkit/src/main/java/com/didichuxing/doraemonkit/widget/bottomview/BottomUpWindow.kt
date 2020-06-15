package com.didichuxing.doraemonkit.widget.bottomview

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.PopupWindow
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.widget.bottomview.AssociationView.OnStateChangeListener

/**
 * 从底部向上弹出的选择器
 *
 * @author vinda
 * @since 15/5/21
 */
class BottomUpWindow(context: Context?) : PopupWindow(context) {

    companion object {
        private val TAG = "BottomUpSelectWindow"
    }

    private val thisView: View
    private var tv_submit: View? = null
    private val titleViiew: View
    private val contentPanel: FrameLayout
    private lateinit var associationView: AssociationView
    private val ll_panel: View
    private val onClickListener = View.OnClickListener { v ->
        val vid = v.id
        if (vid == R.id.tv_submit) {
            val submit = associationView.submit()
            mOnSubmitListener?.submit(submit)
            dismiss()
        } else if (vid == R.id.tv_cancel) {
            cancel()
        }
    }

    private fun initView() {
        tv_submit = thisView.findViewById(R.id.tv_submit)
        tv_submit?.setOnClickListener(onClickListener)
        thisView.findViewById<View>(R.id.tv_cancel).setOnClickListener(onClickListener)
        //点击在上方时关闭
        thisView.setOnClickListener { cancel() }
    }

    /**
     * 设置中间内容的view
     *
     * @param view
     */
    fun setContent(view: AssociationView): BottomUpWindow {
        associationView = view
        contentPanel.removeAllViews()
        contentPanel.addView(associationView.view)
        associationView.onStateChangeListener = object : OnStateChangeListener {
            override fun onStateChanged() {
                tv_submit!!.isEnabled = associationView.isCanSubmit
            }
        }
        return this
    }

    override fun dismiss() {
        //动画
        val animation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f)
        animation.duration = 200
        animation.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                ll_panel.visibility = View.GONE
                dismissWindow()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        ll_panel.startAnimation(animation)
        associationView.onHide()
    }

    /**
     * 隐藏整个窗口
     */
    private fun dismissWindow() {
        try {
            super.dismiss()
        } catch (e: Throwable) {
        }
    }

    private fun cancel() {
        associationView.cancel()
        dismiss()
        if (mOnSubmitListener != null) {
            mOnSubmitListener!!.cancel()
        }
    }

    fun show(parent: View?): BottomUpWindow {
        showAtLocation(parent, Gravity.BOTTOM
                or Gravity.CENTER_HORIZONTAL, 0, 0)
        ll_panel.visibility = View.VISIBLE
        //动画
        val animation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f)
        animation.duration = 200
        ll_panel.startAnimation(animation)
        associationView.onShow()
        return this
    }

    private var mOnSubmitListener: OnSubmitListener? = null
    fun setOnSubmitListener(onSubmitListener: OnSubmitListener?) {
        mOnSubmitListener = onSubmitListener
    }

    interface OnSubmitListener {
        fun submit(obj: Any)
        fun cancel()
    }

    init {
        val layoutInflater = LayoutInflater.from(context)
        thisView = layoutInflater.inflate(R.layout.dk_item_layout_bottom_up_select_window, null)
        ll_panel = thisView.findViewById(R.id.ll_panel)
        titleViiew = thisView.findViewById(R.id.tv_title)
        contentPanel = thisView.findViewById(R.id.content)
        this.contentView = thisView
        initView()
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        this.isFocusable = true
        this.isTouchable = true
        this.isOutsideTouchable = true
        val dw = ColorDrawable(-0x80000000)
        setBackgroundDrawable(dw)
    }
}