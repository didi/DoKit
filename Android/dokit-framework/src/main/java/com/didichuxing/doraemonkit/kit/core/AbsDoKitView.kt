package com.didichuxing.doraemonkit.kit.core

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.view.GravityCompat
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.DoKitEnv
import com.didichuxing.doraemonkit.config.FloatIconConfig
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.main.MainIconDoKitView
import com.didichuxing.doraemonkit.util.ActivityUtils
import com.didichuxing.doraemonkit.util.LogHelper
import com.didichuxing.doraemonkit.util.ScreenUtils
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.plus
import java.lang.ref.WeakReference

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-20-16:22
 * 描    述：dokit 页面浮标抽象类 一般的悬浮窗都需要继承该抽象接口
 * 修订历史：
 * ================================================
 */
abstract class AbsDoKitView : DoKitView, TouchProxy.OnTouchEventListener, DoKitViewManager.DokitViewAttachedListener {

    class ViewArgs {
        var mode: DoKitViewLaunchMode = DoKitViewLaunchMode.SINGLE_INSTANCE
        var normalMode = DoKitManager.IS_NORMAL_FLOAT_MODE
        var edgePinned = false
    }

    val doKitViewScope = MainScope() + CoroutineName(this.toString())

    val TAG = this.tagName

    /**
     * 页面启动模式
     */
    var mode: DoKitViewLaunchMode
        get() = viewProps.mode
        set(value) {
            viewProps.mode = value
        }

    val isNormalMode get() = viewProps.normalMode

    /**
     * 手势代理
     */
    @JvmField
    var mTouchProxy = TouchProxy(this)

    protected val viewProps = ViewArgs()

    @JvmField
    protected var mWindowManager = DoKitViewManager.INSTANCE.windowManager

    /**
     * 创建FrameLayout#LayoutParams 内置悬浮窗调用
     */
    var normalLayoutParams: FrameLayout.LayoutParams? = null
        private set

    /**
     * 创建FrameLayout#LayoutParams 系统悬浮窗调用
     */
    var systemLayoutParams: WindowManager.LayoutParams? = null
        private set

    private var mHandler: Handler? = Looper.myLooper()?.let { Handler(it) }

    private val mInnerReceiver = InnerReceiver()

    /**
     * 当前dokitViewName 用来当做map的key 和dokitViewIntent的tag一致
     */
    var tag = this.tagName
    var bundle: Bundle? = null

    /**
     * weakActivity attach activity
     */
    private var mAttachActivity: WeakReference<Activity>? = null

    val activity: Activity
        get() = if (mAttachActivity != null) {
            mAttachActivity!!.get()!!
        } else ActivityUtils.getTopActivity()

    fun setActivity(activity: Activity) {
        mAttachActivity = WeakReference(activity)
    }

    /**
     * 整个悬浮窗的View
     */
    private var mRootView: DoKitFrameLayout? = null

    val doKitView: View?
        get() = mRootView

    /**
     * rootView的直接子View 一般是用户的xml布局 被添加到mRootView中
     */
    private var mChildView: View? = null

    /**
     * 只控件在布局边界发生大小变化被裁剪的原因：
     * https://juejin.cn/post/6844903624452079623
     *
     */
    val parentView: DoKitFrameLayout?
        get() = if (isNormalMode && mRootView != null) {
            mRootView!!.parent as DoKitFrameLayout
        } else null

    /**
     * 用来保存rootview的LayoutParams
     */
    private lateinit var mDoKitViewLayoutParams: DoKitViewLayoutParams

    /**
     * 上一次DoKitview的位置信息
     */
    private val mLastDoKitViewPosInfo: LastDoKitViewPosInfo by lazy {
        if (DoKitViewManager.INSTANCE.getLastDokitViewPosInfo(tag) == null) {
            val posInfo = LastDoKitViewPosInfo()
            DoKitViewManager.INSTANCE.saveLastDokitViewPosInfo(tag, posInfo)
            posInfo
        } else {
            DoKitViewManager.INSTANCE.getLastDokitViewPosInfo(tag)!!
        }
    }

