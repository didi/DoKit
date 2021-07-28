package com.didichuxing.doraemonkit.kit.core

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.annotation.StringRes

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-20-16:22
 * 描    述：dokit 页面浮标抽象类 一般的悬浮窗都需要继承该抽象接口
 * 修订历史：
 * ================================================
 */
abstract class AbsDokitView : DokitView, TouchProxy.OnTouchEventListener,
    DokitViewManager.DokitViewAttachedListener {


    val TAG = ""

    /**
     * 页面启动模式
     */
    var mode: DoKitViewLaunchMode = DoKitViewLaunchMode.SINGLE_INSTANCE

    val isNormalMode = false



    @JvmField
    protected var mWindowManager = null

    /**
     * 创建FrameLayout#LayoutParams 内置悬浮窗调用
     */
    var normalLayoutParams: FrameLayout.LayoutParams? = null

    /**
     * 创建FrameLayout#LayoutParams 系统悬浮窗调用
     */
    var systemLayoutParams: WindowManager.LayoutParams? = null



    /**
     * 当前dokitViewName 用来当做map的key 和dokitViewIntent的tag一致
     */
    var tag = ""
    var bundle: Bundle? = null

    /**
     * weakActivity attach activity
     */

    val activity: Activity?
        get() = null


    fun setActivity(activity: Activity) {
    }



    val doKitView: View?
        get() = null



    /**
     * 只控件在布局边界发生大小变化被裁剪的原因：
     * https://juejin.cn/post/6844903624452079623
     *
     */
    val parentView: DokitFrameLayout?
        get() = null





    override fun onDestroy() {

    }

    /**
     * 默认实现为true
     *
     * @return
     */
    override fun canDrag(): Boolean {
        return true
    }

    /**
     * 搭配shouldDealBackKey使用 自定义处理完以后需要返回true
     * 默认模式的onBackPressed 拦截在NormalDokitViewManager#getDokitRootContentView中被处理
     * 系统模式下的onBackPressed 在当前类的performCreate 初始话DoKitView时被处理
     * 返回false 表示交由系统处理
     * 返回 true 表示当前的返回事件已由自己处理 并拦截了改返回事件
     */
    override fun onBackPressed(): Boolean {
        return false
    }

    /**
     * 默认不自己处理返回按键
     *
     * @return
     */
    override fun shouldDealBackKey(): Boolean {
        return false
    }

    override fun onEnterBackground() {


    }

    override fun onEnterForeground() {


    }

    override fun onMove(x: Int, y: Int, dx: Int, dy: Int) {

    }

    /**
     * 手指弹起时保存当前浮标位置
     *
     * @param x
     * @param y
     */
    override fun onUp(x: Int, y: Int) {

    }

    /**
     * 手指按下时的操作
     *
     * @param x
     * @param y
     */
    override fun onDown(x: Int, y: Int) {

    }


    /**
     * home键被点击 只有系统悬浮窗控件才会被调用
     */
    open fun onHomeKeyPress() {}

    /**
     * 菜单键被点击 只有系统悬浮窗控件才会被调用
     */
    open fun onRecentAppKeyPress() {}

    /**
     * 不能在改方法中进行dokitview的添加和删除 因为处于遍历过程在
     * 只有系统模式下才会调用
     *
     * @param dokitView
     */
    override fun onDokitViewAdd(dokitView: AbsDokitView?) {}

    override fun onResume() {
    }

    override fun onPause() {}

    /**
     * 系统悬浮窗需要调用
     *
     * @return
     */
    val context: Context?
        get() = null

    val resources: Resources?
        get() = null

    fun getString(@StringRes resId: Int): String? {
        return null
    }

    val isShow: Boolean
        get() = false

    protected fun <T : View> findViewById(@IdRes id: Int): T? {
        return null
    }


    /**
     * 将当前dokitView于activity解绑
     */
    fun detach() {
    }

    /**
     * 操作DecorView的直接子布局
     * 测试专用
     */
    fun dealDecorRootView(decorRootView: FrameLayout?) {

    }

    /**
     * 更新view的位置
     *
     * @param isActivityBackResume 是否是从其他页面返回时更新的位置
     */
    open fun updateViewLayout(tag: String, isActivityBackResume: Boolean) {

    }


    /**
     * 是否限制布局边界
     *
     * @return
     */
    open fun restrictBorderline(): Boolean {
        return true
    }


    fun post(run: Runnable) {
    }

    fun postDelayed(run: Runnable, delayMillis: Long) {
    }


    /**
     * 获取屏幕短边的长度 不包含statusBar
     *
     * @return
     */
    val screenShortSideLength: Int
        get() = -1
    //ScreenUtils.getAppScreenHeight(); 不包含statusBar
    /**
     * 获取屏幕长边的长度 不包含statusBar
     *
     * @return
     */
    val screenLongSideLength: Int
        get() = -1

    /**
     * 强制刷新当前dokitview
     */
    open fun immInvalidate() {
    }
}