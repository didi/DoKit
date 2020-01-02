package com.didichuxing.doraemonkit.ui.base;

import android.app.Activity;

import java.util.Map;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-28-15:18
 * 描    述：页面浮标管理类接口
 * 修订历史：
 * ================================================
 */
public interface DokitViewManagerInterface {


    /**
     * 在当前Activity中添加指定悬浮窗
     *
     * @param dokitIntent
     */
    void attach(DokitIntent dokitIntent);


    /**
     * 移除每个activity指定的dokitView
     *
     * @param dokitView
     */
    void detach(AbsDokitView dokitView);

    /**
     * 移除每个activity指定的dokitView tag
     *
     * @param tag 一般为dokitView的className
     */
    void detach(String tag);

    /**
     * 移除指定的dokitView
     *
     * @param dokitViewClass
     */
    void detach(Class<? extends AbsDokitView> dokitViewClass);

    /**
     * 移除所有activity的所有dokitView
     */
    void detachAll();


    /**
     * 获取页面上指定的dokitView
     *
     * @param activity
     * @param tag
     * @return
     */
    AbsDokitView getDokitView(Activity activity, String tag);


    /**
     * 获取页面上所有的dokitView
     *
     * @param activity
     * @return
     */
    Map<String, AbsDokitView> getDokitViews(Activity activity);

    /**
     * 当app进入后台时调用
     */
    void notifyBackground();


    /**
     * 当app进入前台时调用
     */
    void notifyForeground();


    /**
     * Activity销毁时调用
     *
     * @param activity
     */
    void onActivityDestroy(Activity activity);

    /**
     * 只有普通的浮标才需要调用
     * 添加activity关联的所有dokitView activity resume的时候回调
     *
     * @param activity
     */
    void resumeAndAttachDokitViews(Activity activity);

    /**
     * main activity 创建时回调
     *
     * @param activity
     */
    void onMainActivityCreate(Activity activity);

    /**
     * 除main activity 以外 其他activty 创建时回调
     *
     * @param activity
     */
    void onActivityCreate(Activity activity);

    /**
     * 页面回退的时候调用
     *
     * @param activity
     */
    void onActivityResume(Activity activity);
}
