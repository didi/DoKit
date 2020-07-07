package com.didichuxing.doraemonkit.kit.health

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar
import com.didichuxing.doraemonkit.widget.viewpager.VerticalViewPager
import java.util.*

/**
 * 健康体检fragment
 * @author pengyushan
 */
class HealthFragment : BaseFragment() {
    var mVerticalViewPager: VerticalViewPager? = null
    private var mHomeTitleBar: HomeTitleBar? = null
    var mFragments: MutableList<Fragment> = ArrayList()
    private var mFragmentPagerAdapter: FragmentPagerAdapter? = null
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_health
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity == null) {
            return
        }
        initView()
    }

    private fun initView() {
        mFragments.clear()
        mFragments.add(HealthFragmentFirst())
        mFragments.add(HealthFragmentSecond())
        mHomeTitleBar = findViewById(R.id.title_bar)
        mHomeTitleBar!!.setListener(object : HomeTitleBar.OnTitleBarClickListener {
            override fun onRightClick() {
                finish()
            }
        })
        mVerticalViewPager = findViewById(R.id.view_pager)
        mFragmentPagerAdapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return mFragments[position]
            }

            override fun getCount(): Int {
                return mFragments.size
            }
        }
        mVerticalViewPager!!.adapter = mFragmentPagerAdapter
    }

    /**
     * 滑动到顶部
     */
    fun scroll2theTop() {
        if (mVerticalViewPager != null && mFragmentPagerAdapter != null) {
            mVerticalViewPager!!.setCurrentItem(0, true)
        }
    }
}