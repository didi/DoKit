package com.didichuxing.doraemonkit.kit.toolpanel

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.network.NetworkManager
import com.didichuxing.doraemonkit.kit.toolpanel.bean.MorePageGroupBean
import com.didichuxing.doraemonkit.kit.webview.CommWebViewFragment
import com.didichuxing.doraemonkit.kit.webview.WebViewManager
import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.volley.VolleyManager
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/29-15:00
 * 描    述：dokit 更多页面
 * 修订历史：
 * ================================================
 */
class DokitMoreFragment : BaseFragment() {
    private lateinit var mAdapter: DokitMoreAdapter

    @LayoutRes
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_more
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }

    /**
     * 获取列表数据
     */
    fun getData() {

        val request = StringRequest(
            Request.Method.GET,
            NetworkManager.DOKIT_MORE_PAGE_URL,
            {
                val morePageGroupBean = GsonUtils.fromJson(
                    it,
                    MorePageGroupBean::class.java
                )
                initView(convertGroup2normalItem(morePageGroupBean.data.group))
            }, {
                initView(convertGroup2normalItem(createDefaultGroups()))
            })
        VolleyManager.add(request)

    }

    /**
     * 构造默认数据
     */
    private fun createDefaultGroups(): MutableList<MorePageGroupBean.DataBean.GroupBean> {
        val groups: MutableList<MorePageGroupBean.DataBean.GroupBean> = mutableListOf()
        val groupBean = MorePageGroupBean.DataBean.GroupBean()
        groupBean.group = "本地功能"
        val list: MutableList<MorePageGroupBean.DataBean.GroupBean.ListBean> =
            mutableListOf()
        val listBean = MorePageGroupBean.DataBean.GroupBean.ListBean()
        listBean.name = "功能管理"
        listBean.desc = "介绍:可以针对dokit的内置工具列表进行自定义排序"
        listBean.link = "dokit://native/function_manager"
        listBean.type = "native"
        list.add(listBean)
        groupBean.list = list
        groups.add(groupBean)
        return groups
    }


    private fun convertGroup2normalItem(groups: MutableList<MorePageGroupBean.DataBean.GroupBean>): MutableList<MorePageGroupBean.DataBean.GroupBean.ListBean> {
        val items: MutableList<MorePageGroupBean.DataBean.GroupBean.ListBean> = mutableListOf()
        groups.forEach { group ->
            val item = MorePageGroupBean.DataBean.GroupBean.ListBean()
            item.name = group.group
            item.setHeader(true)
            items.add(item)
            group.list.forEach { innerItem ->
                items.add(innerItem)
            }
        }
        return items
    }

    private var allItems: MutableList<MorePageGroupBean.DataBean.GroupBean.ListBean>? = null

    fun initView(items: MutableList<MorePageGroupBean.DataBean.GroupBean.ListBean>) {
        allItems = items
        findViewById<HomeTitleBar>(R.id.title_bar).setListener {
            finish()
        }

        mAdapter =
            DokitMoreAdapter(R.layout.dk_item_more_header, R.layout.dk_item_more_content, items)
        findViewById<RecyclerView>(R.id.setting_list).apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        mAdapter.setOnItemChildClickListener { _, _, position ->

            val item = allItems?.get(position)
            item?.let { item ->
                when (item.type) {
                    "native" -> {
                        if (item.link == "dokit://native/function_manager") {
                            activity?.let {
                                DoKit.launchFullScreen(
                                    DokitManagerFragment::class.java,
                                    it
                                )
                            }
                        } else {
                        }
                    }
                    "web" -> {
                        activity?.let {
                            WebViewManager.url = item.link
                            DoKit.launchFullScreen(
                                CommWebViewFragment::class.java,
                                it
                            )
                        }
                    }
                    else -> {

                    }
                }
            }
        }

    }

}