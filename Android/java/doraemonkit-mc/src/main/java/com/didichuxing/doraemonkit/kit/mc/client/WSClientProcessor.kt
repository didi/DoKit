package com.didichuxing.doraemonkit.kit.mc.client

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.view.accessibility.AccessibilityEvent
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.*
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.extension.isFalse
import com.didichuxing.doraemonkit.kit.core.DokitFrameLayout
import com.didichuxing.doraemonkit.kit.core.MCInterceptor
import com.didichuxing.doraemonkit.kit.mc.all.DoKitWindowManager
import com.didichuxing.doraemonkit.kit.mc.all.WSEType
import com.didichuxing.doraemonkit.kit.mc.all.WSEvent
import com.didichuxing.doraemonkit.kit.mc.all.view_info.ViewC12c
import com.didichuxing.doraemonkit.kit.mc.server.HostInfo
import com.didichuxing.doraemonkit.kit.mc.util.ViewPathUtil
import com.didichuxing.doraemonkit.util.DokitUtil


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

            /**
             * 切换到前台
             */
            WSEType.APP_ON_FOREGROUND -> {
                val activityName = wsEvent.commParams?.get("activityName")
                activityName?.let {
                    val clazz: Class<Activity> =
                        Class.forName(it) as Class<Activity>
                    DokitUtil.changeAppOnForeground(clazz)
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
                if (wsEvent.commParams?.get("activityName") == topActivity::class.java.canonicalName) {
                    topActivity.onBackPressed()
                }

            }

            WSEType.ACTIVITY_FINISH -> {
                val topActivity = ActivityUtils.getTopActivity()
                if (wsEvent.commParams?.get("activityName") == topActivity::class.java.canonicalName) {
                    topActivity.finish()
                }
            }

            WSEType.WSE_ACCESS_EVENT -> {
                wsEvent.commParams?.let {
//                    LogHelper.json(
//                        TAG,
//                        "ServerActivityName===${it["activityName"]}    ClientActivityName===>${ActivityUtils.getTopActivity()::class.java.canonicalName}"
//                    )
                    if (it["activityName"] != ActivityUtils.getTopActivity()::class.java.canonicalName) {
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
                        targetView?.let { target ->
                            DoKitConstant.MC_INTERCEPT?.let { interceptor ->
                                if (wsEvent.isIntercept) {
                                    custom(interceptor, target, wsEvent.customParams)
                                } else {
                                    comm(viewC12c, target)
                                }
                            } ?: comm(viewC12c, target)


                        } ?: ToastUtils.showShort("匹配控件失败，请手动操作")

                    } ?: ToastUtils.showShort("无法确定当前控件所属窗口")

                }
                    ?: ToastUtils.showShort("无法获取手势控件信息")
            }
        }
    }


    /**
     * 自定义的处理方式
     */
    private fun custom(interceptor: MCInterceptor, target: View, param: Map<String, String>?) {
        param?.let {
            interceptor.clientProcess(target, it).isFalse {
                ToastUtils.showShort("该控件手势未被自定义拦截处理")
            }
        }
    }

    /**
     * 通用的处理方式
     */
    private fun comm(viewC12c: ViewC12c, target: View) {
        when (viewC12c.eventType) {
            //单击
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                if (target is Switch) {
                    target.toggle()
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
                } else {
                }
            }
            //EditText 光标变动
            AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> {
                if (target is EditText) {
                    target.setSelection(
                        viewC12c.accEventInfo?.fromIndex!!,
                        viewC12c.accEventInfo.toIndex!!
                    )
                } else {

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
                } else {

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
                    targetView.smoothScrollTo(accEventInfo.scrollX!!, accEventInfo.scrollY!!)
                }
            }

            is HorizontalScrollView -> {
                viewC12c.accEventInfo?.let { accEventInfo ->
                    targetView.smoothScrollTo(accEventInfo.scrollX!!, accEventInfo.scrollY!!)
                }
            }

            is RecyclerView -> {
                viewC12c.accEventInfo?.let { accEventInfo ->
                    if (accEventInfo.fromIndex!! == 0) {
                        targetView.smoothScrollToPosition(0)
                    } else if (accEventInfo.toIndex!! + 1 == targetView.adapter?.itemCount) {
                        targetView.smoothScrollToPosition(accEventInfo.toIndex)
                    } else {
                        moveToPosition(targetView, accEventInfo.fromIndex)
                    }

                    //targetView.smoothScrollToPosition(accEventInfo.fromIndex!!)
                }
            }

            is ListView -> {
                viewC12c.accEventInfo?.let { accEventInfo ->
                    if (accEventInfo.fromIndex!! == 0) {
                        targetView.smoothScrollToPosition(0)
                    } else if (accEventInfo.toIndex!! + 1 == targetView.adapter?.count) {
                        targetView.smoothScrollToPosition(accEventInfo.toIndex)
                    } else {
                        moveToPosition(targetView, accEventInfo.fromIndex)
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


