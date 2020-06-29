package com.didichuxing.doraemonkit.kit.network.ui

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.didichuxing.doraemonkit.kit.network.okhttp.bean.NetworkRecord

class NetworkPagerAdapter(private val views: List<NetworkDetailView>, private val mRecord: NetworkRecord) : PagerAdapter() {
    override fun getCount(): Int {
        return views.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = views[position]
        if (position == 0) {
            view.bindRequest(mRecord)
        } else {
            view.bindResponse(mRecord)
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(views[position])
    }

}