package com.didichuxing.doraemonkit.kit.core

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.IdRes

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-20-16:22
 * 描    述：dokit 页面浮标抽象类 一般的悬浮窗都需要继承该抽象接口
 * 修订历史：
 * ================================================
 */
abstract class AbsDokitView : DokitView, TouchProxy.OnTouchEventListener, DokitViewManager.DokitViewAttachedListener {
    override fun canDrag(): Boolean {
        return false
    }

    override fun onCreate(context: Context) {
    }

    override fun onCreateView(context: Context, rootView: FrameLayout): View {
        return TextView(context)
    }

    override fun onViewCreated(rootView: FrameLayout) {
    }

    override fun onResume() {
    }

    override fun onPause() {
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
    }

    override fun onEnterBackground() {
    }

    override fun onEnterForeground() {
    }

    override fun shouldDealBackKey(): Boolean {
        return false
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onDestroy() {
    }

    override fun onDokitViewAdd(dokitView: AbsDokitView?) {
    }

    override fun onMove(x: Int, y: Int, dx: Int, dy: Int) {
    }

    override fun onUp(x: Int, y: Int) {
    }

    override fun onDown(x: Int, y: Int) {
    }

    protected fun <T : View> findViewById(@IdRes id: Int): T {
        return Any() as T
    }
}