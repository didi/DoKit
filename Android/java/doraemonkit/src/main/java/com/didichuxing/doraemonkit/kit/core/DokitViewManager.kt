package com.didichuxing.doraemonkit.kit.core

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.view.WindowManager
import androidx.room.Room
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.kit.main.MainIconDokitView
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDatabase
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager
import com.didichuxing.doraemonkit.kit.toolpanel.ToolPanelDokitView
import com.didichuxing.doraemonkit.util.ScreenUtils

/**
 * Created by jintai on 2018/10/23.
 * 浮标管理类
 */
class DokitViewManager : DokitViewManagerInterface {

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
            mutableMapOf()
        }
    }

    private val mLastDoKitViewPosInfoMaps: MutableMap<String, LastDokitViewPosInfo> by lazy {
        mutableMapOf()
    }
    private val mDoKitViewManager: DokitViewManagerInterface by lazy {
        if (DoKitConstant.IS_NORMAL_FLOAT_MODE) {
            NormalDokitViewManager()
        } else {
            SystemDokitViewManager()
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

    private var mContext: Context? = null

    /**
     * 数据库操作类
     */
    private var mDB: DokitDatabase? = null


    fun init(context: Context?) {
        mContext = context
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
    override fun resumeAndAttachDokitViews(activity: Activity) {
        mDoKitViewManager.resumeAndAttachDokitViews(activity)
    }

    override fun onMainActivityCreate(activity: Activity) {}
    override fun onActivityCreate(activity: Activity) {}
    override fun onActivityResume(activity: Activity) {}
    override fun onActivityPause(activity: Activity) {
        mDoKitViewManager.onActivityPause(activity)
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
        detach(ToolPanelDokitView::class.java.simpleName)
    }

    /**
     * 显示工具列表dokitView
     */
    fun attachToolPanel() {
        val toolPanelIntent = DokitIntent(
            ToolPanelDokitView::class.java
        )
        toolPanelIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE
        attach(toolPanelIntent)
    }

    /**
     * 显示主图标 dokitView
     */
    fun attachMainIcon() {
        val mainIconIntent = DokitIntent(
            MainIconDokitView::class.java
        )
        mainIconIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE
        attach(mainIconIntent)
    }

    /**
     * 隐藏首页图标
     */
    fun detachMainIcon() {
        detach(MainIconDokitView::class.java.simpleName)
    }

    /**
     * 移除每个activity指定的dokitView
     */
    override fun detach(tag: String) {
        mDoKitViewManager.detach(tag)
    }

    override fun detach(activity: Activity, tag: String) {
        mDoKitViewManager.detach(activity, tag)
    }

    /**
     * 移除每个activity指定的dokitView
     */
    override fun detach(dokitView: AbsDokitView) {
        mDoKitViewManager.detach(dokitView)
    }

    override fun detach(activity: Activity, dokitView: AbsDokitView) {
        mDoKitViewManager.detach(activity, dokitView)
    }

    override fun detach(dokitViewClass: Class<out AbsDokitView>) {
        mDoKitViewManager.detach(dokitViewClass)
    }

    override fun detach(activity: Activity, dokitViewClass: Class<out AbsDokitView>) {
        mDoKitViewManager.detach(activity, dokitViewClass)
    }

    /**
     * 移除所有activity的所有dokitView
     */
    override fun detachAll() {
        mDoKitViewManager.detachAll()
    }

    /**
     * Activity销毁时调用
     */
    override fun onActivityDestroy(activity: Activity) {
        mDoKitViewManager.onActivityDestroy(activity)
    }

    /**
     * 获取页面上指定的dokitView
     *
     * @param activity 如果是系统浮标 activity可以为null
     * @param tag
     * @return
     */
    override fun getDokitView(activity: Activity, tag: String): AbsDokitView? {

        return mDoKitViewManager.getDokitView(activity, tag)
    }

    /**
     * @param activity
     * @return
     */
    override fun getDokitViews(activity: Activity): Map<String, AbsDokitView>? {
        return mDoKitViewManager.getDokitViews(activity)
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

        if (!DoKitConstant.IS_NORMAL_FLOAT_MODE && mDoKitViewManager is SystemDokitViewManager) {
            (mDoKitViewManager as SystemDokitViewManager).addListener(listener!!)
        }
    }

    /**
     * 系统悬浮窗需要调用
     *
     * @param listener
     */
    fun removeDokitViewAttachedListener(listener: DokitViewAttachedListener?) {

        if (!DoKitConstant.IS_NORMAL_FLOAT_MODE && mDoKitViewManager is SystemDokitViewManager) {
            (mDoKitViewManager as SystemDokitViewManager).removeListener(listener!!)
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