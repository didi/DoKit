package com.didichuxing.doraemonkit.kit.core

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment


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
        return Activity().findViewById(id)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    protected fun interceptTouchEvents(): Boolean {
        return false
    }


    open fun onBackPressed(): Boolean {
        return false
    }


    @JvmOverloads
    fun showContent(fragmentClass: Class<out BaseFragment>, bundle: Bundle? = null) {
    }

    fun finish() {
    }








}