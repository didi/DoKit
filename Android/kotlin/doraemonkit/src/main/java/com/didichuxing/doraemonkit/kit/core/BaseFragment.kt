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
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.kit.core.DokitViewManager.Companion.instance
import com.didichuxing.doraemonkit.kit.main.MainIconDokitView
import com.didichuxing.doraemonkit.util.DokitUtil
import com.didichuxing.doraemonkit.widget.dialog.CommonDialogProvider
import com.didichuxing.doraemonkit.widget.dialog.DialogInfo
import com.didichuxing.doraemonkit.widget.dialog.DialogProvider
import com.didichuxing.doraemonkit.widget.dialog.UniversalDialogFragment

/**
 * @author wanglikun
 * @date 2018/10/26
 */
abstract class BaseFragment : Fragment() {
    val TAG = this.javaClass.simpleName
    private var mRootView: View? = null
    private var mContainer = 0

    /**
     * @return 资源文件
     */
    @LayoutRes
    protected abstract fun onRequestLayout(): Int

    fun <T : View> findViewById(@IdRes id: Int): T {
        return mRootView!!.findViewById(id)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val id = onRequestLayout()
        if (id > 0) {
            mRootView = inflater.inflate(id, container, false)
        }
        if (mRootView == null) {
            mRootView = onCreateView(savedInstanceState)
        }
        if (interceptTouchEvents()) {
            if (mRootView != null) {
                mRootView!!.setOnTouchListener { view, motionEvent -> true }
            }
        }
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tryGetContainerId()
        try {
            if (view.context is Activity) {
                (view.context as Activity).window.decorView.requestLayout()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        instance.detach(MainIconDokitView::class.java)
    }

    private fun tryGetContainerId() {
        if (mRootView != null) {
            val parent = mRootView!!.parent as View
            mContainer = parent.id
        }
    }

    protected fun onCreateView(savedInstanceState: Bundle?): View? {
        return mRootView
    }

    private fun interceptTouchEvents(): Boolean {
        return false
    }

    val container: Int
        get() {
            if (mContainer == 0) {
                tryGetContainerId()
            }
            return mContainer
        }

    public open fun onBackPressed(): Boolean {
        return false
    }

    fun showToast(msg: CharSequence?) {
        ToastUtils.showShort(msg)
    }

    fun showToast(@StringRes resId: Int) {
        ToastUtils.showShort(DokitUtil.getString(resId))
    }

    fun showContent(fragmentClass: Class<out BaseFragment?>?, bundle: Bundle? = null) {
        val activity = activity as BaseActivity?
        activity?.showContent(fragmentClass!!, bundle)
    }

    fun finish() {
        val activity = activity as BaseActivity?
        activity?.doBack(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    open fun showDialog(dialogInfo: DialogInfo): DialogProvider<*>? {
        val provider = CommonDialogProvider(dialogInfo, dialogInfo.listener)
        showDialog(provider)
        return provider
    }

    open fun showDialog(provider: DialogProvider<*>) {
        val dialog = UniversalDialogFragment(provider)
        provider.host = dialog
        provider.show(childFragmentManager)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mRootView != null) {
            mRootView = null
        }
    }
}