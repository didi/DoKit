package com.didichuxing.doraemonkit.kit.toolpanel

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.core.UniversalActivity
import com.didichuxing.doraemonkit.util.DokitUtil
import kotlinx.android.synthetic.main.dk_fragment_setting.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/29-15:00
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DokitSettingFragment : BaseFragment() {
    private lateinit var mAdapter: DokitSettingAdapter
    private val mSettings = mutableListOf(DokitUtil.getString(R.string.dk_setting_kit_manager))

    @LayoutRes
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_setting
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    fun initView() {
        title_bar.setListener {
            finish()
        }

        mAdapter = DokitSettingAdapter(mSettings)
        setting_list.adapter = mAdapter
        setting_list.layoutManager = LinearLayoutManager(activity)
        mAdapter.setOnItemClickListener { adapter, view, position ->

            when (position) {
                0 -> {
                    if (activity != null) {
                        val intent = Intent(activity, UniversalActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_DOKIT_MANAGER)
                        activity!!.startActivity(intent)
                    }
                }
            }
        }
    }

}