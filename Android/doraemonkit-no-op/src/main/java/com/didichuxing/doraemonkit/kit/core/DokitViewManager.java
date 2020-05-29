package com.didichuxing.doraemonkit.kit.core;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;

import java.util.Map;

/**
 * Created by jintai on 2018/10/23.
 * 浮标管理类
 */

public class DokitViewManager implements DokitViewManagerInterface {


    /**
     * 静态内部类单例
     */
    private static class Holder {
        private static DokitViewManager INSTANCE = new DokitViewManager();
    }

    public static DokitViewManager getInstance() {
        return Holder.INSTANCE;
    }


    public void init(Context context) {

    }

    /**
     * 当app进入后台时调用
     */
    @Override
    public void notifyBackground() {

    }

    /**
     * 当app进入前台时调用
     */
    @Override
    public void notifyForeground() {

    }

    /**
     * 只有普通浮标才会调用
     * 保存每种类型dokitView的位置
     */
    public void saveDokitViewPos(String tag, int marginLeft, int marginTop) {

    }


    /**
     * 只有普通的浮标才需要调用
     * 添加activity关联的所有dokitView activity resume的时候回调
     *
     * @param activity
     */
    @Override
    public void resumeAndAttachDokitViews(Activity activity) {

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

    @Override
    public void onActivityPause(Activity activity) {

    }

    /**
     * 在当前Activity中添加指定悬浮窗
     *
     * @param dokitIntent
     */
    @Override
    public void attach(DokitIntent dokitIntent) {

    }

    @Override
    public void detach(AbsDokitView dokitView) {

    }

    /**
     * 隐藏工具列表dokitView
     */
    public void detachToolPanel() {

    }

    /**
     * 移除每个activity指定的dokitView
     */
    @Override
    public void detach(String tag) {

    }

    @Override
    public void detach(Class<? extends AbsDokitView> dokitViewClass) {

    }


    /**
     * 移除所有activity的所有dokitView
     */
    @Override
    public void detachAll() {
    }

    @Override
    public AbsDokitView getDokitView(Activity activity, String tag) {
        return null;
    }

    @Override
    public Map<String, AbsDokitView> getDokitViews(Activity activity) {
        return null;
    }


    /**
     * Activity销毁时调用
     */
    @Override
    public void onActivityDestroy(Activity activity) {
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

    }

    /**
     * 系统悬浮窗需要调用
     *
     * @param listener
     */
    void removeDokitViewAttachedListener(DokitViewAttachedListener listener) {

    }

    /**
     * 获取
     *
     * @return WindowManager
     */
    WindowManager getWindowManager() {
        return null;
    }

}
