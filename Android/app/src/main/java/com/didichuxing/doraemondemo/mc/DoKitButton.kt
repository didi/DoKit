package com.didichuxing.doraemondemo.mc

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatButton

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/7-19:27
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DoKitButton : AppCompatButton {
    companion object {
        const val TAG = "DoKitButton"
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


//    private val POPULATING_ACCESSIBILITY_EVENT_TYPES = (AccessibilityEvent.TYPE_VIEW_CLICKED
//            or AccessibilityEvent.TYPE_VIEW_LONG_CLICKED
//            or AccessibilityEvent.TYPE_VIEW_SELECTED
//            or AccessibilityEvent.TYPE_VIEW_FOCUSED
//            or AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
//            or AccessibilityEvent.TYPE_VIEW_HOVER_ENTER
//            or AccessibilityEvent.TYPE_VIEW_HOVER_EXIT
//            or AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED
//            or AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED
//            or AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED
//            or AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY)
//
//
//    override fun sendAccessibilityEvent(eventType: Int) {
//        super.sendAccessibilityEvent(eventType)
//    }
//
//    override fun sendAccessibilityEventUnchecked(event: AccessibilityEvent?) {
//
//
//        // Do not send scroll events since first they are not interesting for
//        // accessibility and second such events a generated too frequently.
//        // For details see the implementation of bringTextIntoView().
//        if (event!!.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
//            return
//        }
//
//        if (!isShown) {
//            return
//        }
//        onInitializeAccessibilityEvent(event)
//        LogHelper.i(TAG, "event1===>$event")
//        // Only a subset of accessibility events populates text content.
//        // Only a subset of accessibility events populates text content.
//        if (event!!.eventType and POPULATING_ACCESSIBILITY_EVENT_TYPES != 0) {
//            dispatchPopulateAccessibilityEvent(event)
//        }
//        LogHelper.i(TAG, "event2===>$event")
//
//        // Android 9.0以下的系统会在requestSendAccessibilityEvent 方法中对于event做一定的处理 导致event 中的有些字段会被置空
//        this.parent?.requestSendAccessibilityEvent(this, event)
//        LogHelper.i(TAG, "event3===>$event")
//    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)

    }
}