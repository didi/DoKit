package com.didichuxing.doraemonkit.kit.core

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.view.WindowManager
import androidx.collection.ArrayMap
import androidx.room.Room
import com.didichuxing.doraemonkit.DoKitEnv
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDatabase
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager
import com.didichuxing.doraemonkit.util.ScreenUtils

/**
 * Created by jintai on 2018/10/23.
 * 浮标管理类
 */
internal class DoKitViewManager : DoKitViewManagerInterface {

    companion object {
        private const val TAG = "DoKitViewManager"

        @JvmStatic
        val INSTANCE: DoKitViewManager by lazy { DoKitViewManager() }

        /**
         * 每个类型在页面中的位置 只保存marginLeft 和marginTop
         */
        private val doKitViewPos: MutableMap<String, DoKitViewInfo> = ArrayMap<String, DoKitViewInfo>()
    }

    /**
     * Retrieves app [WindowManager]
     *
     * @return WindowManager
     */
    val windowManager: WindowManager
        get() = DoKitEnv.requireApp().getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val lastDoKitViewPosInfoMaps: MutableMap<String, LastDoKitViewPosInfo> = ArrayMap<String, LastDoKitViewPosInfo>()

    private var _doKitViewManager: AbsDoKitViewManager? = null

    //下面注释表示允许主线程进行数据库操作，但是不推荐这样做。
    //他可能造成主线程lock以及anr
    //所以我们的操作都是在新线程完成的
    val db: DokitDatabase by lazy {
        Room.databaseBuilder(
            DoKitEnv.requireApp(),
            DokitDatabase::class.java,
            "dokit-database"
        ) //下面注释表示允许主线程进行数据库操作，但是不推荐这样做。
            //他可能造成主线程lock以及anr
            //所以我们的操作都是在新线程完成的
            .allowMainThreadQueries()
            .build()
    }

    fun init() {
        //获取所有的intercept apis
        DokitDbManager.getInstance().getAllInterceptApis()

        //获取所有的template apis
        DokitDbManager.getInstance().getAllTemplateApis()
    }

    /**
     * 当app进入后台时调用
     */
    override fun notifyBackground() {
        ensureViewManager().notifyBackground()
    }

    /**
     * 当app进入前台时调用
     */
    override fun notifyForeground() {
        ensureViewManager().notifyForeground()
    }

    /**
     * 只有普通浮标才会调用
     * 保存每种类型dokitView的位置
     */
    fun saveDokitViewPos(tag: String, marginLeft: Int, marginTop: Int) {

        var orientation = -1
        val portraitPoint = Point()
        val landscapePoint = Point()
        if (ScreenUtils.isPortrait()) {
            orientation = Configuration.ORIENTATION_PORTRAIT
            portraitPoint.x = marginLeft
            portraitPoint.y = marginTop
        } else {
            orientation = Configuration.ORIENTATION_LANDSCAPE
            landscapePoint.x = marginLeft
            landscapePoint.y = marginTop
        }
        if (doKitViewPos[tag] == null) {
            val doKitViewInfo = DoKitViewInfo(orientation, portraitPoint, landscapePoint)
            doKitViewPos[tag] =
                doKitViewInfo
        } else {
            val doKitViewInfo = doKitViewPos[tag]
            if (doKitViewInfo != null) {
                doKitViewInfo.orientation = orientation
                doKitViewInfo.portraitPoint = portraitPoint
                doKitViewInfo.landscapePoint = landscapePoint
            }
        }
    }

    /**
     * 只有普通的浮标才需要调用
     * 获得指定dokitView的位置信息
     *
     * @param tag
     * @return
     */
    fun getDoKitViewPos(tag: String): DoKitViewInfo? = doKitViewPos[tag]

    /**
     * 只有普通的浮标才需要调用
     * 添加activity关联的所有dokitView activity resume的时候回调
     *
     * @param activity
     */
    override fun dispatchOnActivityResumed(activity: Activity?) {
        activity?.also { ensureViewManager().dispatchOnActivityResumed(it) }
    }

    override fun onActivityPaused(activity: Activity?) {
        activity?.also { ensureViewManager().onActivityPaused(it) }
    }

