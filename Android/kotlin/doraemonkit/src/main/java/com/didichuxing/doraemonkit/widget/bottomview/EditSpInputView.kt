package com.didichuxing.doraemonkit.widget.bottomview

import android.content.Context
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.didichuxing.doraemonkit.kit.fileexplorer.SpBean

class EditSpInputView(context: Context?, private val spBean: SpBean, inputType: Int) : AssociationView() {
    private val editText: EditText = EditText(context)
    override fun submit(): Any {
        return spBean.toDefaultClass(editText.text.toString())
    }

    override fun cancel() {}
    override val view: View
        get() = editText

    override val isCanSubmit: Boolean
        get() = true

    override fun onShow() {}
    override fun onHide() {}

    init {
        editText.setText(spBean.value.toString())
        editText.inputType = inputType or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        editText.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        editText.setSelection(spBean.value.toString().length)
    }
}