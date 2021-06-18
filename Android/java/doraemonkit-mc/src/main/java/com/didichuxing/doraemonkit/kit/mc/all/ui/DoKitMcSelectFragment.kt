package com.didichuxing.doraemonkit.kit.mc.all.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.util.ToastUtils
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter
import com.didichuxing.doraemonkit.kit.mc.all.DoKitWindowManager
import com.didichuxing.doraemonkit.kit.mc.all.McConstant
import com.didichuxing.doraemonkit.kit.mc.client.DoKitWsClient
import com.didichuxing.doraemonkit.kit.mc.server.HostInfo
import com.didichuxing.doraemonkit.kit.mc.server.RecordingDokitView
import com.didichuxing.doraemonkit.kit.mc.util.McUtil
import com.didichuxing.doraemonkit.mc.R
import com.didichuxing.doraemonkit.util.LogHelper
import com.didichuxing.doraemonkit.zxing.activity.CaptureActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/10-10:52
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DoKitMcSelectFragment : BaseFragment() {
    private val REQUEST_CODE_CAMERA = 0x100
    private val REQUEST_CODE_SCAN = 0x101
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_mc_select
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val server = findViewById<Button>(R.id.tv_host)
        server.setOnClickListener {
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
//                ToastUtils.showShort("暂不支持Android 9以下的系统作为主机")
//                return@setOnClickListener
//            }

            if (activity is DoKitMcActivity) {
                (activity as DoKitMcActivity).changeFragment(DoKitMcActivity.FRAGMENT_HOST)
            }
        }
        val client = findViewById<Button>(R.id.tv_client)
        client.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (activity?.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(Manifest.permission.CAMERA)
                    requestPermissions(permissions, REQUEST_CODE_CAMERA)
                } else {
                    startScan()
                }
            } else {
                startScan()
            }
        }
        val record = findViewById<Button>(R.id.tv_record)
        record.setOnClickListener {
            DoKitConstant.WS_MODE = WSMode.RECORDING
            SimpleDokitStarter.startFloating(RecordingDokitView::class.java)
        }

        val datas = findViewById<Button>(R.id.tv_datas)
        datas.setOnClickListener {
            //SimpleDokitStarter.startFloating(RecordingDokitView::class.java)
        }

    }

    /**
     * 开始扫描
     */
    private fun startScan() {
        val intent = Intent(activity, DoKitMcScanActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_SCAN)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (permissions.isNotEmpty()) {
                for (i in permissions.indices) {
                    if (Manifest.permission.CAMERA == permissions[i] &&
                        grantResults[i] == PackageManager.PERMISSION_GRANTED
                    ) {
                        startScan()
                        return
                    }
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN)) {
                val code = data.getStringExtra(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN)
                if (!TextUtils.isEmpty(code)) {
                    try {
                        val uri = Uri.parse(code)
                        handleScanResult(uri)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        finish()
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
        ToastUtils.showShort("没有扫描到任何内容>_<")
    }

    /**
     * 处理返回结果
     */
    private fun handleScanResult(uri: Uri) {
        DoKitWsClient.connect(uri.host!!, uri.port, uri.path!!) { code, message ->
            withContext(Dispatchers.Main) {
                when (code) {
                    DoKitWsClient.CONNECT_SUCCEED -> {
                        DoKitWindowManager.hookWindowManagerGlobal()
                        McConstant.HOST_INFO =
                            GsonUtils.fromJson<HostInfo>(message, HostInfo::class.java)
                        if (activity is DoKitMcActivity) {
                            (activity as DoKitMcActivity).changeFragment(DoKitMcActivity.FRAGMENT_CLIENT)
                        }
                    }
                    DoKitWsClient.CONNECT_FAIL -> {
                        LogHelper.i(TAG, "message===>$message")
                        ToastUtils.showShort(message)
                    }
                }
            }
        }

    }

}