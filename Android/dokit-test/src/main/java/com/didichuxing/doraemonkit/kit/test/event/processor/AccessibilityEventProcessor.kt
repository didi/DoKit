package com.didichuxing.doraemonkit.kit.test.event.processor

import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.view.accessibility.AccessibilityEvent
import android.widget.*
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.didichuxing.doraemonkit.extension.isFalse
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.core.DokitFrameLayout
import com.didichuxing.doraemonkit.kit.test.event.WSClientProcessor
import com.didichuxing.doraemonkit.kit.test.event.ViewC12c
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.kit.test.util.McXposedHookUtils
import com.didichuxing.doraemonkit.kit.test.util.ViewPathUtil
import com.didichuxing.doraemonkit.kit.test.util.WindowPathUtil
import com.didichuxing.doraemonkit.util.*

object AccessibilityEventProcessor {


    fun onAccessibilityEvent(wsEvent: ControlEvent) {

        wsEvent.params?.let {
            if (it["activityName"] != ActivityUtils.getTopActivity()::class.tagName) {
//                DoKitMcManager.syncFailedListener.onActivityNotSync()
                ToastUtils.showShort("当前测试和主机不处于同一个页面，请手动调整同步")
                return
            }
        }

        wsEvent.viewC12c?.let { viewC12c ->
            if (McXposedHookUtils.ROOT_VIEWS == null || viewC12c.viewRootImplIndex == -1) {
                LogHelper.e(WSClientProcessor.TAG, "无法确定当前控件所属窗口/索引 wsEvent=$wsEvent")
                ToastUtils.showShort("无法确定当前控件所属窗口/索引")
                return
            }
            var viewRootImpl: ViewParent? = WindowPathUtil.findViewRoot(McXposedHookUtils.ROOT_VIEWS, viewC12c.viewRootImplIndex)

            viewRootImpl?.let {
                val decorView: ViewGroup =
                    ReflectUtils.reflect(it).field("mView").get<View>() as ViewGroup
                val targetView: View? =
                    ViewPathUtil.findViewByViewParentInfo(decorView, viewC12c.viewPaths)
                targetView?.let { target -> comm(viewC12c, target) }
                    ?: run {
//                        DoKitMcManager.syncFailedListener.onViewNotFound(wsEvent.viewC12c)
                        LogHelper.e(WSClientProcessor.TAG, "匹配控件失败，请手动操作 wsEvent=$wsEvent")
                        LogHelper.e(WSClientProcessor.TAG, "匹配控件失败，请手动操作 className=${viewRootImpl?.javaClass?.name}")
                        ToastUtils.showShort("匹配控件失败，请手动操作")
                    }
            } ?: run {
//                DoKitMcManager.syncFailedListener.onViewNotFound(wsEvent.viewC12c)
                LogHelper.e(WSClientProcessor.TAG, "无法确定当前控件所属窗口 wsEvent=$wsEvent")
                ToastUtils.showShort("无法确定当前控件所属窗口")
            }
        } ?: run {
            LogHelper.e(WSClientProcessor.TAG, "无法获取手势控件信息 wsEvent=$wsEvent")
            ToastUtils.showShort("无法获取手势控件信息")
        }
    }


    /**
     * 通用的处理方式
     */
    private fun comm(viewC12c: ViewC12c, target: View) {
        when (viewC12c.actionType) {
            //单击
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                if (target is Switch) {
                    target.toggle()
                } else if (target is CheckBox) {
                    target.isChecked = !target.isChecked
                } else if (target is RadioButton) {
                    target.isChecked = true
                } else {
                    if (target.hasOnClickListeners()) {
                        target.performClick().isFalse {
//                            DoKitMcManager.syncFailedListener.onViewPerformClickFailed(viewC12c, target)
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
//                            DoKitMcManager.syncFailedListener.onViewPerformLongClickFailed(viewC12c, target)
                            ToastUtils.showShort("模拟长按事件失败")
                        }
                    }
                }
            }
            // view 获取焦点
            AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
                target.requestFocus().isFalse {
//                    DoKitMcManager.syncFailedListener.onViewPerformFocusedFailed(viewC12c, target)
                    ToastUtils.showShort("获取焦点失败")
                }
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
