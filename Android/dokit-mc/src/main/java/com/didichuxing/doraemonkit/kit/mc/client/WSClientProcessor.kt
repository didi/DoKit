package com.didichuxing.doraemonkit.kit.mc.client

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.view.accessibility.AccessibilityEvent
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.constant.WSEType
import com.didichuxing.doraemonkit.extension.doKitGlobalScope
import com.didichuxing.doraemonkit.extension.isFalse
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.core.DokitFrameLayout
import com.didichuxing.doraemonkit.kit.mc.all.DoKitWindowManager
import com.didichuxing.doraemonkit.kit.mc.all.WSEvent
import com.didichuxing.doraemonkit.kit.mc.all.view_info.ViewC12c
import com.didichuxing.doraemonkit.kit.mc.server.HostInfo
import com.didichuxing.doraemonkit.kit.mc.util.ViewPathUtil
import com.didichuxing.doraemonkit.util.*
import kotlinx.coroutines.launch


/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/18-17:32
 * 描    述：
 * 修订历史：
 * ================================================
 */
object WSClientProcessor {
    private var mHostInfo: HostInfo? = null
    private var mRatioX: Float? = null
    private var mRatioY: Float? = null


    /**
     * 处理来自主机的消息
     */
    fun process(wsEvent: WSEvent) {
        //LogHelper.i(TAG, "wsEvent===>$wsEvent")
        when (wsEvent.eventType) {
            WSEType.WSE_TEST -> {
                //ToastUtils.showShort(wsEvent.message)
            }

            WSEType.WSE_CUSTOM_EVENT -> {
                if (DoKitManager.MC_CLIENT_PROCESSOR == null) {
                    return
                }

                wsEvent.viewC12c?.let { viewC12c ->
                    if (viewC12c.viewPaths == null) {
                        DoKitManager.MC_CLIENT_PROCESSOR?.process(
                            ActivityUtils.getTopActivity(),
                            null,
                            viewC12c.customEventType,
                            GsonUtils.fromJson(
                                viewC12c.customParams,
                                Map::class.java
                            ) as Map<String, String>
                        )
                        return
                    }

                    if (DoKitWindowManager.ROOT_VIEWS == null || viewC12c.viewRootImplIndex == -1) {
                        ToastUtils.showShort("匹配控件失败，请手动操作")
                        return
                    }
                    var viewRootImpl: ViewParent? = null
                    DoKitWindowManager.ROOT_VIEWS?.let { rootViews ->
                        viewRootImpl = rootViews[viewC12c.viewRootImplIndex]
                    }
                    viewRootImpl?.let {
                        val decorView: ViewGroup =
                            ReflectUtils.reflect(it).field("mView").get<View>() as ViewGroup
                        val targetView: View? =
                            ViewPathUtil.findViewByViewParentInfo(decorView, viewC12c.viewPaths)
                        targetView?.let { target ->
                            DoKitManager.MC_CLIENT_PROCESSOR?.process(
                                ActivityUtils.getTopActivity(),
                                target,
                                viewC12c.customEventType,
                                GsonUtils.fromJson(
                                    viewC12c.customParams,
                                    Map::class.java
                                ) as Map<String, String>
                            )
                        } ?: run {
                            DoKitManager.MC_CLIENT_PROCESSOR?.process(
                                ActivityUtils.getTopActivity(),
                                null,
                                viewC12c.customEventType,
                                GsonUtils.fromJson(
                                    viewC12c.customParams,
                                    Map::class.java
                                ) as Map<String, String>
                            )
                            ToastUtils.showShort("匹配控件失败，请手动操作")
                        }
                    } ?: run {
                        DoKitManager.MC_CLIENT_PROCESSOR?.process(
                            ActivityUtils.getTopActivity(),
                            null,
                            viewC12c.customEventType,
                            GsonUtils.fromJson(
                                viewC12c.customParams,
                                Map::class.java
                            ) as Map<String, String>
                        )
                        ToastUtils.showShort("无法确定当前控件所属窗口")
                    }

                } ?: run { ToastUtils.showShort("解析长连接数据失败") }

            }

            //主机断开
            WSEType.WSE_HOST_CLOSE -> {
                doKitGlobalScope.launch {
                    DoKitWsClient.close()
                }
                DoKit.removeFloating(ClientDokitView::class)
                if (ActivityUtils.getTopActivity() != null) {
                    AlertDialog.Builder(ActivityUtils.getTopActivity())
                        .setTitle("一机多控")
                        .setMessage("主机已断开连接！")
                        .setPositiveButton("确认") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setNegativeButton("取消") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }

            }
            /**
             * 切换到前台
             */
            WSEType.APP_ON_FOREGROUND -> {
                val activityName = wsEvent.commParams?.get("activityName")
                activityName?.let {
                    val clazz: Class<Activity> =
                        Class.forName(it) as Class<Activity>
                    DoKitCommUtil.changeAppOnForeground(clazz)
                }

            }
            /**
             * 切换到后台
             */
            WSEType.APP_ON_BACKGROUND -> {
                ActivityUtils.startHomeActivity()
            }

            WSEType.WSE_CONNECTED -> {

                mHostInfo = GsonUtils.fromJson<HostInfo>(
                    wsEvent.commParams?.get("hostInfo"),
                    HostInfo::class.java
                )
                mHostInfo?.let {
                    mRatioX = ScreenUtils.getAppScreenHeight() / mHostInfo?.width!!
                    mRatioY = ScreenUtils.getAppScreenHeight() / mHostInfo?.height!!
                    ToastUtils.showShort("已经连接到${it.deviceName}主机")
                }
            }

            /**
             * 模拟返回事件
             */
            WSEType.ACTIVITY_BACK_PRESSED -> {
                val topActivity = ActivityUtils.getTopActivity()
                if (wsEvent.commParams?.get("activityName") == topActivity::class.tagName) {
                    topActivity.onBackPressed()
                }

            }

            WSEType.ACTIVITY_FINISH -> {
                val topActivity = ActivityUtils.getTopActivity()
                if (wsEvent.commParams?.get("activityName") == topActivity::class.tagName) {
                    topActivity.finish()
                }
            }

            WSEType.WSE_COMM_EVENT -> {
                wsEvent.commParams?.let {
//                    LogHelper.json(
//                        TAG,
//                        "ServerActivityName===${it["activityName"]}    ClientActivityName===>${ActivityUtils.getTopActivity()::class.java.canonicalName}"
//                    )
                    if (it["activityName"] != ActivityUtils.getTopActivity()::class.tagName) {
                        ToastUtils.showShort("当前测试和主机不处于同一个页面，请手动调整同步")
                        return
                    }

                }

                wsEvent.viewC12c?.let { viewC12c ->
                    if (DoKitWindowManager.ROOT_VIEWS == null || viewC12c.viewRootImplIndex == -1) {
                        ToastUtils.showShort("匹配控件失败，请手动操作")
                        return
                    }
                    var viewRootImpl: ViewParent? = null
                    DoKitWindowManager.ROOT_VIEWS?.let { rootViews ->
                        viewRootImpl = rootViews[viewC12c.viewRootImplIndex]
                    }
                    viewRootImpl?.let {
                        val decorView: ViewGroup =
                            ReflectUtils.reflect(it).field("mView").get<View>() as ViewGroup
                        val targetView: View? =
                            ViewPathUtil.findViewByViewParentInfo(decorView, viewC12c.viewPaths)
                        targetView?.let { target -> comm(viewC12c, target) }
                            ?: run { ToastUtils.showShort("匹配控件失败，请手动操作") }
                    } ?: run { ToastUtils.showShort("无法确定当前控件所属窗口") }
                } ?: run { ToastUtils.showShort("无法获取手势控件信息") }
            }
            else -> {
            }
        }
    }


