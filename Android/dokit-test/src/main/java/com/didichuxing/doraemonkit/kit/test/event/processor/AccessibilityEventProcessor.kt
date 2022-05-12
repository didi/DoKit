package com.didichuxing.doraemonkit.kit.test.event.processor

import android.app.Activity
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.widget.*
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.didichuxing.doraemonkit.extension.isFalse
import com.didichuxing.doraemonkit.kit.core.DoKitFrameLayout
import com.didichuxing.doraemonkit.kit.test.event.ViewC12c
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.kit.test.event.EventErrorCode
import com.didichuxing.doraemonkit.util.*

/**
 * didi Create on 2022/4/13 .
 *
 * Copyright (c) 2022/4/13 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/13 3:07 下午
 * @Description 用一句话说明文件功能
 */
class AccessibilityEventProcessor : AbstractEventProcessor() {


    override fun onSimulationEventAction(activity: Activity, view: View?, viewC12c: ViewC12c, controlEvent: ControlEvent) {
        if (view != null){
            dispatchSimulationEventAction(activity, view, viewC12c, controlEvent)
        }
    }

    /**
     * 通用的处理方式
     */
    private fun dispatchSimulationEventAction(activity: Activity, targetView: View, viewC12c: ViewC12c, controlEvent: ControlEvent) {
        when (viewC12c.accEventType) {
            //单击
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                onSimulationClickEvent(activity, targetView, viewC12c, controlEvent)
            }
            //长按
            AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> {
                onSimulationLongClickEvent(activity, targetView, viewC12c, controlEvent)
            }
            // view 获取焦点
            AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
                onSimulationViewFocusEvent(activity, targetView, viewC12c, controlEvent)
            }
            //EditText 文字改变
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                onSimulationTextChangeEvent(activity, targetView, viewC12c, controlEvent)
            }
            //EditText 光标变动
            AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> {
                onSimulationTextSelectionChangedEvent(activity, targetView, viewC12c, controlEvent)
            }
            //滚动
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                onSimulationViewScrollEvent(activity, targetView, viewC12c, controlEvent)
            }
            //处理dokit view的拖动
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                onSimulationViewMoveEvent(activity, targetView, viewC12c, controlEvent)
            }
            else -> {
                onControlEventProcessFailed(
                    activity = activity,
                    view = targetView,
                    controlEvent = controlEvent,
                    code = EventErrorCode.ACTION_IGNORE,
                    message = "动作被忽略"
                )
                return
            }
        }

        onControlEventProcessSuccess(activity, targetView, controlEvent)
    }

    private fun onSimulationClickEvent(activity: Activity, targetView: View, viewC12c: ViewC12c, controlEvent: ControlEvent) {
        if (targetView is Switch) {
            targetView.toggle()
        } else if (targetView is CheckBox) {
            targetView.isChecked = !targetView.isChecked
        } else if (targetView is RadioButton) {
            targetView.isChecked = true
        } else {
            if (targetView.hasOnClickListeners()) {
                targetView.performClick().isFalse {
                    ToastUtils.showShort("模拟点击事件失败")
                }
            } else {
                //兼容adapter
                if (targetView.parent is AdapterView<*>) {
                    (targetView.parent as AdapterView<*>).performItemClick(
                        targetView,
                        viewC12c.viewPaths?.get(1)!!.currentEventPosition,
                        targetView.id.toLong()
                    )
                } else {
                    ToastUtils.showShort("该控件没有设置点击事件")
                }
            }
        }
    }

    private fun onSimulationLongClickEvent(activity: Activity, targetView: View, viewC12c: ViewC12c, controlEvent: ControlEvent) {
        when (targetView) {
            is EditText -> {
                targetView.selectAll()
            }
            else -> {
                targetView.performLongClick().isFalse {
                    ToastUtils.showShort("模拟长按事件失败")
                }
            }
        }
    }

    private fun onSimulationViewFocusEvent(activity: Activity, targetView: View, viewC12c: ViewC12c, controlEvent: ControlEvent) {
        targetView.requestFocus().isFalse {
            ToastUtils.showShort("获取焦点失败")
        }
    }

    private fun onSimulationTextChangeEvent(activity: Activity, targetView: View, viewC12c: ViewC12c, controlEvent: ControlEvent) {
        if (targetView is TextView) {
            targetView.text = viewC12c.text
        }
    }

    private fun onSimulationTextSelectionChangedEvent(activity: Activity, targetView: View, viewC12c: ViewC12c, controlEvent: ControlEvent) {
        if (targetView is EditText) {
            targetView.setSelection(
                viewC12c.accEventInfo?.fromIndex!!,
                viewC12c.accEventInfo.toIndex!!
            )
        }
    }


    private fun onSimulationViewScrollEvent(activity: Activity, targetView: View, viewC12c: ViewC12c, controlEvent: ControlEvent) {
        simulationScrollEvent(activity, targetView, viewC12c, controlEvent)
    }

    private fun onSimulationViewMoveEvent(activity: Activity, targetView: View, viewC12c: ViewC12c, controlEvent: ControlEvent) {
        if (targetView is DoKitFrameLayout && targetView.layoutParams is FrameLayout.LayoutParams) {
            val layoutParams: FrameLayout.LayoutParams = targetView.layoutParams as FrameLayout.LayoutParams

            layoutParams.leftMargin = viewC12c.doKitViewNode?.leftMargin!!
            layoutParams.topMargin = viewC12c.doKitViewNode.topMargin
            targetView.layoutParams = layoutParams
        }
    }

    /**
     * 处理滑动事件
     */
    private fun simulationScrollEvent(activity: Activity, targetView: View, viewC12c: ViewC12c, controlEvent: ControlEvent) {
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
                }
            }

            is ViewPager -> {
                viewC12c.accEventInfo?.let {
                    targetView.setCurrentItem(it.toIndex!!, true)
                }
            }

            else -> {
                onControlEventProcessFailed(
                    activity = activity,
                    view = targetView,
                    controlEvent = controlEvent,
                    code = EventErrorCode.ACTION_IGNORE,
                    message = "动作被忽略"
                )
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
