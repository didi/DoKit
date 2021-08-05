package com.didichuxing.doraemonkit.kit.mc.all.hook

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout
import android.widget.TextView
import com.didichuxing.doraemonkit.util.ActivityUtils
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.constant.WSEType
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.core.DokitFrameLayout
import com.didichuxing.doraemonkit.kit.core.McClientProcessor
import com.didichuxing.doraemonkit.kit.mc.all.DoKitWindowManager
import com.didichuxing.doraemonkit.kit.mc.all.WSEvent
import com.didichuxing.doraemonkit.kit.mc.all.view_info.AccEventInfo
import com.didichuxing.doraemonkit.kit.mc.all.view_info.DokitViewInfo
import com.didichuxing.doraemonkit.kit.mc.all.view_info.ViewC12c
import com.didichuxing.doraemonkit.kit.mc.server.DoKitWsServer
import com.didichuxing.doraemonkit.kit.mc.util.ViewPathUtil
import com.didichuxing.doraemonkit.util.ConvertUtils
import com.didichuxing.doraemonkit.util.LogHelper
import de.robv.android.xposed.XC_MethodHook

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/30-19:55
 * 描    述：View# onInitializeAccessibility hook
 * wiki:https://blog.csdn.net/u011391629/article/details/83343312
 * http://lionoggo.com/2018/03/22/%E6%B7%B1%E5%85%A5Android%E8%BE%85%E5%8A%A9%E6%9C%8D%E5%8A%A1%E6%9E%B6%E6%9E%84%E4%B8%8E%E8%AE%BE%E8%AE%A1/
 * https://blog.csdn.net/omnispace/article/details/70598515
 * 修订历史：sendAccessibilityEventUncheckedInternal 中会被调用
 * ================================================
 */
class View_onInitializeAccessibilityEventHook : XC_MethodHook() {

    companion object {
        const val TAG = "onInitializeAccessibilityEventHook"
    }


    /**
     * https://developer.android.google.cn/reference/android/view/accessibility/AccessibilityEvent
     */
    override fun afterHookedMethod(param: MethodHookParam?) {
        super.afterHookedMethod(param)
        if (DoKitManager.WS_MODE != WSMode.HOST) {
            return
        }
        param?.let {
            val view = it.thisObject as View
            val accessibilityEvent = it.args[0] as AccessibilityEvent


//            LogHelper.i(
//                TAG,
//                "view==>$view  accessibilityEvent.eventType===${accessibilityEvent.eventType}"
//            )
            if (view is DokitFrameLayout && accessibilityEvent.eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED) {
                return
            }

            //通用
            comm(view, accessibilityEvent)

        }

    }


