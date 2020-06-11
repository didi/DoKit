package com.didichuxing.doraemonkit.kit.core

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ScreenUtils
import com.didichuxing.doraemonkit.config.FloatIconConfig
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.kit.main.MainIconDokitView
import com.didichuxing.doraemonkit.util.LogHelper
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
abstract class AbsDokitView : DokitView, TouchProxy.OnTouchEventListener, DokitViewManager.DokitViewAttachedListener {
    private val TAG = this.javaClass.simpleName

    /**
     * 当前dokitViewName 用来当做map的key 和dokitViewIntent的tag一致
     */
    var tag = this.javaClass.simpleName

    /**
     * 手势代理
     */
    val mTouchProxy = TouchProxy(this)

    protected val mWindowManager = DokitViewManager.instance.windowManager

    /**
     * 创建FrameLayout#LayoutParams 内置悬浮窗调用
     */
    lateinit var normalLayoutParams: FrameLayout.LayoutParams

    /**
     * 创建FrameLayout#LayoutParams 系统悬浮窗调用
     */
    lateinit var systemLayoutParams: WindowManager.LayoutParams

    private var mHandler: Handler? = Handler(Looper.myLooper()!!)

    private val mInnerReceiver = InnerReceiver()


    var bundle: Bundle? = null

    /**
     * weakActivity attach activity
     */
    var attachActivity: WeakReference<Activity>? = null

    val activity: Activity
        get() {
            return if (attachActivity != null) {
                attachActivity?.get()!!
            } else {
                ActivityUtils.getTopActivity()
            }
        }

    /**
     * DokitView的根布局
     */
    lateinit var rootView: FrameLayout

    /**
     * rootView的直接子View 一般是用户的xml布局 被添加到mRootView中
     */
    private lateinit var mChildView: View

    private lateinit var mDoKitViewLayoutParams: DokitViewLayoutParams

    /**
     * 上一次DoKitView的位置信息
     */
    private lateinit var mLastDokitViewPosInfo: LastDokitViewPosInfo

    /**
     * 根布局的实际宽
     */
    private var mDokitViewWidth: Int = 0

    /**
     * 根布局的实际高
     */
    private var mDokitViewHeight: Int = 0

    //private var mViewTreeObserver: ViewTreeObserver? = null


    /**
     * 对象初始化时调用
     */
    init {
        /**
         * also 会返回对象本身 用来代替传统if else 判断比较好 let由于会返回最后一行最为返回值 所以会有坑
         */
        DokitViewManager.instance.getLastDokitViewPosInfo(tag)?.also {
            //非空
            mLastDokitViewPosInfo = it
        } ?: also {
            it.mLastDokitViewPosInfo = LastDokitViewPosInfo()
            DokitViewManager.instance.saveLastDokitViewPosInfo(tag, it.mLastDokitViewPosInfo)
        }
    }


    private val mOnGlobalLayoutListener: OnGlobalLayoutListener = OnGlobalLayoutListener {
        this@AbsDokitView.also { dokitView ->
            dokitView.rootView.let { rootView ->
                dokitView.mDokitViewWidth = rootView.measuredWidth
                dokitView.mDokitViewHeight = rootView.measuredHeight
                dokitView.mLastDokitViewPosInfo.apply {
                    dokitViewWidth = dokitView.mDokitViewWidth
                    dokitViewHeight = dokitView.mDokitViewHeight
                }
            }
        }
    }

