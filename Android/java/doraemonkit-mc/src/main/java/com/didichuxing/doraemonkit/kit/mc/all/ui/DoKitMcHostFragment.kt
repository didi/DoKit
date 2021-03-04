package com.didichuxing.doraemonkit.kit.mc.all.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.ImageUtils
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.mc.all.DoKitWindowManager
import com.didichuxing.doraemonkit.kit.mc.all.hook.AccessibilityGetInstanceMethodHook
import com.didichuxing.doraemonkit.kit.mc.all.hook.View_onInitializeAccessibilityEventHook
import com.didichuxing.doraemonkit.kit.mc.server.DoKitWsServer
import com.didichuxing.doraemonkit.kit.mc.util.CodeUtils
import com.didichuxing.doraemonkit.kit.mc.util.McUtil
import com.didichuxing.doraemonkit.mc.R
import de.robv.android.xposed.DexposedBridge
import de.robv.android.xposed.XposedHelpers

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
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_mc_host
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ivCode = findViewById<ImageView>(R.id.iv_code)
        val tvHost = findViewById<TextView>(R.id.tv_host)
        val host = "ws://${DoKitConstant.IP_ADDRESS_BY_WIFI}:4444/mc"
        val logo = ImageUtils.getBitmap(R.mipmap.dk_logo)
        val qCode = CodeUtils.createCode(activity, host, logo)
        tvHost.text = host
        ivCode.setImageBitmap(qCode)
        if (DoKitConstant.WS_MODE == WSMode.UNKNOW) {
            DoKitWsServer.start {
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
                McUtil.hookAccessibilityManager(activity)
//                val sendAccessibilityEventInternalMethod = XposedHelpers.findMethodExact(
//                    View::class.java,
//                    "sendAccessibilityEventInternal",
//                    Int::class.java
//                )
//                DexposedBridge.hookMethod(
//                    sendAccessibilityEventInternalMethod,
//                    View_sendAccessibilityEventInternalHook()
//                )
            }

            //绕过无障碍的权限
            DexposedBridge.findAndHookMethod(
                AccessibilityManager::class.java,
                "isEnabled",
                AccessibilityGetInstanceMethodHook()
            )
            //hook sendAccessibilityEventUnchecked
//            val sendAccessibilityEventUncheckedMethod = XposedHelpers.findMethodExact(
//                View::class.java,
//                "sendAccessibilityEventUnchecked",
//                AccessibilityEvent::class.java
//            )
//            DexposedBridge.hookMethod(
//                sendAccessibilityEventUncheckedMethod,
//                View_sendAccessibilityEventUncheckedHook()
//            )


//            val sendAccessibilityEventUncheckedInternalMethod = XposedHelpers.findMethodExact(
//                View::class.java,
//                "sendAccessibilityEventUncheckedInternal",
//                AccessibilityEvent::class.java
//            )
//            DexposedBridge.hookMethod(
//                sendAccessibilityEventUncheckedInternalMethod,
//                View_sendAccessibilityEventUncheckedInternalHook()
//            )

            //hook onInitializeAccessibilityEvent
            val onInitializeAccessibilityEventMethod = XposedHelpers.findMethodExact(
                View::class.java,
                "onInitializeAccessibilityEvent",
                AccessibilityEvent::class.java
            )
            DexposedBridge.hookMethod(
                onInitializeAccessibilityEventMethod,
                View_onInitializeAccessibilityEventHook()
            )


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}