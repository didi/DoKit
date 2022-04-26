package com.didichuxing.doraemonkit.kit.core

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.main.MainIconDoKitView
import com.didichuxing.doraemonkit.util.ToastUtils
import com.didichuxing.doraemonkit.widget.dialog.CommonDialogProvider
import com.didichuxing.doraemonkit.widget.dialog.DialogInfo
import com.didichuxing.doraemonkit.widget.dialog.DialogProvider
import com.didichuxing.doraemonkit.widget.dialog.UniversalDialogFragment


/**
 * @author wanglikun
 * @date 2018/10/26
 */
abstract class BaseFragment : Fragment() {
    @JvmField
    val TAG = this.javaClass.simpleName

    /**
     * @return 资源文件
     */
    @LayoutRes
    protected abstract fun onRequestLayout(): Int


    fun <T : View> findViewById(@IdRes id: Int): T {
        return requireView().findViewById(id)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val id = onRequestLayout()
        var rootView: View? = null
        if (id > 0) {
            rootView = inflater.inflate(id, container, false)
        }
        if (interceptTouchEvents() && rootView != null) {
            rootView.setOnTouchListener { _, _ -> true }
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            if (view.context is Activity) {
                (view.context as Activity).window.decorView.requestLayout()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        DoKit.removeFloating(MainIconDoKitView::class)
    }


    protected fun interceptTouchEvents(): Boolean {
        return false
    }


    open fun onBackPressed(): Boolean {
        return false
    }


    @JvmOverloads
    fun showContent(fragmentClass: Class<out BaseFragment>, bundle: Bundle? = null) {
        val activity = activity as BaseActivity?
        activity?.showContent(fragmentClass, bundle)
    }

    fun finish() {
        if (activity is BaseActivity) {
            val activity = activity as BaseActivity?
            activity?.doBack(this)
        }

        if (activity is NewBaseActivity) {
            val activity = activity as NewBaseActivity?
            activity?.doBack(this)
        }
    }


    fun showDialog(dialogInfo: DialogInfo): DialogProvider<*> {
        val provider = CommonDialogProvider(dialogInfo, dialogInfo.listener)
        showDialog(provider)
        return provider
    }

    open fun showDialog(provider: DialogProvider<*>) {
        val dialog = UniversalDialogFragment()
        provider.host = dialog
        dialog.setProvider(provider)
        provider.show(childFragmentManager)
    }

    fun dismissDialog(provider: DialogProvider<*>) {
        provider.dismiss()
    }

    fun showToast(msg: String) {
        ToastUtils.showShort(msg)
    }

    fun showToast(@StringRes res: Int) {
        ToastUtils.showShort(res)
    }

}
