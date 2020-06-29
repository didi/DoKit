package com.didichuxing.doraemonkit.kit.network.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.network.okhttp.bean.NetworkRecord
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar
import java.util.*

/**
 * @desc: 网络抓包详情页，显示request和response的详情
 * todo 文件上传 界面 出现卡死状态
 */
class NetworkDetailFragment : BaseFragment(), View.OnClickListener {
    private var mViewPager: ViewPager? = null
    private var mDiverRequest: View? = null
    private var mDiverResponse: View? = null
    private var mTvRequest: TextView? = null
    private var mTvResponse: TextView? = null
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_network_monitor_detail
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun initView() {
        mViewPager = findViewById(R.id.network_viewpager)
        mDiverRequest = findViewById(R.id.diver_request)
        mDiverResponse = findViewById(R.id.diver_response)
        mTvRequest = findViewById(R.id.tv_pager_request)
        mTvRequest?.apply {
            isSelected = true
            setOnClickListener(this@NetworkDetailFragment)
        }
        mTvResponse = findViewById(R.id.tv_pager_response)
        mTvResponse?.apply {
            isSelected = false
            setOnClickListener(this@NetworkDetailFragment)
        }
        val views: MutableList<NetworkDetailView> = ArrayList()
        val bundle = arguments
        val record = bundle!!.getSerializable(NetworkListView.Companion.KEY_RECORD) as NetworkRecord
        context?.apply {
            views.add(NetworkDetailView(this))
            views.add(NetworkDetailView(this))
        }

        val adapter = NetworkPagerAdapter(views, record)
        mViewPager!!.adapter = adapter
        mViewPager!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                mDiverRequest!!.visibility = if (position == 0) View.VISIBLE else View.GONE
                mDiverResponse!!.visibility = if (position == 1) View.VISIBLE else View.GONE
                mTvRequest!!.isSelected = position == 0
                mTvResponse!!.isSelected = position == 1
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        val mTitleBar = findViewById<TitleBar>(R.id.title_bar)
        mTitleBar.onTitleBarClickListener = object : TitleBar.OnTitleBarClickListener {
            override fun onLeftClick() {
                activity!!.onBackPressed()
            }

            override fun onRightClick() {}
        }
    }

    override fun onBackPressed(): Boolean {
        return super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onClick(v: View) {
        if (v.id == R.id.tv_pager_request) {
            mViewPager!!.setCurrentItem(0, true)
        } else if (v.id == R.id.tv_pager_response) {
            mViewPager!!.setCurrentItem(1, true)
        }
    }

    companion object {
        private const val TAG = "NetworkDetailFragment"
    }
}