    /**
     * 页面启动模式
     */
    var mode = 0

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
                DokitViewManager.instance.addDokitViewAttachedListener(this)
            }
            this.rootView = if (isNormalMode) {
                DokitFrameLayout(context)
            } else {
                //系统悬浮窗的返回按键监听
                object : DokitFrameLayout(context) {
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
            mChildView = onCreateView(context, this.rootView)
            //将子View添加到rootview中
            this.rootView.addView(mChildView)
            //设置根布局的手势拦截
            this.rootView.setOnTouchListener { v, event ->
                mTouchProxy.onTouchEvent(v, event)
            }
            //调用onViewCreated回调
            onViewCreated(this.rootView)

            mDoKitViewLayoutParams = DokitViewLayoutParams()
            //分别创建对应的LayoutParams
            if (isNormalMode) {
                normalLayoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                normalLayoutParams.gravity = Gravity.LEFT or Gravity.TOP
                mDoKitViewLayoutParams.gravity = Gravity.LEFT or Gravity.TOP
            } else {
                systemLayoutParams = WindowManager.LayoutParams()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //android 8.0
                    systemLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    systemLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE
                }
                if (!shouldDealBackKey()) {
                    //参考：http://www.shirlman.com/tec/20160426/362
                    //设置WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE会导致rootview监听不到返回按键的监听失效
                    systemLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    mDoKitViewLayoutParams.flags = DokitViewLayoutParams.FLAG_NOT_FOCUSABLE
                }
                systemLayoutParams.format = PixelFormat.TRANSPARENT
                systemLayoutParams.gravity = Gravity.LEFT or Gravity.TOP
                mDoKitViewLayoutParams.gravity = Gravity.LEFT or Gravity.TOP
                //动态注册关闭系统弹窗的广播
                val intentFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                context.registerReceiver(mInnerReceiver, intentFilter)
            }
            initDokitViewLayoutParams(mDoKitViewLayoutParams)
            if (isNormalMode) {
                onNormalLayoutParamsCreated(normalLayoutParams)
            } else {
                onSystemLayoutParamsCreated(systemLayoutParams)
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
        onDestroy()
    }

    private fun addViewTreeObserverListener() {
        rootView.viewTreeObserver?.addOnGlobalLayoutListener(mOnGlobalLayoutListener)
    }

    private fun removeViewTreeObserverListener() {
        rootView.viewTreeObserver?.let {
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
    private fun onNormalLayoutParamsCreated(params: FrameLayout.LayoutParams?) {
        //如果有上一个页面的位置记录 这更新位置
        params?.let {
            it.width = mDoKitViewLayoutParams.width
            it.height = mDoKitViewLayoutParams.height
            it.gravity = mDoKitViewLayoutParams.gravity
            portraitOrLandscape(it)
        }

    }

    /**
     * 用于普通模式下的横竖屏切换
     */
    private fun portraitOrLandscape(params: FrameLayout.LayoutParams?) {
        params?.let {
            val point = DokitViewManager.instance.getDokitViewPos(tag)
            if (point != null) {
                //横竖屏切换兼容
                if (ScreenUtils.isPortrait()) {
                    if (mLastDokitViewPosInfo.isPortrait) {
                        it.leftMargin = point.x
                        it.topMargin = point.y
                    } else {
                        it.leftMargin = (point.x * mLastDokitViewPosInfo.leftMarginPercent).toInt()
                        it.topMargin = (point.y * mLastDokitViewPosInfo.topMarginPercent).toInt()
                    }
                } else {
                    if (mLastDokitViewPosInfo.isPortrait) {
                        it.leftMargin = (point.x * mLastDokitViewPosInfo.leftMarginPercent).toInt()
                        it.topMargin = (point.y * mLastDokitViewPosInfo.topMarginPercent).toInt()
                    } else {
                        it.leftMargin = point.x
                        it.topMargin = point.y
                    }
                }
            } else {
                //横竖屏切换兼容
                if (ScreenUtils.isPortrait()) {
                    if (mLastDokitViewPosInfo.isPortrait) {
                        it.leftMargin = mDoKitViewLayoutParams.x
                        it.topMargin = mDoKitViewLayoutParams.y
                    } else {
                        it.leftMargin = (mDoKitViewLayoutParams.x * mLastDokitViewPosInfo.leftMarginPercent).toInt()
                        it.topMargin = (mDoKitViewLayoutParams.y * mLastDokitViewPosInfo.topMarginPercent).toInt()
                    }
                } else {
                    if (mLastDokitViewPosInfo.isPortrait) {
                        it.leftMargin = (mDoKitViewLayoutParams.x * mLastDokitViewPosInfo.leftMarginPercent).toInt()
                        it.topMargin = (mDoKitViewLayoutParams.y * mLastDokitViewPosInfo.topMarginPercent).toInt()
                    } else {
                        it.leftMargin = mDoKitViewLayoutParams.x
                        it.topMargin = mDoKitViewLayoutParams.y
                    }
                }
            }
            mLastDokitViewPosInfo.setLeftMargin(it.leftMargin)
            mLastDokitViewPosInfo.setTopMargin(it.topMargin)
        }


    }

    /**
     * 确定系统浮标的初始位置
     * LayoutParams创建完以后调用
     * 调用时建议放在实现下方
     *
     * @param params
     */
    private fun onSystemLayoutParamsCreated(params: WindowManager.LayoutParams?) {
        //如果有上一个页面的位置记录 这更新位置
        params?.apply {
            flags = mDoKitViewLayoutParams.flags
            gravity = mDoKitViewLayoutParams.gravity
            width = mDoKitViewLayoutParams.width
            height = mDoKitViewLayoutParams.height
            val point = DokitViewManager.instance.getDokitViewPos(tag)
            if (point != null) {
                x = point.x
                y = point.y
            } else {
                x = mDoKitViewLayoutParams.x
                y = mDoKitViewLayoutParams.y
            }
        }
    }

    override fun onDestroy() {
        if (!isNormalMode) {
            DokitViewManager.instance.removeDokitViewAttachedListener(this)
        }
        DokitViewManager.instance.removeLastDokitViewPosInfo(tag)
        attachActivity = null
        mTouchProxy.removeListener()
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
     * 搭配shouldDealBackKey使用
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
        if (!isNormalMode) {
            this.rootView.visibility = View.GONE
        }
    }

    override fun onEnterForeground() {
        if (!isNormalMode) {
            this.rootView.visibility = View.VISIBLE
        }
    }

    override fun onMove(x: Int, y: Int, dx: Int, dy: Int) {
        if (!canDrag()) {
            return
        }
        if (isNormalMode) {
            normalLayoutParams.leftMargin += dx
            normalLayoutParams.topMargin += dy
            //更新图标位置
            updateViewLayout(tag, false)
        } else {
            systemLayoutParams.x += dx
            systemLayoutParams.y += dy
            mWindowManager.updateViewLayout(this.rootView, systemLayoutParams)
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
        if (tag == MainIconDokitView::class.java.simpleName) {
            if (isNormalMode) {
                FloatIconConfig.saveLastPosX(normalLayoutParams.leftMargin)
                FloatIconConfig.saveLastPosY(normalLayoutParams.topMargin)
            } else {
                FloatIconConfig.saveLastPosX(systemLayoutParams.x)
                FloatIconConfig.saveLastPosY(systemLayoutParams.y)
            }
        } else {
            //保存在内存中
            if (isNormalMode) {
                DokitViewManager.instance.saveDokitViewPos(tag, normalLayoutParams.leftMargin, normalLayoutParams.topMargin)
            } else {
                DokitViewManager.instance.saveDokitViewPos(tag, systemLayoutParams.x, systemLayoutParams.y)
            }
        }
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
     * @param dokitView
     */
    override fun onDokitViewAdd(dokitView: AbsDokitView?) {}
    override fun onResume() {}
    override fun onPause() {}

    /**
     * 系统悬浮窗需要调用
     *
     * @return
     */
    val context: Context?
        get() = this.rootView.context

    val resources: Resources?
        get() = context?.resources


    fun getString(@StringRes resId: Int): String? {
        return if (context == null) {
            null
        } else context!!.getString(resId)
    }

    val isShow: Boolean
        get() = this.rootView.isShown


    protected fun <T : View> findViewById(@IdRes id: Int): T {
        return this.rootView.findViewById(id)
    }


    /**
     * 将当前dokitView于activity解绑
     */
    fun detach() {
        DokitViewManager.instance.detach(this)
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
        }
    }

    /**
     * 更新view的位置
     *
     * @param isActivityResume 是否是从其他页面返回时更新的位置
     */
    open fun updateViewLayout(tag: String, isActivityResume: Boolean) {
        if (!isNormalMode) {
            return
        }
        if (isActivityResume) {
            if (tag == MainIconDokitView::class.java.simpleName) {
                normalLayoutParams.leftMargin = FloatIconConfig.getLastPosX()
                normalLayoutParams.topMargin = FloatIconConfig.getLastPosY()
            } else {
                val point = DokitViewManager.instance.getDokitViewPos(tag)
                if (point != null) {
                    normalLayoutParams.leftMargin = point.x
                    normalLayoutParams.topMargin = point.y
                }
            }
        } else {
            //非页面切换的时候保存当前位置信息
            mLastDokitViewPosInfo.setLeftMargin(normalLayoutParams.leftMargin)
            mLastDokitViewPosInfo.setTopMargin(normalLayoutParams.topMargin)
        }
        if (tag == MainIconDokitView::class.java.simpleName) {
            normalLayoutParams.width = MainIconDokitView.FLOAT_SIZE
            normalLayoutParams.height = MainIconDokitView.FLOAT_SIZE
        } else {
            normalLayoutParams.width = mDokitViewWidth
            normalLayoutParams.height = mDokitViewHeight
        }


        //portraitOrLandscape(mFrameLayoutParams);
        resetBorderline(normalLayoutParams)
        this.rootView.layoutParams = normalLayoutParams
    }

    /**
     * 限制边界 调用的时候必须保证是在控件能获取到宽高德前提下
     */
    private fun resetBorderline(normalFrameLayoutParams: FrameLayout.LayoutParams) {
        //如果是系统模式或者手动关闭动态限制边界
        if (!restrictBorderline() || !isNormalMode) {
            return
        }
        //LogHelper.i(TAG, "topMargin==>" + normalFrameLayoutParams.topMargin + "  leftMargin====>" + normalFrameLayoutParams.leftMargin);
        if (normalFrameLayoutParams.topMargin <= 0) {
            normalFrameLayoutParams.topMargin = 0
        }
        if (ScreenUtils.isPortrait()) {
            if (normalFrameLayoutParams.topMargin >= screenLongSideLength - mDokitViewHeight) {
                normalFrameLayoutParams.topMargin = screenLongSideLength - mDokitViewHeight
            }
        } else {
            if (normalFrameLayoutParams.topMargin >= screenShortSideLength - mDokitViewHeight) {
                normalFrameLayoutParams.topMargin = screenShortSideLength - mDokitViewHeight
            }
        }
        if (normalFrameLayoutParams.leftMargin <= 0) {
            normalFrameLayoutParams.leftMargin = 0
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
    }

    /**
     * 是否限制布局边界
     *
     * @return
     */
    open fun restrictBorderline(): Boolean {
        return true
    }


    fun post(runnable: Runnable?) {
        runnable?.let {
            mHandler?.post(runnable)
        }
    }

    fun postDelayed(delayMillis: Long, runnable: Runnable?) {
        runnable?.let {
            mHandler?.postDelayed(runnable, delayMillis)
        }
    }

    /**
     * 设置当前kitView不响应触摸事件
     * 控件默认响应触摸事件
     * 需要在子view的onViewCreated中调用
     */
    fun setDokitViewNotResponseTouchEvent(view: View?) {
        if (isNormalMode) {
            view?.setOnTouchListener { v, event -> false }
        } else {
            view?.setOnTouchListener(null)
        }
    }

    /**
     * 获取屏幕短边的长度 不包含statusBar
     * ScreenUtils.getScreenHeight(); 包含statusBar
     * ScreenUtils.getAppScreenHeight(); 不包含statusBar
     * @return
     */
    val screenShortSideLength: Int
        get() = if (ScreenUtils.isPortrait()) {
            ScreenUtils.getAppScreenWidth()
        } else {
            ScreenUtils.getAppScreenHeight()
        }

    /**
     * 获取屏幕长边的长度 不包含statusBar
     * ScreenUtils.getScreenHeight(); 包含statusBar
     * ScreenUtils.getAppScreenHeight(); 不包含statusBar
     * @return
     */
    val screenLongSideLength: Int
        get() = if (ScreenUtils.isPortrait()) {
            ScreenUtils.getAppScreenHeight()
        } else {
            ScreenUtils.getAppScreenWidth()
        }

    /**
     * 是否是普通的浮标模式
     *
     * @return
     */
    val isNormalMode: Boolean
        get() = DokitConstant.IS_NORMAL_FLOAT_MODE

    /**
     * 强制刷新当前dokitview
     */
    open fun invalidate() {
        this.rootView.layoutParams = normalLayoutParams
    }


}