    /**
     * 通用的ws信息处理
     */
    private fun comm(view: View, accessibilityEvent: AccessibilityEvent) {
        val viewC12c: ViewC12c?
        when (accessibilityEvent.eventType) {
            // on a View like Button, CompoundButton
            AccessibilityEvent.TYPE_VIEW_CLICKED,
                // represents the event of scrolling a view
            AccessibilityEvent.TYPE_VIEW_SCROLLED,
                // represents the event of long clicking on a View like Button, CompoundButton
            AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> {
                viewC12c = createViewC12c(view, accessibilityEvent)
                //LogHelper.i(TAG, "viewCharacteristic===>$viewC12c")
                val wsEvent = WSEvent(
                    WSMode.HOST,
                    WSEType.WSE_COMM_EVENT,
                    mutableMapOf(
                        "activityName" to if (view.context is Activity) {
                            view.context::class.tagName
                        } else {
                            ActivityUtils.getTopActivity()::class.tagName
                        }
                    ),
                    viewC12c
                )
                LogHelper.json(
                    TAG, wsEvent
                )
                DoKitWsServer.send(wsEvent)
            }
            /**
             * view 获取焦点
             */
            AccessibilityEvent.TYPE_VIEW_FOCUSED,
                //针对 EditText 文字改变
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED,
                // represents the event of changing the text selection of an EditText
            AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> {

                viewC12c = createViewC12c(view, accessibilityEvent)
                //LogHelper.i(TAG, "viewCharacteristic===>$viewC12c")
                val wsEvent = WSEvent(
                    WSMode.HOST,
                    WSEType.WSE_COMM_EVENT,
                    mutableMapOf(
                        "activityName" to if (view.context is Activity) {
                            view.context::class.tagName
                        } else {
                            ActivityUtils.getTopActivity()::class.tagName
                        }
                    ),
                    viewC12c
                )
//                LogHelper.json(
//                    TAG, wsEvent
//                )
                DoKitWsServer.send(wsEvent)
            }
            //针对dokit悬浮窗
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                if (view is DokitFrameLayout) {
                    if (DoKitManager.WS_MODE == WSMode.HOST) {
                        viewC12c = createViewC12c(view, accessibilityEvent)
                        //LogHelper.i(TAG, "viewCharacteristic===>$viewC12c")
                        val wsEvent = WSEvent(
                            WSMode.HOST,
                            WSEType.WSE_COMM_EVENT,
                            mapOf(
                                "activityName" to if (view.context is Activity) {
                                    view.context::class.tagName
                                } else {
                                    ActivityUtils.getTopActivity()::class.tagName
                                }
                            ),
                            viewC12c
                        )
                        LogHelper.json(
                            TAG, wsEvent
                        )
                        DoKitWsServer.send(wsEvent)
                    }
                }
            }


            //针对 AdapterView
            AccessibilityEvent.TYPE_VIEW_SELECTED -> {
                LogHelper.i(TAG, "TYPE_VIEW_SELECTED====>$view")
            }
            else -> {
            }
        }
    }


    private fun createViewC12c(
        view: View,
        acc: AccessibilityEvent
    ): ViewC12c {
        var viewRootImplIndex: Int = -1
        DoKitWindowManager.ROOT_VIEWS?.let {
            if (view.rootView.parent == null) {
//                val decorView =
//                    ActivityUtils.getTopActivity().window.decorView as ViewParent
                viewRootImplIndex = it.size - 1
                // LogHelper.i(TAG, "${view.rootView}")
            } else {
                viewRootImplIndex = it.indexOf(view.rootView.parent)
            }
        }
        return ViewC12c(
            commEventType = acc.eventType,
            viewRootImplIndex = viewRootImplIndex,
            viewPaths = ViewPathUtil.createViewPathOfWindow(view),
            accEventInfo = transformAccEventInfo(acc),
            text = if (view is TextView) {
                view.text.toString()
            } else {
                ""
            },
            dokitViewPosInfo = createDokitViewInfo(view)
        )
    }


    /**
     * 创建dokitview info
     */
    private fun createDokitViewInfo(view: View): DokitViewInfo? {
        if (view !is DokitFrameLayout) {
            return null
        }

        if (view.layoutParams !is FrameLayout.LayoutParams) {
            return null
        }

        return DokitViewInfo(
            (view.layoutParams as FrameLayout.LayoutParams).leftMargin,
            (view.layoutParams as FrameLayout.LayoutParams).topMargin
        )

    }

    private fun transformAccEventInfo(acc: AccessibilityEvent): AccEventInfo {
        return AccEventInfo(
            acc.eventType,
            acc.className?.toString(),
            acc.packageName?.toString(),
            acc.eventTime,
//            acc.text,
            acc.beforeText?.toString(),
            acc.fromIndex,
            acc.addedCount,
            acc.removedCount,
            acc.movementGranularity,
            acc.toIndex,
            acc.action,
            ConvertUtils.px2dp(acc.maxScrollX.toFloat()),
            ConvertUtils.px2dp(acc.maxScrollY.toFloat()),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ConvertUtils.px2dp(acc.scrollDeltaX.toFloat())
            } else {
                -1
            },
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ConvertUtils.px2dp(acc.scrollDeltaY.toFloat())
            } else {
                -1
            },
            ConvertUtils.px2dp(acc.scrollX.toFloat()),
            ConvertUtils.px2dp(acc.scrollY.toFloat()),
            acc.isScrollable,
            acc.contentChangeTypes,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                acc.windowChanges
            } else {
                -1
            }
        )
    }

}