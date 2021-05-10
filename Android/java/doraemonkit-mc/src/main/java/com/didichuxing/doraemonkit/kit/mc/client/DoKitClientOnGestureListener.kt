package com.didichuxing.doraemonkit.kit.mc.client

import android.view.GestureDetector
import android.view.MotionEvent
import com.didichuxing.doraemonkit.util.LogHelper

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/26-16:29
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DoKitClientOnGestureListener : GestureDetector.SimpleOnGestureListener() {
    companion object {
        const val TAG = "DoKitOnGestureListener"
    }

    override fun onDown(e: MotionEvent?): Boolean {
        //scrollView.dispatchTouchEvent(e)
        return super.onDown(e)
    }

    override fun onShowPress(e: MotionEvent?) {
        LogHelper.i(TAG, "===onShowPress===")
        super.onShowPress(e)
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        LogHelper.i(TAG, "===onSingleTapUp===")
        return super.onSingleTapUp(e)
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        //LogHelper.i(TAG, "===onScroll===")
        return super.onScroll(e1, e2, distanceX, distanceY)
    }

    override fun onLongPress(e: MotionEvent?) {
        LogHelper.i(TAG, "===onLongPress===")
        super.onLongPress(e)
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {

        //LogHelper.i(TAG, "===onFling===")
        return super.onFling(e1, e2, velocityX, velocityY)
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        LogHelper.i(TAG, "===onSingleTapConfirmed===")
        return super.onSingleTapConfirmed(e)
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        LogHelper.i(TAG, "===onDoubleTap===")
        return super.onDoubleTap(e)
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        LogHelper.i(TAG, "===onDoubleTapEvent===")
        return super.onDoubleTapEvent(e)
    }

    override fun onContextClick(e: MotionEvent?): Boolean {
        LogHelper.i(TAG, "===onContextClick===")
        return super.onContextClick(e)
    }
}