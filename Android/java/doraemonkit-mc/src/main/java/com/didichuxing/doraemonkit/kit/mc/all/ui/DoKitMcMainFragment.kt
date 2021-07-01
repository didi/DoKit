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
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.util.ToastUtils
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter
import com.didichuxing.doraemonkit.kit.mc.ability.McHttpManager
import com.didichuxing.doraemonkit.kit.mc.all.DoKitWindowManager
import com.didichuxing.doraemonkit.kit.mc.all.McConstant
import com.didichuxing.doraemonkit.kit.mc.client.DoKitWsClient
import com.didichuxing.doraemonkit.kit.mc.server.HostInfo
import com.didichuxing.doraemonkit.kit.mc.server.RecordingDokitView
import com.didichuxing.doraemonkit.mc.R
import com.didichuxing.doraemonkit.util.LogHelper
import com.didichuxing.doraemonkit.widget.dialog.DialogListener
import com.didichuxing.doraemonkit.widget.dialog.DialogProvider
import com.didichuxing.doraemonkit.widget.dialog.SimpleDialogListener
import com.didichuxing.doraemonkit.zxing.activity.CaptureActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/10-10:52
 * 描    述：一机多控main fragment
 * 修订历史：
 * ================================================
 */
class DoKitMcMainFragment : BaseFragment() {
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
            if (DoKitConstant.WS_MODE == WSMode.RECORDING) {
                ToastUtils.showShort("当前处于数据录制状态，请先执行上传操作")
                return@setOnClickListener
            }
            if (activity is DoKitMcActivity) {
                (activity as DoKitMcActivity).changeFragment(WSMode.HOST)
            }
        }
        val client = findViewById<Button>(R.id.tv_client)
        client.setOnClickListener {

            if (DoKitConstant.WS_MODE == WSMode.RECORDING) {
                ToastUtils.showShort("当前处于数据录制状态，请先执行上传操作")
                return@setOnClickListener
            }

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
            //请求一个CaseId
            lifecycleScope.launch {

                if (interceptDialogResult()) {
                    //                try {
//                    val result = McHttpManager.mockStart()
//                    DoKitConstant.WS_MODE = WSMode.RECORDING
//                    SimpleDokitStarter.startFloating(RecordingDokitView::class.java)
//                    LogHelper.i(TAG, "result===>$result")
//                } catch (e: Exception) {
//                    DoKitConstant.WS_MODE = WSMode.UNKNOW
//                    ToastUtils.showShort("用例采集启动失败")
//                    LogHelper.i(TAG, "e===>${e.message}  thread===>${Thread.currentThread().name}")
//                }
                    DoKitConstant.WS_MODE = WSMode.RECORDING
                    SimpleDokitStarter.startFloating(RecordingDokitView::class.java)
                    ToastUtils.showShort("用例开始采集")
                } else {
                    ToastUtils.showShort("取消用例采集")
                }

            }


        }


        val upload = findViewById<Button>(R.id.tv_upload)
        upload.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val result = McHttpManager.mockStop(mcCaseInfoDialog())
                    LogHelper.i(TAG, "result===>$result")
                } catch (e: Exception) {
                    ToastUtils.showShort(e.message)
                    LogHelper.i(TAG, "e===>${e.message}  thread===>${Thread.currentThread().name}")
                }
            }
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
                            (activity as DoKitMcActivity).changeFragment(WSMode.CLIENT)
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


    /**
     * 处理dialog返回值
     */
    private suspend fun interceptDialogResult() = suspendCoroutine<Boolean> {
        AlertDialog.Builder(requireActivity())
            .setTitle("隐私提醒")
            .setMessage("开启用例采集会实时录制并上传接口数据到dokit.cn平台!!!")
            .setPositiveButton("开启") { dialog, _ ->
                dialog.dismiss()
                it.resume(true)
            }
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
                it.resume(false)
            }
            .show()

    }


    /**
     * 确认用例信息
     */
    private suspend fun mcCaseInfoDialog() = suspendCoroutine<McCaseInfoDialogProvider.CaseInfo> {
        showDialog(McCaseInfoDialogProvider(null, object : DialogListener {
            override fun onPositive(dialogProvider: DialogProvider<*>): Boolean {
                val provider = dialogProvider as McCaseInfoDialogProvider
                val (caseName, personName) = provider.getCaseInfo()

                if (caseName.isBlank()) {
                    ToastUtils.showShort("用例名称不能为空")
                    return false
                }

                if (personName.isBlank()) {
                    ToastUtils.showShort("用例采集人不能为空")
                    return false
                }

                it.resume(McCaseInfoDialogProvider.CaseInfo(caseName, personName))

                return true
            }

            override fun onNegative(dialogProvider: DialogProvider<*>): Boolean {
                return true
            }

        }))
    }


}