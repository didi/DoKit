package com.didichuxing.doraemonkit.kit.autotest.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.autotest.R
import com.didichuxing.doraemonkit.kit.autotest.AutoTestManager
import com.didichuxing.doraemonkit.kit.connect.ConnectAddress
import com.didichuxing.doraemonkit.kit.connect.ConnectAddressStore
import com.didichuxing.doraemonkit.kit.connect.DoKitConnectFragment
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.util.ToastUtils


/**
 * didi Create on 2022/4/14 .
 *
 * Copyright (c) 2022/4/14 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/14 4:40 下午
 * @Description 用一句话说明文件功能
 */

class DoKitAutotestConnectFragment : BaseFragment() {


    private var urlTextView: TextView? = null
    private var address: ConnectAddress? = null

    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_autotest_connect
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        urlTextView = view.findViewById<TextView>(R.id.url)

        view.findViewById<View>(R.id.connect).setOnClickListener {
            starConnect()
        }
        view.findViewById<View>(R.id.change).setOnClickListener {
            startChange()
        }

    }

    override fun onResume() {
        super.onResume()
        updateUrl()
    }

    private fun updateUrl() {
        val list = ConnectAddressStore.loadAddress()
        if (list.size > 0) {
            address = list[list.size - 1]
        }
        address?.let {
            urlTextView?.text = "可使用地址:${it.url}"
        } ?: run {
            urlTextView?.text = "可使用地址:--}"
        }
    }

    private fun starConnect() {
        if (address == null) {
            ToastUtils.showShort("无可用地址，请添加")
        } else {
            address?.let {
                AutoTestManager.startConnect(it)
                finish()
            }
        }
    }

    private fun startChange() {
        //使用统一的链接管理
        DoKit.launchFullScreen(DoKitConnectFragment::class.java, context, null, false)
    }


}
