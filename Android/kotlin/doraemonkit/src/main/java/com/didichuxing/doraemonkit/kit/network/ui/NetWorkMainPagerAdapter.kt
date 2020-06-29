package com.didichuxing.doraemonkit.kit.network.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.didichuxing.doraemonkit.R
import java.util.*

class NetWorkMainPagerAdapter(context: Context?, private val views: List<View>) : PagerAdapter() {
    private val tabs: MutableList<String> = ArrayList()
    override fun getPageTitle(position: Int): CharSequence? {
        return tabs[position]
    }

    override fun getCount(): Int {
        return views.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = views[position]
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(views[position])
    }

    init {
        tabs.add(context!!.getString(R.string.dk_net_monitor_title_summary))
        tabs.add(context.getString(R.string.dk_net_monitor_list))
    }
}