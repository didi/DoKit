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
import com.didichuxing.doraemonkit.zxing.activity.CaptureActivity
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/10-10:52
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DoKitConnectFragment : BaseFragment() {


    companion object {
        private const val REQUEST_CODE_SCAN = 0x10898
    }


    private lateinit var mRv: RecyclerView
    private lateinit var mAdapter: ConnectListAdapter
    private lateinit var histories: MutableList<ConnectAddress>


    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_dokit_connect
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val add: Button = findViewById(R.id.btn_add)
        add.setOnClickListener { view ->
            startScan()
        }

        mRv = findViewById(R.id.rv)

        mAdapter = ConnectListAdapter(mutableListOf<ConnectAddress>()) { it ->
            handleConnect(it)
        }

        mAdapter.setOnItemClickListener { adapter, view, pos ->

            val data = histories[pos]

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

        mRv.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireActivity())
            val decoration = DividerItemDecoration(DividerItemDecoration.VERTICAL)
            decoration.setDrawable(resources.getDrawable(R.drawable.dk_divider))
            addItemDecoration(decoration)
        }

        updateHistoryView()


    }

    private fun handleConnect(connectAddress: ConnectAddress) {
        DoKitSConnectManager.startConnect(connectAddress)
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
            val current = DoKitSConnectManager.getCurrentConnectAddress()
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
                            val url = "ws://${uri.host}:${uri.port}${uri.path}"
                            val history = ConnectAddress(name, url, time)
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