    /**
     * 通用的处理方式
     */
    private fun comm(viewC12c: ViewC12c, target: View) {
        when (viewC12c.commEventType) {
            //单击
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                if (target is Switch) {
                    target.toggle()
                } else if (target is RadioButton) {
                    target.isChecked = true
                } else {
                    if (target.hasOnClickListeners()) {
                        target.performClick().isFalse {
                            ToastUtils.showShort("模拟点击事件失败")
                        }
                    } else {
                        //兼容adapter
                        if (target.parent is AdapterView<*>) {
                            (target.parent as AdapterView<*>).performItemClick(
                                target,
                                viewC12c.viewPaths?.get(1)!!.currentEventPosition,
                                target.id.toLong()
                            )
                        } else {
                            ToastUtils.showShort("该控件没有设置点击事件")
                        }

                    }
                }
            }
            //长按
            AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> {
                when (target) {
                    is EditText -> {
                        target.selectAll()
                    }
                    else -> {
                        target.performLongClick().isFalse {
                            ToastUtils.showShort("模拟长按事件失败")
                        }
                    }
                }
            }
            // view 获取焦点

            AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
                target.requestFocus()
            }
            //EditText 文字改变
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                if (target is TextView) {
                    target.text = viewC12c.text
                }
            }
            //EditText 光标变动
            AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> {
                if (target is EditText) {
                    target.setSelection(
                        viewC12c.accEventInfo?.fromIndex!!,
                        viewC12c.accEventInfo.toIndex!!
                    )
                }
            }
            //滚动
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                dealScrollEvent(target, viewC12c)
            }

            //处理dokit view的拖动
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {

                if (target is DokitFrameLayout && target.layoutParams is FrameLayout.LayoutParams) {
                    val layoutParams: FrameLayout.LayoutParams =
                        target.layoutParams as FrameLayout.LayoutParams
                    layoutParams.leftMargin =
                        viewC12c.dokitViewPosInfo?.leftMargin!!
                    layoutParams.topMargin =
                        viewC12c.dokitViewPosInfo.topMargin
                    target.layoutParams = layoutParams
                }

            }
            else -> {

            }
        }
    }


    /**
     * 处理滑动事件
     */
    private fun dealScrollEvent(targetView: View?, viewC12c: ViewC12c) {

        when (targetView) {
            is ScrollView -> {
                viewC12c.accEventInfo?.let { accEventInfo ->
                    targetView.smoothScrollTo(
                        ConvertUtils.dp2px(accEventInfo.scrollX!!.toFloat()),
                        ConvertUtils.dp2px(accEventInfo.scrollY!!.toFloat())
                    )
                }
            }

            is NestedScrollView -> {
                viewC12c.accEventInfo?.let { accEventInfo ->
                    targetView.smoothScrollTo(
                        ConvertUtils.dp2px(accEventInfo.scrollX!!.toFloat()),
                        ConvertUtils.dp2px(accEventInfo.scrollY!!.toFloat())
                    )
                }
            }

            is HorizontalScrollView -> {
                viewC12c.accEventInfo?.let { accEventInfo ->
                    targetView.smoothScrollTo(
                        ConvertUtils.dp2px(accEventInfo.scrollX!!.toFloat()),
                        ConvertUtils.dp2px(accEventInfo.scrollY!!.toFloat())
                    )
                }
            }

            is RecyclerView -> {
                viewC12c.accEventInfo?.let { accEventInfo ->
                    when {
                        accEventInfo.fromIndex!! == 0 -> {
                            targetView.smoothScrollToPosition(0)
                        }
                        accEventInfo.toIndex!! == targetView.adapter?.itemCount!! - 1 -> {
                            targetView.smoothScrollToPosition(accEventInfo.toIndex)
                        }
                        else -> {
                            moveToPosition(targetView, accEventInfo.fromIndex)
                        }
                    }


//                    if (targetView.layoutManager is LinearLayoutManager) {
//                        val layoutManager = targetView.layoutManager as LinearLayoutManager
//                        if (layoutManager.orientation == RecyclerView.VERTICAL) {
//                            if (targetView.scrollY <= ConvertUtils.dp2px(10.0f)) {
//                                targetView.smoothScrollBy(
//                                    ConvertUtils.dp2px(accEventInfo.scrollDeltaX!!.toFloat()),
//                                    ConvertUtils.dp2px(accEventInfo.scrollDeltaY!!.toFloat())
//                                )
//                            }
//
//                        } else {
//                            if (targetView.scrollX <= ConvertUtils.dp2px(10.0f)) {
//                                targetView.smoothScrollBy(
//                                    ConvertUtils.dp2px(accEventInfo.scrollDeltaX!!.toFloat()),
//                                    ConvertUtils.dp2px(accEventInfo.scrollDeltaY!!.toFloat())
//                                )
//                            }
//                        }
//                    }
                }
            }

            is ListView -> {
                viewC12c.accEventInfo?.let { accEventInfo ->
                    when {
                        accEventInfo.fromIndex!! == 0 -> {
                            targetView.smoothScrollToPosition(0)
                        }
                        accEventInfo.toIndex!! + 1 == targetView.adapter?.count -> {
                            targetView.smoothScrollToPosition(accEventInfo.toIndex)
                        }
                        else -> {
                            moveToPosition(targetView, accEventInfo.fromIndex)
                        }
                    }

                    //targetView.smoothScrollToPosition(accEventInfo.fromIndex!!)
                }
            }

            is ViewPager -> {
                viewC12c.accEventInfo?.let {
                    targetView.setCurrentItem(it.toIndex!!, true)
                }
            }

            else -> {

            }
        }


    }

    private fun moveToPosition(rv: RecyclerView, position: Int) {
        if (position != -1) {
            rv.scrollToPosition(position)
            if (rv.layoutManager is LinearLayoutManager) {
                val mLayoutManager = rv.layoutManager as LinearLayoutManager
                mLayoutManager.scrollToPositionWithOffset(position, 0)
            }
        }
    }

    private fun moveToPosition(lv: ListView, position: Int) {
        if (position != -1) {
            lv.smoothScrollToPositionFromTop(position, 0)
        }
    }

}


