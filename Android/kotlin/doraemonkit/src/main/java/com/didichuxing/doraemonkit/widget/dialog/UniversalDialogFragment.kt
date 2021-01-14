package com.didichuxing.doraemonkit.widget.dialog

import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.didichuxing.doraemonkit.R

/**
 * Created by wanglikun on 2019/4/12
 */
class UniversalDialogFragment(private val provider: DialogProvider<*>) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun getTheme(): Int {
        return R.style.DK_Dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        isCancelable = provider.isCancellable
        val window = dialog.window
        val lp = window!!.attributes
        lp.gravity = gravity
        lp.width = width
        lp.height = height
        window.attributes = lp
        return provider!!.createView(inflater, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        provider!!.onViewCreated(view)
    }

    override fun onStart() {
        super.onStart()
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(resources.getColor(android.R.color.transparent)))
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        provider!!.onCancel()
    }

    val gravity: Int
        get() = Gravity.CENTER

    val width: Int
        get() = WindowManager.LayoutParams.WRAP_CONTENT

    val height: Int
        get() = WindowManager.LayoutParams.WRAP_CONTENT
}