    /**
     * 根布局的实际宽
     */
    private var mDokitViewWidth = 0

    /**
     * 根布局的实际高
     */
    private var mDokitViewHeight = 0
    private var mViewTreeObserver: ViewTreeObserver? = null

    private val mOnGlobalLayoutListener: OnGlobalLayoutListener = OnGlobalLayoutListener {
        //每次布局发生变动的时候重新赋值
        mRootView?.let {
            mDokitViewWidth = it.measuredWidth
            mDokitViewHeight = it.measuredHeight
            mLastDoKitViewPosInfo.doKitViewWidth = mDokitViewWidth
            mLastDoKitViewPosInfo.doKitViewHeight = mDokitViewHeight
        }
    }

    /**
     * 执行floatPage create
     *
     * @param context 上下文环境
     */
    @SuppressLint("ClickableViewAccessibility")
    fun performCreate(context: Context) {
        try {
            //调用onCreate方法
            onCreate(context)
            if (!isNormalMode) {
                DoKitViewManager.INSTANCE.addDokitViewAttachedListener(this)
            }
            mRootView = if (isNormalMode) {
                DoKitFrameLayout(
                    context,
                    DoKitFrameLayout.DoKitFrameLayoutFlag_CHILD
                )
            } else {
                //系统悬浮窗的返回按键监听
                object : DoKitFrameLayout(context, DoKitFrameLayoutFlag_CHILD) {
                    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                        if (event.action == KeyEvent.ACTION_UP && shouldDealBackKey()) {
                            //监听返回键
                            if (event.keyCode == KeyEvent.KEYCODE_BACK || event.keyCode == KeyEvent.KEYCODE_HOME) {
                                return onBackPressed()
                            }
                        }
                        return super.dispatchKeyEvent(event)
                    }
                }
            }
            //添加根布局的layout回调
            addViewTreeObserverListener()

            //调用onCreateView抽象方法
            mChildView = onCreateView(context, mRootView)
            //将子View添加到rootview中
            mRootView?.addView(mChildView)
            mRootView?.title = this.javaClass.name
            //设置根布局的手势拦截
            mRootView?.setOnTouchListener { v, event -> mTouchProxy.onTouchEvent(v, event) }
            //调用onViewCreated回调
            onViewCreated(mRootView)
            mDoKitViewLayoutParams = DoKitViewLayoutParams()
            //分别创建对应的LayoutParams
            if (isNormalMode) {
                normalLayoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                    .apply {
                        gravity = GravityCompat.START or Gravity.TOP
                    }
                mDoKitViewLayoutParams.gravity = GravityCompat.START or Gravity.TOP
            } else {
                systemLayoutParams = WindowManager.LayoutParams()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //android 8.0
                    systemLayoutParams?.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    systemLayoutParams?.type = WindowManager.LayoutParams.TYPE_PHONE
                }
                //shouldDealBackKey : fasle 不自己收返回事件处理
                if (shouldDealBackKey()) {
                    //自己处理返回按键
                    systemLayoutParams?.flags =
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    mDoKitViewLayoutParams.flags =
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or DoKitViewLayoutParams.FLAG_LAYOUT_NO_LIMITS
                } else {
                    //参考：http://www.shirlman.com/tec/20160426/362
                    //设置WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE会导致RootView监听不到返回按键的监听失效 系统处理返回按键
                    systemLayoutParams?.flags =
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    mDoKitViewLayoutParams.flags =
                        DoKitViewLayoutParams.FLAG_NOT_FOCUSABLE or DoKitViewLayoutParams.FLAG_LAYOUT_NO_LIMITS
                }
                systemLayoutParams?.apply {
                    format = PixelFormat.TRANSPARENT
                    gravity = GravityCompat.START or Gravity.TOP
                }


                mDoKitViewLayoutParams.gravity = GravityCompat.START or Gravity.TOP
                //动态注册关闭系统弹窗的广播
                val intentFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                context.registerReceiver(mInnerReceiver, intentFilter)
            }
            initDokitViewLayoutParams(mDoKitViewLayoutParams)
            if (isNormalMode) {
                normalLayoutParams?.let {
                    onNormalLayoutParamsCreated()
                }
            } else {
                systemLayoutParams?.let {
                    onSystemLayoutParamsCreated()
                }
            }
        } catch (e: Exception) {
            LogHelper.e(TAG, "e===>" + e.message)
            e.printStackTrace()
        }
    }

    fun performDestroy() {
        if (!isNormalMode) {
            context?.unregisterReceiver(mInnerReceiver)
        }
        //移除布局监听
        removeViewTreeObserverListener()
        mHandler = null
        mRootView = null
        mAttachActivity = null
        onDestroy()
    }

    private fun addViewTreeObserverListener() {
        if (mViewTreeObserver == null && mRootView != null) {
            mViewTreeObserver = mRootView!!.viewTreeObserver
            mViewTreeObserver?.addOnGlobalLayoutListener(mOnGlobalLayoutListener)

        }
    }

    private fun removeViewTreeObserverListener() {
        mViewTreeObserver?.let {
            if (it.isAlive) {
                it.removeOnGlobalLayoutListener(mOnGlobalLayoutListener)
            }
        }
    }

    /**
     * 确定普通浮标的初始位置
     * LayoutParams创建完以后调用
     * 调用时建议放在实现下方
     *
     * @param params
     */
    private fun onNormalLayoutParamsCreated() {
        //如果有上一个页面的位置记录 这更新位置
        normalLayoutParams?.apply {
            width = mDoKitViewLayoutParams.width
            height = mDoKitViewLayoutParams.height
            gravity = mDoKitViewLayoutParams.gravity
        }
        val doKitViewInfo = DoKitViewManager.INSTANCE.getDoKitViewPos(tag)
        if (doKitViewInfo != null) {
            //竖向
            if (doKitViewInfo.orientation == Configuration.ORIENTATION_PORTRAIT) {
                normalLayoutParams?.apply {
                    leftMargin = doKitViewInfo.portraitPoint.x
                    topMargin = doKitViewInfo.portraitPoint.y
                }
            } else if (doKitViewInfo.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                normalLayoutParams?.apply {
                    leftMargin = doKitViewInfo.landscapePoint.x
                    topMargin = doKitViewInfo.landscapePoint.y
                }
            }
        } else {
            normalLayoutParams?.apply {
                leftMargin = mDoKitViewLayoutParams.x
                topMargin = mDoKitViewLayoutParams.y
            }
        }
        portraitOrLandscape()
    }

    /**
     * 用于普通模式下的横竖屏切换
     */
    private fun portraitOrLandscape() {
        DoKitViewManager.INSTANCE.getDoKitViewPos(tag)
            ?.also { doKitViewInfo ->
                //横竖屏切换兼容
                if (ScreenUtils.isPortrait()) {
                    if (mLastDoKitViewPosInfo.isPortrait) {
                        normalLayoutParams?.apply {
                            leftMargin = doKitViewInfo.portraitPoint.x
                            topMargin = doKitViewInfo.portraitPoint.y
                        }
                    } else {
                        normalLayoutParams?.apply {
                            leftMargin = (doKitViewInfo.landscapePoint.x * mLastDoKitViewPosInfo.leftMarginPercent).toInt()
                            topMargin = (doKitViewInfo.landscapePoint.y * mLastDoKitViewPosInfo.topMarginPercent).toInt()
                        }
                    }
                } else {
                    if (mLastDoKitViewPosInfo.isPortrait) {
                        normalLayoutParams?.apply {
                            leftMargin = (doKitViewInfo.portraitPoint.x * mLastDoKitViewPosInfo.leftMarginPercent).toInt()
                            topMargin = (doKitViewInfo.portraitPoint.y * mLastDoKitViewPosInfo.topMarginPercent).toInt()
                        }
                    } else {
                        normalLayoutParams?.apply {
                            leftMargin = doKitViewInfo.landscapePoint.x
                            topMargin = doKitViewInfo.landscapePoint.y
                        }
                    }
                }
            }
            ?: run {
                //横竖屏切换兼容
                if (ScreenUtils.isPortrait()) {
                    if (mLastDoKitViewPosInfo.isPortrait) {
                        normalLayoutParams?.apply {
                            leftMargin = mDoKitViewLayoutParams.x
                            topMargin = mDoKitViewLayoutParams.y
                        }
                    } else {
                        normalLayoutParams?.apply {
                            leftMargin = (mDoKitViewLayoutParams.x * mLastDoKitViewPosInfo.leftMarginPercent).toInt()
                            topMargin = (mDoKitViewLayoutParams.y * mLastDoKitViewPosInfo.topMarginPercent).toInt()
                        }
                    }
                } else {
                    if (mLastDoKitViewPosInfo.isPortrait) {
                        normalLayoutParams?.apply {
                            leftMargin = (mDoKitViewLayoutParams.x * mLastDoKitViewPosInfo.leftMarginPercent).toInt()
                            topMargin = (mDoKitViewLayoutParams.y * mLastDoKitViewPosInfo.topMarginPercent).toInt()
                        }
                    } else {
                        normalLayoutParams?.apply {
                            leftMargin = mDoKitViewLayoutParams.x
                            topMargin = mDoKitViewLayoutParams.y
                        }
                    }
                }
            }
        mLastDoKitViewPosInfo.setPortrait()
        normalLayoutParams?.also {
            mLastDoKitViewPosInfo.setLeftMargin(it.leftMargin)
            mLastDoKitViewPosInfo.setTopMargin(it.topMargin)
        }
        if (tag == MainIconDoKitView::class.tagName) {
            if (isNormalMode) {
                normalLayoutParams?.also {
                    FloatIconConfig.saveLastPosX(it.leftMargin)
                    FloatIconConfig.saveLastPosY(it.topMargin)
                }
            } else {
                systemLayoutParams?.also {
                    FloatIconConfig.saveLastPosX(it.x)
                    FloatIconConfig.saveLastPosY(it.y)
                }
            }
        }

        DoKitViewManager.INSTANCE.saveDokitViewPos(tag, normalLayoutParams?.leftMargin ?: 0, normalLayoutParams?.topMargin ?: 0)
    }

    /**
     * 确定系统浮标的初始位置
     * LayoutParams创建完以后调用
     * 调用时建议放在实现下方
     *
     * @param params
     */
    private fun onSystemLayoutParamsCreated() {
        //如果有上一个页面的位置记录 这更新位置
        systemLayoutParams?.flags = mDoKitViewLayoutParams.flags
        systemLayoutParams?.gravity = mDoKitViewLayoutParams.gravity
        systemLayoutParams?.width = mDoKitViewLayoutParams.width
        systemLayoutParams?.height = mDoKitViewLayoutParams.height
        val doKitViewInfo = DoKitViewManager.INSTANCE.getDoKitViewPos(
            tag
        )
        if (doKitViewInfo != null) {
            if (ScreenUtils.isPortrait()) {
                systemLayoutParams?.x = doKitViewInfo.portraitPoint.x
                systemLayoutParams?.y = doKitViewInfo.portraitPoint.y
            } else if (ScreenUtils.isLandscape()) {
                systemLayoutParams?.x = doKitViewInfo.landscapePoint.x
                systemLayoutParams?.y = doKitViewInfo.landscapePoint.y
            }
        } else {
            systemLayoutParams?.x = mDoKitViewLayoutParams.x
            systemLayoutParams?.y = mDoKitViewLayoutParams.y
        }
        systemLayoutParams?.let {
            DoKitViewManager.INSTANCE.saveDokitViewPos(tag, it.x, it.y)
        }
    }

    override fun onDestroy() {
        if (!isNormalMode) {
            DoKitViewManager.INSTANCE.removeDokitViewAttachedListener(this)
        }
        DoKitViewManager.INSTANCE.removeLastDokitViewPosInfo(tag)
        mAttachActivity = null
        doKitViewScope.cancel()
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
        mRootView?.let {
            if (!isNormalMode) {
                it.visibility = View.GONE
            }
        }
    }

    override fun onEnterForeground() {
        mRootView?.let {
            if (!isNormalMode) {
                it.visibility = View.VISIBLE
            }
        }
    }

    override fun onMove(x: Int, y: Int, dx: Int, dy: Int) {
        if (!canDrag()) {
            return
        }
        if (isNormalMode) {
            normalLayoutParams?.apply {
                this.leftMargin += dx
                this.topMargin += dy
            }

            //更新图标位置
            updateViewLayout(tag, false)
        } else {
            systemLayoutParams?.apply {
                this.x += dx
                this.y += dy
            }
            //限制布局边界
            resetBorderline(normalLayoutParams, systemLayoutParams)
            mWindowManager.updateViewLayout(mRootView, systemLayoutParams)
        }
    }

    /**
     * 手指弹起时保存当前浮标位置
     *
     * @param x
     * @param y
     */
    override fun onUp(x: Int, y: Int) {
        if (!canDrag()) {
            return
        }
        if (!viewProps.edgePinned) {
            endMoveAndRecord()
            return
        }
        animatedMoveToEdge()
    }

    /**
     * 手指按下时的操作
     *
     * @param x
     * @param y
     */
    override fun onDown(x: Int, y: Int) {
        if (!canDrag()) {
            return
        }
    }

    /**
     * 广播接收器 系统悬浮窗需要调用
     */
    private inner class InnerReceiver : BroadcastReceiver() {
        val SYSTEM_DIALOG_REASON_KEY = "reason"
        val SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps"
        val SYSTEM_DIALOG_REASON_HOME_KEY = "homekey"
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS == action) {
                val reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY)
                if (reason != null) {
                    if (reason == SYSTEM_DIALOG_REASON_HOME_KEY) {
                        //点击home键
                        onHomeKeyPress()
                    } else if (reason == SYSTEM_DIALOG_REASON_RECENT_APPS) {
                        //点击menu按钮
                        onRecentAppKeyPress()
                    }
                }
            }
        }
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
     * @param doKitView
     */
    override fun onDokitViewAdd(doKitView: AbsDoKitView?) {}

    override fun onResume() {
        mRootView?.requestLayout()
    }

    override fun onPause() {}

    /**
     * 系统悬浮窗需要调用
     *
     * @return
     */
    val context: Context?
        get() = if (mRootView != null) {
            mRootView!!.context
        } else {
            null
        }

    val resources: Resources?
        get() = if (context == null) {
            null
        } else context!!.resources

    fun getString(@StringRes resId: Int): String? {
        return if (context == null) {
            null
        } else context!!.getString(resId)
    }

    val isShow: Boolean
        get() = mRootView!!.isShown

    protected fun <T : View> findViewById(@IdRes id: Int): T? {
        if (mRootView == null) {
            return null
        }
        return mRootView?.findViewById(id)
    }

    /**
     * 将当前dokitView于activity解绑
     */
    fun detach() {
        DoKit.removeFloating(this)
    }

    /**
     * 操作DecorView的直接子布局
     * 测试专用
     */
    fun dealDecorRootView(decorRootView: FrameLayout?) {
        if (isNormalMode) {
            if (decorRootView == null) {
                return
            }
        } // FIXME: useless code, what that intention? @jtsky
    }

    /**
     * 更新view的位置
     *
     * @param isActivityBackResume 是否是从其他页面返回时更新的位置
     */
    open fun updateViewLayout(tag: String, isActivityBackResume: Boolean) {
        if (mRootView == null || mChildView == null || normalLayoutParams == null || !isNormalMode) {
            return
        }
        normalLayoutParams?.apply {
            if (isActivityBackResume) {
                if (tag == MainIconDoKitView::class.tagName) {
                    this.leftMargin = FloatIconConfig.getLastPosX()
                    this.topMargin = FloatIconConfig.getLastPosY()
                } else {
                    val doKitViewInfo = DoKitViewManager.INSTANCE.getDoKitViewPos(tag)
                    if (doKitViewInfo != null) {
                        if (doKitViewInfo.orientation == Configuration.ORIENTATION_PORTRAIT) {
                            this.leftMargin = doKitViewInfo.portraitPoint.x
                            this.topMargin = doKitViewInfo.portraitPoint.y
                        } else {
                            this.leftMargin = doKitViewInfo.landscapePoint.x
                            this.topMargin = doKitViewInfo.landscapePoint.y
                        }
                    }
                }
            } else {
                //非页面切换的时候保存当前位置信息
                mLastDoKitViewPosInfo.setPortrait()
                mLastDoKitViewPosInfo.setLeftMargin(this.leftMargin)
                mLastDoKitViewPosInfo.setTopMargin(this.topMargin)
            }
            if (tag == MainIconDoKitView::class.tagName) {
                this.width = DoKitViewLayoutParams.WRAP_CONTENT
                this.height = DoKitViewLayoutParams.WRAP_CONTENT
                //            mFrameLayoutParams.width = ConvertUtils.dp2px(MainIconDokitView.FLOAT_SIZE);
//            mFrameLayoutParams.height = ConvertUtils.dp2px(MainIconDokitView.FLOAT_SIZE);
            } else {
                if (mDokitViewWidth != 0) {
                    this.width = mDokitViewWidth
                }
                if (mDokitViewHeight != 0) {
                    this.height = mDokitViewHeight
                }
            }

            resetBorderline(this, systemLayoutParams)
            //更新根布局的位置
            mRootView?.layoutParams = this
        }
    }

    /**
     * 限制边界 调用的时候必须保证是在控件能获取到宽高德前提下
     */
    private fun resetBorderline(
        normalFrameLayoutParams: FrameLayout.LayoutParams?,
        windowLayoutParams: WindowManager.LayoutParams?
    ) {
        //如果是系统模式或者手动关闭动态限制边界
        if (!restrictBorderline()) {
            return
        }

        //普通模式
        if (isNormalMode) {
            if (normalFrameLayoutParams != null) {
                if (ScreenUtils.isPortrait()) {
                    if (normalFrameLayoutParams.topMargin >= screenLongSideLength - mDokitViewHeight) {
                        normalFrameLayoutParams.topMargin = screenLongSideLength - mDokitViewHeight
                    }
                } else {
                    if (normalFrameLayoutParams.topMargin >= screenShortSideLength - mDokitViewHeight) {
                        normalFrameLayoutParams.topMargin = screenShortSideLength - mDokitViewHeight
                    }
                }

                if (ScreenUtils.isPortrait()) {
                    if (normalFrameLayoutParams.leftMargin >= screenShortSideLength - mDokitViewWidth) {
                        normalFrameLayoutParams.leftMargin = screenShortSideLength - mDokitViewWidth
                    }
                } else {
                    if (normalFrameLayoutParams.leftMargin >= screenLongSideLength - mDokitViewWidth) {
                        normalFrameLayoutParams.leftMargin = screenLongSideLength - mDokitViewWidth
                    }
                }

                if (normalFrameLayoutParams.topMargin <= 0) {
                    normalFrameLayoutParams.topMargin = 0
                }

                if (normalFrameLayoutParams.leftMargin <= 0) {
                    normalFrameLayoutParams.leftMargin = 0
                }
            }

        } else {
            if (windowLayoutParams != null) {
                if (ScreenUtils.isPortrait()) {
                    if (windowLayoutParams.y >= screenLongSideLength - mDokitViewHeight) {
                        windowLayoutParams.y = screenLongSideLength - mDokitViewHeight
                    }
                } else {
                    if (windowLayoutParams.y >= screenShortSideLength - mDokitViewHeight) {
                        windowLayoutParams.y = screenShortSideLength - mDokitViewHeight
                    }
                }

                if (ScreenUtils.isPortrait()) {
                    if (windowLayoutParams.x >= screenShortSideLength - mDokitViewWidth) {
                        windowLayoutParams.x = screenShortSideLength - mDokitViewWidth
                    }
                } else {
                    if (windowLayoutParams.x >= screenLongSideLength - mDokitViewWidth) {
                        windowLayoutParams.x = screenLongSideLength - mDokitViewWidth
                    }
                }

                //系统模式
                if (windowLayoutParams.y <= 0) {
                    windowLayoutParams.y = 0
                }

                if (windowLayoutParams.x <= 0) {
                    windowLayoutParams.x = 0
                }
            }

        }
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
        mHandler?.post(run)
    }

    fun postDelayed(run: Runnable, delayMillis: Long) {
        mHandler?.postDelayed(run, delayMillis)
    }

    /**
     * 设置当前 kitView 不响应触摸事件
     * 控件默认响应触摸事件
     * 需要在子 view 的 onViewCreated 中调用
     */
    fun setDoKitViewNotResponseTouchEvent(view: View?) {
        if (isNormalMode) {
            view?.setOnTouchListener { _, _ -> false }
        } else {
            view?.setOnTouchListener(null)
        }
    }

    /**
     * 获取屏幕短边的长度 不包含statusBar
     *
     * @return
     */
    val screenShortSideLength: Int
        get() = if (ScreenUtils.isPortrait()) {
            ScreenUtils.getAppScreenWidth()
        } else {
            ScreenUtils.getAppScreenHeight()
        }//ScreenUtils.getScreenHeight(); 包含statusBar
    //ScreenUtils.getAppScreenHeight(); 不包含statusBar
    /**
     * 获取屏幕长边的长度 不包含statusBar
     *
     * @return
     */
    val screenLongSideLength: Int
        get() = if (ScreenUtils.isPortrait()) {
            //ScreenUtils.getScreenHeight(); 包含statusBar
            //ScreenUtils.getAppScreenHeight(); 不包含statusBar
            ScreenUtils.getAppScreenHeight()
        } else {
            ScreenUtils.getAppScreenWidth()
        }

    /**
     * 强制刷新当前dokitview
     */
    open fun immInvalidate() {
        mRootView?.requestLayout()
    }

    private fun animatedMoveToEdge() {
        val viewSize = mRootView?.width ?: return
        if (isNormalMode) {
            val parent = (mRootView?.parent as? ViewGroup) ?: return
            normalLayoutParams?.also { layoutAttrs ->
                makeAnimator(layoutAttrs.leftMargin, viewSize, parent.width) {
                    addUpdateListener { v ->
                        layoutAttrs.leftMargin = v.animatedValue as Int
                        updateViewLayout(tag, false)
                    }
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            endMoveAndRecord()
                        }
                    })
                }
            }
            return
        }
        systemLayoutParams?.also { layoutAttrs ->
            makeAnimator(layoutAttrs.x, viewSize, DoKitEnv.windowSize.x) {
                addUpdateListener { v ->
                    layoutAttrs.x = v.animatedValue as Int
                    mWindowManager.updateViewLayout(mRootView, layoutAttrs)
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        endMoveAndRecord()
                    }
                })
            }
        }
    }

    private fun endMoveAndRecord() {
        if (tag == MainIconDoKitView::class.tagName) {
            if (isNormalMode) {
                normalLayoutParams?.also {
                    FloatIconConfig.saveLastPosX(it.leftMargin)
                    FloatIconConfig.saveLastPosY(it.topMargin)
                }
            } else {
                systemLayoutParams?.also {
                    FloatIconConfig.saveLastPosX(it.x)
                    FloatIconConfig.saveLastPosY(it.y)
                }
            }
        }
        // 保存在内存中
        if (isNormalMode) {
            normalLayoutParams?.also { DoKitViewManager.INSTANCE.saveDokitViewPos(tag, it.leftMargin, it.topMargin) }
        } else {
            systemLayoutParams?.also { DoKitViewManager.INSTANCE.saveDokitViewPos(tag, it.x, it.y) }
        }
    }

    private inline fun makeAnimator(from: Int, size: Int, containerSize: Int, setup: ValueAnimator.() -> Unit) {
        if (size <= 0 || containerSize <= 0) return
        ValueAnimator.ofInt(from, if (from <= (containerSize - size) / 2) 0 else (containerSize - size))
            .apply {
                duration = 150L
                setup()
            }
            .start()
    }
}
