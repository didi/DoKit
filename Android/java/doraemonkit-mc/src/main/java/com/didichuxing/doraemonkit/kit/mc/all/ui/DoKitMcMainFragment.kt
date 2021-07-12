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
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.extension.isTrueWithCor
import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.util.ToastUtils
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter
import com.didichuxing.doraemonkit.kit.mc.ability.McHttpManager
import com.didichuxing.doraemonkit.kit.mc.ability.McHttpManager.RESPONSE_OK
import com.didichuxing.doraemonkit.kit.mc.all.DoKitWindowManager
import com.didichuxing.doraemonkit.kit.mc.all.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.data.McCaseInfo
import com.didichuxing.doraemonkit.kit.mc.client.DoKitWsClient
import com.didichuxing.doraemonkit.kit.mc.data.McConfigInfo
import com.didichuxing.doraemonkit.kit.mc.server.HostInfo
import com.didichuxing.doraemonkit.kit.mc.server.RecordingDokitView
import com.didichuxing.doraemonkit.mc.R
import com.didichuxing.doraemonkit.util.LogHelper
import com.didichuxing.doraemonkit.util.SPUtils
import com.didichuxing.doraemonkit.widget.dialog.DialogListener
import com.didichuxing.doraemonkit.widget.dialog.DialogProvider
import com.didichuxing.doraemonkit.zxing.activity.CaptureActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        LogHelper.e(TAG, "error message: ${throwable.message}")
    }

    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_mc_select
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val server = findViewById<Button>(R.id.tv_host)
        server.setOnClickListener {
            if (DoKitManager.WS_MODE == WSMode.RECORDING) {
                ToastUtils.showShort("当前处于数据录制状态，请先执行上传操作")
                return@setOnClickListener
            }
            if (DoKitMcManager.MC_CASE_ID.isEmpty()) {
                lifecycleScope.launch(exceptionHandler) {
                    privacyInterceptDialog(
                        "操作提醒",
                        "当前未选中任何的数据用例，请确认要否要以数据不同步模式运行？"
                    ).isTrueWithCor {
                        if (activity is DoKitMcActivity) {
                            (activity as DoKitMcActivity).changeFragment(WSMode.HOST)
                        }
                    }
                }
            } else {
                if (activity is DoKitMcActivity) {
                    (activity as DoKitMcActivity).changeFragment(WSMode.HOST)
                }
            }

        }
        val client = findViewById<Button>(R.id.tv_client)
        client.setOnClickListener {

            if (DoKitManager.WS_MODE == WSMode.RECORDING) {
                ToastUtils.showShort("当前处于数据录制状态，请先执行上传操作")
                return@setOnClickListener
            }

            if (DoKitMcManager.MC_CASE_ID.isEmpty()) {
                lifecycleScope.launch(exceptionHandler) {
                    privacyInterceptDialog(
                        "操作提醒",
                        "当前未选中任何的数据用例，请确认要否要以数据不同步模式运行？"
                    ).isTrueWithCor {
                        performScan()
                    }
                }
            } else {
                performScan()
            }

        }
        val record = findViewById<Button>(R.id.tv_record)
        record.setOnClickListener {
            if (DoKitManager.PRODUCT_ID.isEmpty()) {
                ToastUtils.showShort("DoKit初始化时未传入产品id")
                return@setOnClickListener
            }

            if (DoKitManager.WS_MODE == WSMode.RECORDING) {
                ToastUtils.showShort("当前已处于录制状态")
                return@setOnClickListener
            }

            //请求一个CaseId
            lifecycleScope.launch(exceptionHandler) {
                privacyInterceptDialog(
                    "隐私提醒",
                    "用例采集会实时录制并上传接口数据到dokit.cn平台,请确认是否要开启？"
                ).isTrueWithCor(
                    isFalse = {
                        ToastUtils.showShort("取消用例采集")
                    }) {
                    try {
                        val caseInfo = McHttpManager.mockStart<McCaseInfo>()
                        if (caseInfo.code == RESPONSE_OK) {
                            saveRecodingStatus(caseInfo.data)
                        }
                    } catch (e: Exception) {
                        LogHelper.e(TAG, "e===>${e.message}")
                        DoKitManager.WS_MODE = WSMode.UNKNOW
                        ToastUtils.showShort("用例采集启动失败")
                    }
                }
            }

        }


        val upload = findViewById<Button>(R.id.tv_upload)
        upload.setOnClickListener {
            if (DoKitManager.PRODUCT_ID.isEmpty()) {
                ToastUtils.showShort("DoKit初始化时未传入产品id")
                return@setOnClickListener
            }

            if (DoKitMcManager.MC_CASE_ID.isEmpty()) {
                ToastUtils.showShort("请先开始执行用例采集")
                return@setOnClickListener
            }

            lifecycleScope.launch(exceptionHandler) {
                val result = McHttpManager.mockStop<Any>(mcCaseInfoDialog())
                if (result.code == RESPONSE_OK) {
                    SimpleDokitStarter.removeFloating(RecordingDokitView::class.java)
                    SPUtils.getInstance().put(DoKitMcManager.MC_CASE_RECODING_KEY, false)
                    DoKitManager.WS_MODE = WSMode.UNKNOW
                    DoKitMcManager.IS_MC_RECODING = false
                    ToastUtils.showShort("用例上传成功")
                } else {
                    LogHelper.e(TAG, "error msg===>${result.msg}")
                }

            }
        }

        val datas = findViewById<Button>(R.id.tv_datas)
        datas.setOnClickListener {
            if (DoKitManager.PRODUCT_ID.isEmpty()) {
                ToastUtils.showShort("DoKit初始化时未传入产品id")
                return@setOnClickListener
            }
            (requireActivity() as DoKitMcActivity).changeFragment(WSMode.MC_CASELIST)
        }

        //加载exclude key
        if (DoKitManager.PRODUCT_ID.isNotBlank()) {
            lifecycleScope.launch(exceptionHandler) {
                val config = McHttpManager.getMcConfig<McConfigInfo>()
                if (config.code == RESPONSE_OK) {
                    config.data?.multiControl?.exclude?.let {
                        McHttpManager.mExcludeKey = it
                    }
                } else {
                    ToastUtils.showShort(config.msg)
                }

            }
        }


    }

    /**
     * 持久化录制状态 方便重启继续录制
     */
    private fun saveRecodingStatus(configInfo: McCaseInfo?) {
        configInfo?.let {
            DoKitMcManager.MC_CASE_ID = it.caseId
            SimpleDokitStarter.startFloating(RecordingDokitView::class.java)
            DoKitManager.WS_MODE = WSMode.RECORDING
            SPUtils.getInstance().put(DoKitMcManager.MC_CASE_ID_KEY, DoKitMcManager.MC_CASE_ID)
            SPUtils.getInstance().put(DoKitMcManager.MC_CASE_RECODING_KEY, true)
            ToastUtils.showShort("开始用例采集")
        }

    }


    /**
     * 执行扫描
     */
    private fun performScan() {
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
                        DoKitMcManager.HOST_INFO =
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
    private suspend fun privacyInterceptDialog(title: String, content: String): Boolean =
        suspendCoroutine {
            AlertDialog.Builder(requireActivity())
                .setTitle(title)
                .setMessage(content)
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
    private suspend fun mcCaseInfoDialog(): McCaseInfoDialogProvider.CaseInfo = suspendCoroutine {
        showDialog(McCaseInfoDialogProvider(null, object : DialogListener {
            override fun onPositive(dialogProvider: DialogProvider<*>): Boolean {
                val provider = dialogProvider as McCaseInfoDialogProvider
                val (_, _, caseName, personName) = provider.getCaseInfo()

                if (caseName.isBlank()) {
                    ToastUtils.showShort("用例名称不能为空")
                    return false
                }

                if (personName.isBlank()) {
                    ToastUtils.showShort("用例采集人不能为空")
                    return false
                }

                it.resume(
                    McCaseInfoDialogProvider.CaseInfo(
                        caseName = caseName,
                        personName = personName
                    )
                )

                return true
            }

            override fun onNegative(dialogProvider: DialogProvider<*>): Boolean {
                return true
            }

        }))
    }


}