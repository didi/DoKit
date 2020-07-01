package com.didichuxing.doraemonkit.kit.network.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar
import java.util.*

class NetWorkMainPagerFragment : BaseFragment(), View.OnClickListener {
    private var mViewPager: ViewPager? = null
    private var mSummaryView: NetWorkSummaryView? = null
    private var mNetworkListView: NetworkListView? = null
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_net_main_pager
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val mTitleBar = findViewById<TitleBar>(R.id.title_bar)
        mTitleBar.onTitleBarClickListener = object : TitleBar.OnTitleBarClickListener {
            override fun onLeftClick() {
                activity!!.onBackPressed()
            }

            override fun onRightClick() {}
        }
        mViewPager = findViewById(R.id.vp_show)
        mSummaryView = NetWorkSummaryView(context)
        mNetworkListView = NetworkListView(context)
        mNetworkListView!!.registerNetworkListener()
        val views: MutableList<View> = ArrayList()
        views.add(mSummaryView!!)
        views.add(mNetworkListView!!)
        mViewPager!!.adapter = NetWorkMainPagerAdapter(context, views)
        val tabSummary = findViewById<View>(R.id.tab_summary)
        (tabSummary.findViewById<View>(R.id.tab_text) as TextView).setText(R.string.dk_net_monitor_title_summary)
        (tabSummary.findViewById<View>(R.id.tab_icon) as ImageView).setImageResource(R.drawable.dk_net_work_monitor_summary_selector)
        tabSummary.isSelected = true
        tabSummary.setOnClickListener(this)
        val tabList = findViewById<View>(R.id.tab_list)
        (tabList.findViewById<View>(R.id.tab_text) as TextView).setText(R.string.dk_net_monitor_list)
        (tabList.findViewById<View>(R.id.tab_icon) as ImageView).setImageResource(R.drawable.dk_net_work_monitor_list_selector)
        tabList.setOnClickListener(this)
        mViewPager!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (position == 0) {
                    tabSummary.isSelected = true
                    tabList.isSelected = false
                } else {
                    tabList.isSelected = true
                    tabSummary.isSelected = false
                }
            }

            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.tab_summary) {
            mViewPager!!.setCurrentItem(0, true)
        } else if (id == R.id.tab_list) {
            mViewPager!!.setCurrentItem(1, true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mNetworkListView != null) {
            mNetworkListView!!.unRegisterNetworkListener()
        }
    }
}