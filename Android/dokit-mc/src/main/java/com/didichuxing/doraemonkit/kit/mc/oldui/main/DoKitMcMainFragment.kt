package com.didichuxing.doraemonkit.kit.mc.oldui.main

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.test.TestMode
import com.didichuxing.doraemonkit.extension.isTrueWithCor
import com.didichuxing.doraemonkit.util.ToastUtils
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.test.mock.http.HttpMockServer
import com.didichuxing.doraemonkit.kit.test.mock.http.HttpMockServer.RESPONSE_OK
import com.didichuxing.doraemonkit.kit.mc.oldui.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.ui.DoKitMcActivity
import com.didichuxing.doraemonkit.kit.mc.ui.adapter.McCaseInfoDialogProvider
import com.didichuxing.doraemonkit.kit.mc.ui.McPages
import com.didichuxing.doraemonkit.kit.test.mock.data.McCaseInfo
import com.didichuxing.doraemonkit.kit.test.mock.data.McConfigInfo
import com.didichuxing.doraemonkit.kit.mc.oldui.record.RecordingDoKitView
import com.didichuxing.doraemonkit.kit.test.mock.data.CaseInfo
import com.didichuxing.doraemonkit.kit.mc.utils.McCaseUtils
import com.didichuxing.doraemonkit.kit.test.DoKitTestManager
import com.didichuxing.doraemonkit.mc.R
import com.didichuxing.doraemonkit.util.LogHelper
import com.didichuxing.doraemonkit.util.SPUtils
import com.didichuxing.doraemonkit.widget.dialog.DialogListener
import com.didichuxing.doraemonkit.widget.dialog.DialogProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
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

    private val mExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        LogHelper.e(TAG, "error message: ${throwable.message}")
    }

    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_mc_select
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (DoKitMcManager.MC_CASE_ID.isEmpty()) {
            DoKitMcManager.MC_CASE_ID = McCaseUtils.loadCaseId()
        }

        val host = findViewById<Button>(R.id.tv_host)
        host.setOnClickListener {
            if (DoKitMcManager.CONNECT_MODE != TestMode.UNKNOWN) {
                ToastUtils.showShort("当前处于联网模式，请先关闭！")
                return@setOnClickListener
            }
            checkMcPreparedState {
                if (activity is DoKitMcActivity) {
                    (activity as DoKitMcActivity).pushFragment(McPages.HOST)
                }
            }
        }
        val client = findViewById<Button>(R.id.tv_client)
        client.setOnClickListener {
            if (DoKitMcManager.CONNECT_MODE != TestMode.UNKNOWN) {
                ToastUtils.showShort("当前处于联网模式，请先关闭！")
                return@setOnClickListener
            }
            checkMcPreparedState {
                if (activity is DoKitMcActivity) {
                    (activity as DoKitMcActivity).pushFragment(McPages.CLIENT_HISTORY)
                }
            }

        }

        val record = findViewById<Button>(R.id.tv_record)
        record.setOnClickListener {
            if (DoKitManager.PRODUCT_ID.isEmpty()) {
                ToastUtils.showShort("DoKit初始化时未传入产品id")
                return@setOnClickListener
            }

            if (DoKitTestManager.isHostMode()) {
                ToastUtils.showShort("当前已处于录制状态")
                return@setOnClickListener
            }

            //请求一个CaseId
            lifecycleScope.launch(mExceptionHandler) {
                privacyInterceptDialog(
                    "隐私提醒",
                    """1.用例采集会实时录制并上传接口数据到dokit.cn平台,请确认是否要开启？
2. 请确认已在dokit.cn平台一机多控模块添加诸如token、sign等无法确认接口唯一性的exclude字段(字段作用于全部录制接口)。
                            """
                ).isTrueWithCor(
                    isFalse = {
                        ToastUtils.showShort("取消用例采集")
                    }) {
                    try {
                        val caseInfo = HttpMockServer.mockStart<McCaseInfo>()
                        if (caseInfo.code == RESPONSE_OK) {
                            saveRecodingStatus(caseInfo.data)
                        }
                    } catch (e: Exception) {
                        LogHelper.e(TAG, "e===>${e.message}")
                        DoKitTestManager.closeTest()
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

            lifecycleScope.launch(mExceptionHandler) {
                val result = HttpMockServer.mockStop<Any>(mcCaseInfoDialog())
                if (result.code == RESPONSE_OK) {
                    DoKit.removeFloating(RecordingDoKitView::class)
                    SPUtils.getInstance().put(DoKitMcManager.MC_CASE_RECODING_KEY, false)
                    DoKitTestManager.closeTest()
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
            (requireActivity() as DoKitMcActivity).pushFragment(McPages.MC_CASELIST)
        }

        //加载exclude key
        if (DoKitManager.PRODUCT_ID.isNotBlank()) {
            lifecycleScope.launch(mExceptionHandler) {
                val config = HttpMockServer.getMcConfig<McConfigInfo>()
                if (config.code == RESPONSE_OK) {
                    config.data?.multiControl?.exclude?.let {
                        HttpMockServer.mExcludeKey = it
                    }
                } else {
                    ToastUtils.showShort(config.msg)
                }

            }
        }

    }

    private fun checkMcPreparedState(callback: () -> Unit) {
        if (DoKitTestManager.isHostMode()) {
            ToastUtils.showShort("当前处于数据录制状态，请先执行上传操作")
            return
        }
        if (DoKitMcManager.MC_CASE_ID.isEmpty()) {
            lifecycleScope.launch(mExceptionHandler) {
                privacyInterceptDialog(
                    "操作提醒",
                    "当前未选中任何的数据用例，请确认要否要以数据不同步模式运行？"
                ).isTrueWithCor {
                    callback()
                }
            }
        } else {
            callback()
        }
    }

    /**
     * 持久化录制状态 方便重启继续录制
     */
    private fun saveRecodingStatus(configInfo: McCaseInfo?) {
        configInfo?.let {
            DoKitMcManager.MC_CASE_ID = it.caseId
            DoKit.launchFloating(RecordingDoKitView::class.java)
            DoKitTestManager.startTest(TestMode.HOST)
            SPUtils.getInstance().put(DoKitMcManager.MC_CASE_ID_KEY, DoKitMcManager.MC_CASE_ID)
            SPUtils.getInstance().put(DoKitMcManager.MC_CASE_RECODING_KEY, true)
            ToastUtils.showShort("开始用例采集")
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
                .setCancelable(false)
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
    private suspend fun mcCaseInfoDialog(): CaseInfo = suspendCoroutine {
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
                    CaseInfo(
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
