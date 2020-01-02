package com.didichuxing.doraemonkit.ui.base;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

import com.didichuxing.doraemonkit.constant.DokitConstant;
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDatabase;
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager;
import com.didichuxing.doraemonkit.ui.main.ToolPanelDokitView;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jintai on 2018/10/23.
 * 浮标管理类
 */

public class DokitViewManager implements DokitViewManagerInterface {
    public static final String TAG = "DokitViewManagerProxy";
    /**
     * 每个类型在页面中的位置 只保存marginLeft 和marginTop
     */
    private static Map<String, Point> mDokitViewPos;

    private Map<String, LastDokitViewPosInfo> mLastDokitViewPosInfoMaps;


    private DokitViewManagerInterface mDokitViewManager;
    private Context mContext;
    /**
     * 数据库操作类
     */
    private DokitDatabase mDB;

    /**
     * 静态内部类单例
     */
    private static class Holder {
        private static DokitViewManager INSTANCE = new DokitViewManager();
    }

    public static DokitViewManager getInstance() {
        return DokitViewManager.Holder.INSTANCE;
    }


    public void init(Context context) {
        mContext = context;
        if (DokitConstant.IS_NORMAL_FLOAT_MODE) {
            mDokitViewManager = new NormalDokitViewManager(context);
        } else {
            mDokitViewManager = new SystemDokitViewManager(context);
        }
        mDokitViewPos = new HashMap<>();
        mLastDokitViewPosInfoMaps = new HashMap<>();
        mDB = Room.databaseBuilder(context,
                DokitDatabase.class,
                "dokit-database")
                //下面注释表示允许主线程进行数据库操作，但是不推荐这样做。
                //他可能造成主线程lock以及anr
                //所以我们的操作都是在新线程完成的
                .allowMainThreadQueries()
                .build();
        //获取所有的intercept apis
        DokitDbManager.getInstance().getAllInterceptApis();

        //获取所有的template apis
        DokitDbManager.getInstance().getAllTemplateApis();

    }

    public DokitDatabase getDb() {
        return mDB;
    }

    /**
     * 当app进入后台时调用
     */
    @Override
    public void notifyBackground() {
        mDokitViewManager.notifyBackground();
    }

    /**
     * 当app进入前台时调用
     */
    @Override
    public void notifyForeground() {
        mDokitViewManager.notifyForeground();
    }

    /**
     * 只有普通浮标才会调用
     * 保存每种类型dokitView的位置
     */
    public void saveDokitViewPos(String tag, int marginLeft, int marginTop) {
        if (mDokitViewPos == null) {
            return;
        }

        if (mDokitViewPos.get(tag) == null) {
            Point point = new Point(marginLeft, marginTop);
            mDokitViewPos.put(tag, point);
        } else {
            Point point = mDokitViewPos.get(tag);
            point.set(marginLeft, marginTop);
        }

        for (String key : mDokitViewPos.keySet()) {
            LogHelper.i(TAG, "saveDokitViewPos  key==> " + key + "  point===>" + mDokitViewPos.get(key));
        }
    }

    /**
     * 只有普通的浮标才需要调用
     * 获得指定dokitView的位置信息
     *
     * @param tag
     * @return
     */
    Point getDokitViewPos(String tag) {
        if (mDokitViewPos == null) {
            return null;
        }

        for (String key : mDokitViewPos.keySet()) {
            LogHelper.i(TAG, "getDokitViewPos  key==> " + key + "  point===>" + mDokitViewPos.get(key));
        }
        return mDokitViewPos.get(tag);
    }

    /**
     * 只有普通的浮标才需要调用
     * 添加activity关联的所有dokitView activity resume的时候回调
     *
     * @param activity
     */
    @Override
    public void resumeAndAttachDokitViews(Activity activity) {
        mDokitViewManager.resumeAndAttachDokitViews(activity);
    }

    @Override
    public void onMainActivityCreate(Activity activity) {

    }

    @Override
    public void onActivityCreate(Activity activity) {

    }

    @Override
    public void onActivityResume(Activity activity) {

    }

    /**
     * 在当前Activity中添加指定悬浮窗
     *
     * @param dokitIntent
     */
    @Override
    public void attach(DokitIntent dokitIntent) {
        mDokitViewManager.attach(dokitIntent);
    }

    /**
     * 隐藏工具列表dokitView
     */
    public void detachToolPanel() {
        detach(ToolPanelDokitView.class.getSimpleName());
    }

    /**
     * 移除每个activity指定的dokitView
     */
    @Override
    public void detach(String tag) {
        mDokitViewManager.detach(tag);
    }


    /**
     * 移除每个activity指定的dokitView
     */
    @Override
    public void detach(AbsDokitView dokitView) {
        mDokitViewManager.detach(dokitView);
    }

    /**
     * 移除所有activity的所有dokitView
     */
    @Override
    public void detachAll() {
        mDokitViewManager.detachAll();
    }

    @Override
    public void detach(Class<? extends AbsDokitView> dokitViewClass) {
        mDokitViewManager.detach(dokitViewClass);
    }


    /**
     * Activity销毁时调用
     */
    @Override
    public void onActivityDestroy(Activity activity) {
        mDokitViewManager.onActivityDestroy(activity);
    }

    /**
     * 获取页面上指定的dokitView
     *
     * @param activity 如果是系统浮标 activity可以为null
     * @param tag
     * @return
     */
    @Override
    public AbsDokitView getDokitView(Activity activity, String tag) {
        return mDokitViewManager.getDokitView(activity, tag);
    }

    /**
     * @param activity
     * @return
     */
    @Override
    public Map<String, AbsDokitView> getDokitViews(Activity activity) {
        return mDokitViewManager.getDokitViews(activity);
    }

    /**
     * 系统悬浮窗需要调用
     */
    public interface DokitViewAttachedListener {
        void onDokitViewAdd(AbsDokitView dokitView);
    }

    /**
     * 系统悬浮窗需要调用
     *
     * @param listener
     */
    void addDokitViewAttachedListener(DokitViewAttachedListener listener) {
        if (!DokitConstant.IS_NORMAL_FLOAT_MODE && mDokitViewManager instanceof SystemDokitViewManager) {
            ((SystemDokitViewManager) mDokitViewManager).addListener(listener);
        }
    }

    /**
     * 系统悬浮窗需要调用
     *
     * @param listener
     */
    void removeDokitViewAttachedListener(DokitViewAttachedListener listener) {
        if (!DokitConstant.IS_NORMAL_FLOAT_MODE && mDokitViewManager instanceof SystemDokitViewManager) {
            ((SystemDokitViewManager) mDokitViewManager).removeListener(listener);
        }
    }

    /**
     * 获取
     *
     * @return WindowManager
     */
    WindowManager getWindowManager() {
        return (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    void saveLastDokitViewPosInfo(String key, LastDokitViewPosInfo lastDokitViewPosInfo) {
        if (mLastDokitViewPosInfoMaps != null) {
            mLastDokitViewPosInfoMaps.put(key, lastDokitViewPosInfo);
        }
    }

    LastDokitViewPosInfo getLastDokitViewPosInfo(String key) {
        if (mLastDokitViewPosInfoMaps == null) {
            return null;
        }
        return mLastDokitViewPosInfoMaps.get(key);
    }


    void removeLastDokitViewPosInfo(String key) {
        if (mLastDokitViewPosInfoMaps == null) {
            return;
        }
        mLastDokitViewPosInfoMaps.remove(key);
    }

}
