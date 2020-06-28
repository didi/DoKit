package com.didichuxing.doraemonkit.kit.network.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.BaseActivity
import com.didichuxing.doraemonkit.kit.network.NetworkManager
import com.didichuxing.doraemonkit.kit.network.OnNetworkInfoUpdateListener
import com.didichuxing.doraemonkit.kit.network.okhttp.bean.NetworkRecord
import com.didichuxing.doraemonkit.widget.recyclerview.DividerItemDecoration
import java.util.*

/**
 * @desc: 抓包列表
 */
class NetworkListView : LinearLayout, OnNetworkInfoUpdateListener {
    private var mNetworkList: RecyclerView? = null
    private var mNetworkListAdapter: NetworkListAdapter? = null

    constructor(context: Context?) : super(context) {
        View.inflate(context, R.layout.dk_fragment_network_monitor_list, this)
        initView()
        initData()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        View.inflate(context, R.layout.dk_fragment_network_monitor_list, this)
        initView()
        initData()
    }

    private fun initView() {
        mNetworkList = findViewById(R.id.network_list)
        val layoutManager = LinearLayoutManager(context)
        mNetworkList?.setLayoutManager(layoutManager)
        mNetworkListAdapter = NetworkListAdapter(context)
        mNetworkList?.setAdapter(mNetworkListAdapter)
        val decoration = DividerItemDecoration(DividerItemDecoration.VERTICAL)
        decoration.setDrawable(resources.getDrawable(R.drawable.dk_divider))
        decoration.showHeaderDivider(true)
        mNetworkList?.addItemDecoration(decoration)
        mNetworkListAdapter?.setOnItemClickListener(object : NetworkListAdapter.OnItemClickListener {
            override fun onClick(record: NetworkRecord) {
                val bundle = Bundle()
                bundle.putSerializable(KEY_RECORD, record)
                (context as BaseActivity).showContent(NetworkDetailFragment::class.java, bundle)
            }

        })
        (findViewById<View>(R.id.network_list_filter) as EditText).addTextChangedListener(mTextWatcher)
    }

    private fun initData() {
        synchronized(this) {
            val records: List<NetworkRecord> = ArrayList(NetworkManager.get().records)
            Collections.reverse(records)
            mNetworkListAdapter!!.replaceData(records)
        }
    }

    fun registerNetworkListener() {
        NetworkManager.get().setOnNetworkInfoUpdateListener(this)
    }

    fun unRegisterNetworkListener() {
        NetworkManager.get().setOnNetworkInfoUpdateListener(null)
    }

    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            mNetworkListAdapter!!.filter.filter(s)
        }
    }

    override fun onNetworkInfoUpdate(record: NetworkRecord?, add: Boolean) {
        synchronized(this) {
            if (add) {
                mNetworkListAdapter!!.append(record, 0)
            }
            mNetworkListAdapter!!.notifyDataSetChanged()
        }
    }

    companion object {
        private const val TAG = "NetworkListFragment"
        const val KEY_RECORD = "record"
    }


}