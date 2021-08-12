package com.didichuxing.doraemondemo.comm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：3/11/21-16:14
 * 描    述：
 * 修订历史：
 * ================================================
 */
abstract class CommBaseFragment : Fragment() {
    private val viewModel: CommViewModel by activityViewModels()

    private lateinit var mRootView: ViewGroup
    abstract fun initActivityTitle(): String

    @LayoutRes
    abstract fun getLayoutId(): Int


    private fun bindViewModel() {
        viewModel.title = initActivityTitle()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = inflater.inflate(getLayoutId(), container, false) as ViewGroup
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
    }

    abstract fun initView(savedInstanceState: Bundle?)

    fun <T : View> findViewById(@IdRes id: Int): T {

        return mRootView.findViewById(id)
    }

}