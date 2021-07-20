package com.didichuxing.doraemonkit.kit.core

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.view.WindowManager
import androidx.room.Room
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDatabase
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager
import com.didichuxing.doraemonkit.util.ScreenUtils
import kotlin.reflect.KClass

/**
 * Created by jintai on 2018/10/23.
 * 浮标管理类
 */
internal class DokitViewManager : DokitViewManagerInterface {

    companion object {
        @JvmStatic
        val instance: DokitViewManager by lazy {
            DokitViewManager()
        }
        private const val TAG = "DokitViewManagerProxy"

        /**
         * 每个类型在页面中的位置 只保存marginLeft 和marginTop
         */
        private val mDoKitViewPos: MutableMap<String, DoKitViewInfo> by lazy {
            mutableMapOf<String, DoKitViewInfo>()
        }
    }

    private val mLastDoKitViewPosInfoMaps: MutableMap<String, LastDokitViewPosInfo> by lazy {
        mutableMapOf<String, LastDokitViewPosInfo>()
    }
    private val mDoKitViewManager: AbsDokitViewManager by lazy {
        if (DoKitManager.IS_NORMAL_FLOAT_MODE) {
            NormalDoKitViewManager()
        } else {
            SystemDoKitViewManager()
        }
    }

    //下面注释表示允许主线程进行数据库操作，但是不推荐这样做。
    //他可能造成主线程lock以及anr
    //所以我们的操作都是在新线程完成的
    val db: DokitDatabase by lazy {
        Room.databaseBuilder(
            DoKit.APPLICATION,
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
        mDoKitViewManager.notifyBackground()
    }

    /**
     * 当app进入前台时调用
     */
    override fun notifyForeground() {
        mDoKitViewManager.notifyForeground()
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
        if (mDoKitViewPos[tag] == null) {
            val doKitViewInfo = DoKitViewInfo(orientation, portraitPoint, landscapePoint)
            mDoKitViewPos[tag] =
                doKitViewInfo
        } else {
            val doKitViewInfo = mDoKitViewPos[tag]
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
    fun getDoKitViewPos(tag: String): DoKitViewInfo? {
        return mDoKitViewPos[tag]
    }

    /**
     * 只有普通的浮标才需要调用
     * 添加activity关联的所有dokitView activity resume的时候回调
     *
     * @param activity
     */
    override fun dispatchOnActivityResumed(activity: Activity) {
        mDoKitViewManager.dispatchOnActivityResumed(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        mDoKitViewManager.onActivityPaused(activity)
    }

    override fun onActivityStopped(activity: Activity) {
        mDoKitViewManager.onActivityStopped(activity)
    }

    /**
     * 在当前Activity中添加指定悬浮窗
     *
     * @param dokitIntent
     */
    override fun attach(dokitIntent: DokitIntent) {
        mDoKitViewManager.attach(dokitIntent)
    }

    /**
     * 隐藏工具列表dokitView
     */
    fun detachToolPanel() {
        mDoKitViewManager.detachToolPanel()
    }

    /**
     * 显示工具列表dokitView
     */
    fun attachToolPanel(activity: Activity) {
        mDoKitViewManager.attachToolPanel(activity)
    }

    /**
     * 显示主图标 dokitView
     */
    fun attachMainIcon(activity: Activity) {
        mDoKitViewManager.attachMainIcon(activity)
    }

    /**
     * 隐藏首页图标
     */
    fun detachMainIcon() {
        mDoKitViewManager.detachMainIcon()
    }

    /**
     * 移除每个activity指定的dokitView
     */
    override fun detach(tag: String) {
        mDoKitViewManager.detach(tag)
    }


    /**
     * 移除每个activity指定的dokitView
     */
    override fun detach(dokitView: AbsDokitView) {
        mDoKitViewManager.detach(dokitView)
    }


    override fun detach(doKitViewClass: Class<out AbsDokitView>) {
        mDoKitViewManager.detach(doKitViewClass)
    }


    /**
     * 移除所有activity的所有dokitView
     */
    override fun detachAll() {
        mDoKitViewManager.detachAll()
    }

    /**
     * 获取页面上指定的dokitView
     *
     * @param activity 如果是系统浮标 activity可以为null
     * @param tag
     * @return
     */
    override fun <T : AbsDokitView> getDoKitView(
        activity: Activity,
        clazz: Class<T>
    ): AbsDokitView? {
        return mDoKitViewManager.getDoKitView(activity, clazz)
    }

    /**
     * Activity销毁时调用
     */
    override fun onActivityDestroyed(activity: Activity) {
        mDoKitViewManager.onActivityDestroyed(activity)
    }


    /**
     * @param activity
     * @return
     */
    override fun getDoKitViews(activity: Activity): Map<String, AbsDokitView>? {
        return mDoKitViewManager.getDoKitViews(activity)
    }

    /**
     * 系统悬浮窗需要调用
     */
    interface DokitViewAttachedListener {
        fun onDokitViewAdd(dokitView: AbsDokitView?)
    }

    /**
     * 系统悬浮窗需要调用
     *
     * @param listener
     */
    fun addDokitViewAttachedListener(listener: DokitViewAttachedListener?) {

        if (!DoKitManager.IS_NORMAL_FLOAT_MODE && mDoKitViewManager is SystemDoKitViewManager) {
            (mDoKitViewManager as SystemDoKitViewManager).addListener(listener!!)
        }
    }

    /**
     * 系统悬浮窗需要调用
     *
     * @param listener
     */
    fun removeDokitViewAttachedListener(listener: DokitViewAttachedListener?) {

        if (!DoKitManager.IS_NORMAL_FLOAT_MODE && mDoKitViewManager is SystemDoKitViewManager) {
            (mDoKitViewManager as SystemDoKitViewManager).removeListener(listener!!)
        }
    }

    /**
     * 获取
     *
     * @return WindowManager
     */
    val windowManager: WindowManager
        get() = DoKit.APPLICATION.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    fun saveLastDokitViewPosInfo(key: String, lastDokitViewPosInfo: LastDokitViewPosInfo) {
        mLastDoKitViewPosInfoMaps[key] = lastDokitViewPosInfo
    }

    fun getLastDokitViewPosInfo(key: String): LastDokitViewPosInfo? {
        return mLastDoKitViewPosInfoMaps[key]
    }

    fun removeLastDokitViewPosInfo(key: String) {

        mLastDoKitViewPosInfoMaps.remove(key)
    }


}