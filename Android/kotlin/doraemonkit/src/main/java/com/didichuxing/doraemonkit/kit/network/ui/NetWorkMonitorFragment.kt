package com.didichuxing.doraemonkit.kit.network.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.PathUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.config.DokitMemoryConfig
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.kit.core.SettingItem
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter.OnSettingItemClickListener
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter.OnSettingItemSwitchListener
import com.didichuxing.doraemonkit.kit.network.NetworkManager
import com.didichuxing.doraemonkit.kit.network.okhttp.bean.WhiteHostBean
import com.didichuxing.doraemonkit.kit.performance.AbsPerformanceFragment
import com.didichuxing.doraemonkit.kit.performance.manager.datasource.DataSourceFactory
import java.io.File
import java.util.*

/**
 * @author jint
 */
class NetWorkMonitorFragment : AbsPerformanceFragment() {
    var mHostRv: RecyclerView? = null
    var mHostAdapter: WhiteHostAdapter? = null
    var mHostBeans: MutableList<WhiteHostBean> = ArrayList()
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_net_monitor
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCustomView()
    }

    override val title: Int
        protected get() = R.string.dk_kit_net_monitor

    override val performanceType: Int
        protected get() = DataSourceFactory.TYPE_NETWORK

    override fun getSettingItems(list: MutableList<SettingItem>): MutableList<SettingItem>? {
        list?.add(SettingItem(R.string.dk_net_monitor_detection_switch, NetworkManager.isActive))
        return list!!
    }



    override val itemSwitchListener: OnSettingItemSwitchListener
        protected get() = object : OnSettingItemSwitchListener {
            override fun onSettingItemSwitch(view: View, data: SettingItem, on: Boolean) {
                if (on) {
                    startMonitor()
                } else {
                    stopMonitor()
                }
                DokitMemoryConfig.NETWORK_STATUS = on
            }
        }

    override val itemClickListener: OnSettingItemClickListener
        protected get() = object : OnSettingItemClickListener {


            override fun onSettingItemClick(view: View, data: SettingItem) {

            }
        }

    private fun initCustomView() {
        findViewById<View>(R.id.btn_net_summary).setOnClickListener { showContent(NetWorkMainPagerFragment::class.java, null) }
        mHostRv = findViewById(R.id.host_list)
        mHostRv!!.layoutManager = LinearLayoutManager(activity)
        if (DokitConstant.WHITE_HOSTS.isEmpty()) {
            val whiteHostArray = FileIOUtils.readFile2String(whiteHostPath)
            if (TextUtils.isEmpty(whiteHostArray)) {
                mHostBeans.add(WhiteHostBean("", true))
            } else {
                mHostBeans = GsonUtils.fromJson(whiteHostArray, GsonUtils.getListType(WhiteHostBean::class.java))
                DokitConstant.WHITE_HOSTS.clear()
                DokitConstant.WHITE_HOSTS.addAll(mHostBeans)
            }
        } else {
            mHostBeans.addAll(DokitConstant.WHITE_HOSTS)
        }
        mHostAdapter = WhiteHostAdapter(R.layout.dk_item_white_host, mHostBeans)
        mHostRv!!.adapter = mHostAdapter
    }

    private fun startMonitor() {
        NetworkManager.instance.startMonitor()
        openChartPage(R.string.dk_kit_net_monitor, DataSourceFactory.TYPE_NETWORK)
    }

    private fun stopMonitor() {
        NetworkManager.instance.stopMonitor()
        closeChartPage()
    }

    private val whiteHostPath = PathUtils.getInternalAppFilesPath() + File.separator + "white_host.json"
    override fun onDestroy() {
        super.onDestroy()
        //保存白名单
        val hostBeans: List<WhiteHostBean> = mHostAdapter!!.data
        if (hostBeans.size == 1 && TextUtils.isEmpty(hostBeans[0].host)) {
            DokitConstant.WHITE_HOSTS.clear()
            FileUtils.delete(whiteHostPath)
            return
        }
        DokitConstant.WHITE_HOSTS.clear()
        DokitConstant.WHITE_HOSTS.addAll(hostBeans)
        val hostArray = GsonUtils.toJson(hostBeans)
        //保存到本地
        FileUtils.delete(whiteHostPath)
        FileIOUtils.writeFileFromString(whiteHostPath, hostArray)
        //ToastUtils.showShort("host白名单已保存");
    }
}