    override fun onActivityStopped(activity: Activity?) {
        activity?.also { ensureViewManager().onActivityStopped(it) }
    }

    /**
     * 在当前Activity中添加指定悬浮窗
     *
     * @param doKitIntent
     */
    override fun attach(doKitIntent: DoKitIntent) {
        ensureViewManager().attach(doKitIntent)
    }

    /**
     * 隐藏工具列表dokitView
     */
    fun detachToolPanel() {
        ensureViewManager().detachToolPanel()
    }

    /**
     * 显示工具列表dokitView
     */
    fun attachToolPanel(activity: Activity) {
        ensureViewManager().attachToolPanel(activity)
    }

    /**
     * 显示主图标 dokitView
     */
    fun attachMainIcon(activity: Activity) {
        ensureViewManager().attachMainIcon(activity)
    }

    /**
     * 隐藏首页图标
     */
    fun detachMainIcon() {
        ensureViewManager().detachMainIcon()
    }

    /**
     * 移除每个activity指定的dokitView
     */
    override fun detach(tag: String) {
        ensureViewManager().detach(tag)
    }

    /**
     * 移除每个activity指定的dokitView
     */
    override fun detach(doKitView: AbsDoKitView) {
        ensureViewManager().detach(doKitView)
    }

    override fun detach(doKitViewClass: Class<out AbsDoKitView>) {
        ensureViewManager().detach(doKitViewClass)
    }

    /**
     * 移除所有activity的所有dokitView
     */
    override fun detachAll() {
        ensureViewManager().detachAll()
    }

    /**
     * 获取页面上指定的dokitView
     *
     * @param activity 如果是系统浮标 activity可以为null
     * @param tag
     * @return
     */
    override fun <T : AbsDoKitView> getDoKitView(activity: Activity?, clazz: Class<T>): AbsDoKitView? {
        return activity?.let { ensureViewManager().getDoKitView(it, clazz) }
    }

    /**
     * Activity销毁时调用
     */
    override fun onActivityDestroyed(activity: Activity?) {
        activity?.also { ensureViewManager().onActivityDestroyed(it) }
    }

    /**
     * @param activity
     * @return
     */
    override fun getDoKitViews(activity: Activity?): Map<String, AbsDoKitView>? {
        return activity?.let { ensureViewManager().getDoKitViews(it) }
    }

    /**
     * 系统悬浮窗需要调用
     */
    interface DokitViewAttachedListener {
        fun onDokitViewAdd(dokitView: AbsDoKitView?)
    }

    /**
     * 系统悬浮窗需要调用
     *
     * @param listener
     */
    fun addDokitViewAttachedListener(listener: DokitViewAttachedListener?) {
        listener?.takeIf { !DoKitManager.IS_NORMAL_FLOAT_MODE && _doKitViewManager is SystemDoKitViewManager }
            ?.also {
                (_doKitViewManager as? SystemDoKitViewManager)?.addListener(it)
            }
    }

    /**
     * 系统悬浮窗需要调用
     *
     * @param listener
     */
    fun removeDokitViewAttachedListener(listener: DokitViewAttachedListener?) {
        listener?.takeIf { !DoKitManager.IS_NORMAL_FLOAT_MODE && _doKitViewManager is SystemDoKitViewManager }
            ?.also {
                (_doKitViewManager as SystemDoKitViewManager).removeListener(it)
            }
    }

    fun saveLastDokitViewPosInfo(key: String, lastDoKitViewPosInfo: LastDoKitViewPosInfo) {
        lastDoKitViewPosInfoMaps[key] = lastDoKitViewPosInfo
    }

    fun getLastDokitViewPosInfo(key: String): LastDoKitViewPosInfo? = lastDoKitViewPosInfoMaps[key]

    fun removeLastDokitViewPosInfo(key: String) {
        lastDoKitViewPosInfoMaps.remove(key)
    }

    @Synchronized
    private fun ensureViewManager(): AbsDoKitViewManager {
        return _doKitViewManager
            ?: run {
                if (DoKitManager.IS_NORMAL_FLOAT_MODE) NormalDoKitViewManager() else SystemDoKitViewManager()
            }.also {
                _doKitViewManager = it
            }
    }
}
