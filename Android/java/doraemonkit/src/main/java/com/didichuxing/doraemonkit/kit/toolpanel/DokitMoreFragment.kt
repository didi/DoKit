package com.didichuxing.doraemonkit.kit.toolpanel

import android.content.Intent
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.blankj.utilcode.util.GsonUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.core.UniversalActivity
import com.didichuxing.doraemonkit.kit.network.NetworkManager
import com.didichuxing.doraemonkit.kit.toolpanel.bean.MorePageGroupBean
import com.didichuxing.doraemonkit.kit.webview.WebViewManager
import com.didichuxing.doraemonkit.okgo.DokitOkGo
import kotlinx.android.synthetic.main.dk_fragment_more.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        GlobalScope.launch(Dispatchers.Main) {
            val groups = withContext(Dispatchers.IO) {
                val response = DokitOkGo.get<String>(NetworkManager.DOKIT_MORE_PAGE_URL).execute()
                if (response.isSuccessful) {
                    val morePageGroupBean = GsonUtils.fromJson(
                        response.body()?.string(),
                        MorePageGroupBean::class.java
                    )
                    return@withContext morePageGroupBean.data.group
                } else {
                    return@withContext createDefaultGroups()
                }
            }


            initView(convertGroup2normalItem(groups))


        }
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
        title_bar.setListener {
            finish()
        }

        mAdapter =
            DokitMoreAdapter(R.layout.dk_item_more_header, R.layout.dk_item_more_content, items)
        setting_list.adapter = mAdapter
        setting_list.layoutManager = LinearLayoutManager(activity)
        mAdapter.setOnItemChildClickListener { _, _, position ->

            val item = allItems?.get(position)
            item?.let { item ->
                when (item.type) {
                    "native" -> {
                        if (item.link == "dokit://native/function_manager") {
                            activity?.let {
                                val intent = Intent(activity, UniversalActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent.putExtra(
                                    BundleKey.FRAGMENT_INDEX,
                                    FragmentIndex.FRAGMENT_DOKIT_MANAGER
                                )
                                it.startActivity(intent)
                            }
                        } else {

                        }
                    }
                    "web" -> {
                        activity?.let {
                            WebViewManager.url = item.link
                            val intent = Intent(activity, UniversalActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            intent.putExtra(
                                BundleKey.FRAGMENT_INDEX,
                                FragmentIndex.FRAGMENT_WEB
                            )
                            it.startActivity(intent)
                        }
                    }
                    else -> {

                    }
                }
            }
        }

    }

}