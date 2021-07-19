package com.didichuxing.doraemonkit.kit.mc.all.hook

import android.app.Activity
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
import com.didichuxing.doraemonkit.kit.mc.all.*
import com.didichuxing.doraemonkit.kit.mc.server.DoKitWsServer
import com.didichuxing.doraemonkit.kit.mc.all.view_info.DokitViewInfo
import com.didichuxing.doraemonkit.kit.mc.all.view_info.ViewC12c
import com.didichuxing.doraemonkit.util.ReflectUtils
import de.robv.android.xposed.XC_MethodHook

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/30-19:55
 * 描    述：View# sendAccessibilityEventUnchecked hook
 * wiki:https://blog.csdn.net/u011391629/article/details/83343312
 * http://lionoggo.com/2018/03/22/%E6%B7%B1%E5%85%A5Android%E8%BE%85%E5%8A%A9%E6%9C%8D%E5%8A%A1%E6%9E%B6%E6%9E%84%E4%B8%8E%E8%AE%BE%E8%AE%A1/
 * 修订历史：
 * ================================================
 */
class View_sendAccessibilityEventInternalHook : XC_MethodHook() {

    companion object {
        const val TAG = "SendAccessibilityEventMethodHook"
    }


    /**
     * https://developer.android.google.cn/reference/android/view/accessibility/AccessibilityEvent
     */
    override fun afterHookedMethod(param: MethodHookParam?) {
        super.afterHookedMethod(param)
        param?.let {
            val view = it.thisObject as View
            val eventType = it.args[0] as Int

            var viewC12c: ViewC12c? = null
            when (eventType) {
                // on a View like Button, CompoundButton
                AccessibilityEvent.TYPE_VIEW_CLICKED,
                    // represents the event of scrolling a view
                AccessibilityEvent.TYPE_VIEW_SCROLLED,
                    // represents the event of long clicking on a View like Button, CompoundButton
                AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> {
                    viewC12c = createViewC12c(view, eventType)

                    if (DoKitManager.WS_MODE == WSMode.HOST) {
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
//                        LogHelper.json(
//                            TAG, wsEvent
//                        )
                        DoKitWsServer.send(wsEvent)
                    }

                }


                //针对 AdapterView
                AccessibilityEvent.TYPE_VIEW_SELECTED -> {

                }

                //针对 EditText
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {

                }

                // represents the event of changing the text selection of an EditText
                AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> {

                }
                else -> {
                }
            }

        }

    }


    private fun createViewC12c(
        view: View,
        eventType: Int
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
        return ViewC12c()
    }


//    private fun transformAccEventInfo(eventType: Int, view: View): AccEventInfo {
//        return AccEventInfo(
//            eventType,
//            view::class.java.canonicalName,
//            AppUtils.getAppPackageName(),
//            -1,
////            acc.text,
//            getBeforeText(view),
//            acc.fromIndex,
//            acc.addedCount,
//            acc.removedCount,
//            acc.movementGranularity,
//            acc.toIndex,
//            acc.action,
//            acc.maxScrollX,
//            acc.maxScrollY,
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                acc.scrollDeltaX
//            } else {
//                -1
//            },
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                acc.scrollDeltaY
//            } else {
//                -1
//            },
//            acc.scrollX,
//            acc.scrollY,
//            acc.isScrollable,
//            acc.contentChangeTypes,
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                acc.windowChanges
//            } else {
//                -1
//            }
//        )
//    }

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

    private fun getBeforeText(view: View): String {
        if (view is TextView) {
            val changeWatcher = ReflectUtils.reflect(view).field("mChangeWatcher").get<Any>()
            if (changeWatcher == null) {
                return ""
            } else {
                return ReflectUtils.reflect(changeWatcher).field("mBeforeText").get<CharSequence>()
                    .toString()
            }
        }
        return ""
    }

}