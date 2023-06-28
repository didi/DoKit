package com.didichuxing.doraemonkit.kit.connect

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.extension.isTrueWithCor
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.util.TimeUtils
import com.didichuxing.doraemonkit.util.ToastUtils
import com.didichuxing.doraemonkit.widget.recyclerview.DividerItemDecoration
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar
import com.didichuxing.doraemonkit.zxing.activity.CaptureActivity
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * didi Create on 2022/4/12 .
 *
 * Copyright (c) 2022/4/12 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/12 6:07 下午
 * @Description 用一句话说明文件功能
 */
class DoKitConnectFragment : BaseFragment() {


    companion object {
        private const val REQUEST_CODE_SCAN = 0x1008
    }


    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: ConnectListAdapter
    private lateinit var histories: MutableList<ConnectAddress>


    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_dokit_connect
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findViewById<HomeTitleBar>(R.id.title_bar).setListener {
            finish()
        }

        findViewById<Button>(R.id.btn_add).setOnClickListener {
            startScan()
        }

        recyclerView = findViewById(R.id.recyclerView)

        mAdapter = ConnectListAdapter(mutableListOf<ConnectAddress>()) { it ->
            handleConnect(it)
        }

        mAdapter.setOnItemClickListener { adapter, view, pos ->
            val data = histories[pos]
            if (data.enable) {
                lifecycleScope.launch {
                    privacyInterceptDialog("提示", "当前已链接:${data.url} 是否断开链接").isTrueWithCor {
                        DoKitConnectManager.stopConnect()
                        ToastUtils.showShort("已断开")
                        updateHistoryView()
                    }
                }
            } else {
                lifecycleScope.launch {
                    privacyInterceptDialog("提示", "当前未链接:${data.url} ").isTrueWithCor {
                        updateHistoryView()
                    }
                }
            }

        }

        mAdapter.setOnItemLongClickListener { adapter, view, pos ->
            val data = histories[pos]
            lifecycleScope.launch {
                privacyInterceptDialog("提示", "是否删除连接历史记录").isTrueWithCor {
                    ConnectAddressStore.removeAddress(data)
                    ToastUtils.showShort("已删除记录")
                    updateHistoryView()
                }
            }
            return@setOnItemLongClickListener false
        }

        recyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireActivity())
            val decoration = DividerItemDecoration(DividerItemDecoration.VERTICAL)
            decoration.setDrawable(resources.getDrawable(R.drawable.dk_divider))
            addItemDecoration(decoration)
        }

        updateHistoryView()


    }

    private fun handleConnect(connectAddress: ConnectAddress) {
        DoKitConnectManager.startConnect(connectAddress)
        updateHistoryView()
    }


    /**
     * 处理dialog返回值
     */
    private suspend fun privacyInterceptDialog(title: String, content: String): Boolean =
        suspendCoroutine {
            AlertDialog.Builder(requireActivity())
                .setTitle(title)
                .setMessage(content)
                .setCancelable(false)
                .setPositiveButton("确认") { dialog, _ ->
                    dialog.dismiss()
                    it.resume(true)
                }
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                    it.resume(false)
                }
                .show()

        }


    override fun onResume() {
        super.onResume()
        updateHistoryView()
    }

    override fun onStart() {
        super.onStart()
    }

    private fun updateHistoryView() {
        lifecycleScope.launch {
            val clients = ConnectAddressStore.loadAddress()
            val current = DoKitConnectManager.getCurrentConnectAddress()
            for (history in clients) {
                if (current != null) {
                    history.enable = TextUtils.equals(history.url, current.url)
                } else {
                    history.enable = false
                }
            }

            histories = clients
            clients?.let {
                mAdapter.setList(clients)
            }
        }
    }



    /**
     * 开始扫描
     */
    private fun startScan() {
        val intent = Intent(activity, DoKitScanActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_SCAN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN)) {
                val url = data.getStringExtra(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN)
                if (!TextUtils.isEmpty(url)) {
                    try {
                        val uri = Uri.parse(url)
                        uri?.let {
                            val name = uri.host.toString()
                            val time = TimeUtils.date2String(Date())
                            val history = ConnectAddress(name, url!!, time)
                            ConnectAddressStore.saveAddress(history)
                            handleConnect(history)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    handleNoResult()
                }
            } else {
                handleNoResult()
            }
        } else {
            handleNoResult()
        }
    }

    /**
     * 没有返回结果
     */
    private fun handleNoResult() {
        ToastUtils.showShort("没有扫描到任何内容 >_< .")
    }


}
