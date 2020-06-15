package com.didichuxing.doraemonkit.kit.fileexplorer.widget

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.Switch
import android.widget.TextView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.SpInputType
import com.didichuxing.doraemonkit.kit.fileexplorer.SpBean
import com.didichuxing.doraemonkit.widget.bottomview.BottomUpWindow
import com.didichuxing.doraemonkit.widget.bottomview.EditSpInputView

class SpInputView : FrameLayout {
    private var onDataChangeListener: OnDataChangeListener? = null
    private var spValue: TextView? = null
    private var switchBtn: Switch? = null
    private var bean: SpBean? = null

    constructor(context: Context?) : super(context!!, null) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        val inflate: View = LayoutInflater.from(context).inflate(R.layout.dk_view_sp_input, this, true)
        switchBtn = inflate.findViewById(R.id.switch_btn)
        spValue = inflate.findViewById(R.id.tv_sp_value)
    }

    fun setInput(bean: SpBean, onDataChangeListener: OnDataChangeListener) {
        this.bean = bean
        this.onDataChangeListener = onDataChangeListener
        when (bean.value.javaClass.simpleName) {
            SpInputType.BOOLEAN -> {
                switchBtn!!.isChecked = (bean.value as Boolean)
                switchBtn!!.visibility = View.VISIBLE
                switchBtn!!.setOnCheckedChangeListener { _, isChecked ->
                    bean.value = isChecked
                    onDataChangeListener.onDataChanged()
                }
                spValue!!.visibility = View.GONE
            }
            SpInputType.INTEGER, SpInputType.LONG -> initEdt(bean, INTEGER)
            SpInputType.FLOAT -> initEdt(bean, FLOAT)
            SpInputType.STRING -> initEdt(bean, STRING)
            else -> {
            }
        }
    }

    private fun initEdt(spBean: SpBean, inputType: Int) {
        spValue!!.visibility = View.VISIBLE
        switchBtn!!.visibility = View.GONE
        spValue!!.text = spBean.value.toString()
        spValue!!.setOnClickListener { v -> showInputView(v, spBean, inputType) }
    }

    fun refresh() {
        if (bean != null) {
            spValue!!.text = bean!!.value.toString()
        }
    }

    private fun showInputView(view: View, spBean: SpBean, inputType: Int) {
        BottomUpWindow(context).setContent(EditSpInputView(context, spBean, inputType))
                .show(view).setOnSubmitListener(object : BottomUpWindow.OnSubmitListener {
                    override fun submit(obj: Any) {
                        spBean.value = obj.toString()
                        if (onDataChangeListener != null) {
                            onDataChangeListener!!.onDataChanged()
                        }
                    }

                    override fun cancel() {}
                })
    }

    interface OnDataChangeListener {
        fun onDataChanged()
    }

    companion object {
        private const val FLOAT = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        private const val INTEGER = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        private const val STRING = InputType.TYPE_CLASS_TEXT
    }
}