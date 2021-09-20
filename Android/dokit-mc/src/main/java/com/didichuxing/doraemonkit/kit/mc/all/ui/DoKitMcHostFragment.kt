package com.didichuxing.doraemonkit.kit.mc.all.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.mc.all.DoKitWindowManager
import com.didichuxing.doraemonkit.kit.mc.all.hook.AccessibilityGetInstanceMethodHook
import com.didichuxing.doraemonkit.kit.mc.all.hook.View_onClickListenerEventHook
import com.didichuxing.doraemonkit.kit.mc.all.hook.View_onInitializeAccessibilityEventHook
import com.didichuxing.doraemonkit.kit.mc.all.hook.View_onTouchEventHook
import com.didichuxing.doraemonkit.kit.mc.server.DoKitWsServer
import com.didichuxing.doraemonkit.kit.mc.server.HostDokitView
import com.didichuxing.doraemonkit.kit.mc.util.CodeUtils
import com.didichuxing.doraemonkit.kit.mc.util.McHookUtil
import com.didichuxing.doraemonkit.mc.R
import com.didichuxing.doraemonkit.util.ImageUtils
import com.didichuxing.doraemonkit.util.LogHelper
import de.robv.android.xposed.DexposedBridge
import de.robv.android.xposed.XposedHelpers
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
                        (activity as DoKitMcActivity).changeFragment(WSMode.UNKNOW)
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
                runTimeHook()
            }
        }
    }

    /**
     * hook
     */
    private fun runTimeHook() {
        try {
            //对于Android9.0以下的系统 需要反射将AccessibilityManager.mIsEnabled变量改成true
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                McHookUtil.hookAccessibilityManager(activity)
            }

            //绕过无障碍的权限
            DexposedBridge.findAndHookMethod(
                AccessibilityManager::class.java,
                "isEnabled",
                AccessibilityGetInstanceMethodHook()
            )


            //hook onInitializeAccessibilityEvent
//            val onInitializeAccessibilityEventMethod = XposedHelpers.findMethodExact(
//                View::class.java,
//                "onInitializeAccessibilityEvent",
//                AccessibilityEvent::class.java
//            )
//            DexposedBridge.hookMethod(
//                onInitializeAccessibilityEventMethod,
//                View_onInitializeAccessibilityEventHook()
//            )
            DexposedBridge.findAndHookMethod(
                View::class.java,
                "onInitializeAccessibilityEvent",
                AccessibilityEvent::class.java,
                View_onInitializeAccessibilityEventHook()
            )


            //hook View#onTouchEvent
//            val onTouchEventEventMethod = XposedHelpers.findMethodExact(
//                View::class.java,
//                "onTouchEvent",
//                MotionEvent::class.java
//            )
//            DexposedBridge.hookMethod(
//                onTouchEventEventMethod,
//                View_onTouchEventHook()
//            )

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}