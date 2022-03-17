package com.didichuxing.doraemonkit.kit.mc.all.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.mc.all.DoKitWindowManager
import com.didichuxing.doraemonkit.kit.mc.server.DoKitWsServer
import com.didichuxing.doraemonkit.kit.mc.server.HostDokitView
import com.didichuxing.doraemonkit.kit.mc.util.CodeUtils
import com.didichuxing.doraemonkit.mc.R
import com.didichuxing.doraemonkit.util.ImageUtils
import com.didichuxing.doraemonkit.util.LogHelper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/10-10:52
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DoKitMcHostFragment : BaseFragment() {
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        LogHelper.e(TAG, "error message: ${throwable.message}")
    }

    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_mc_host
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ivCode = findViewById<ImageView>(R.id.iv_code)
        val tvHost = findViewById<TextView>(R.id.tv_host)
        val btnClose = findViewById<Button>(R.id.btn_close)
        btnClose.setOnClickListener {
            lifecycleScope.launch(exceptionHandler) {
                DoKitWsServer.stop {
                    DoKit.removeFloating(HostDokitView::class)
                    if (activity is DoKitMcActivity) {
                        (activity as DoKitMcActivity).onBackPressed()
                    }
                }
            }
        }


        val host = "ws://${DoKitManager.IP_ADDRESS_BY_WIFI}:${DoKitManager.MC_WS_PORT}/mc"
        val logo = ImageUtils.getBitmap(R.mipmap.dk_logo)
        val qCode = CodeUtils.createCode(activity, host, logo)
        tvHost.text = host
        ivCode.setImageBitmap(qCode)

        if (DoKitManager.WS_MODE != WSMode.HOST) {
            DoKitWsServer.start {
                //启动悬浮窗
                DoKit.launchFloating(HostDokitView::class)
                DoKitWindowManager.hookWindowManagerGlobal()
                if (!DoKitWindowManager.HOOK_ENABLE) {
                    DoKitWindowManager.runTimeHook(activity)
                }
            }
        }
    }


}
