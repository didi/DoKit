package com.didichuxing.doraemonkit.kit.core;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-20-16:22
 * 描    述：dokit 页面浮标接口
 * 修订历史：
 * ================================================
 */
interface DoKitView {


    /**
     * dokit view 创建时调用 做一些变量的初始化  当还不能进行View的操作
     *
     * @param context
     */
    void onCreate(Context context);

    /**
     * 传入rootView 用于创建kit控件
     *
     * @param context
     * @param rootView
     * @return 返回创建的childView
     */
    View onCreateView(Context context, FrameLayout rootView);


    /**
     * 将xml中的控件添加到rootView以后调用，在当前方法中可以进行view的一些操作
     *
     * @param rootView
     */
    void onViewCreated(FrameLayout rootView);

    /**
     * 当前的dokitView添加到根布局里时调用
     */
    void onResume();

    /**
     * 当前activity onPause时调用
     */
    void onPause();

    /**
     * 确定浮标的初始位置
     * LayoutParams创建完以后调用
     * 调用时建议放在实现下方
     *
     * @param layoutParams
     */
    //void onNormalLayoutParamsCreated(FrameLayout.LayoutParams layoutParams);


    /**
     * 确定系统悬浮窗浮标的初始位置
     * LayoutParams创建完以后调用
     *
     * @param layoutParams
     */
    //void onSystemLayoutParamsCreated(WindowManager.LayoutParams layoutParams);

    /**
     * 确定系统悬浮窗浮标的初始位置
     * LayoutParams创建完以后调用
     *
     * @param params
     */

    void initDokitViewLayoutParams(DoKitViewLayoutParams params);

    /**
     * app进入后台时调用 内置dokitView 不需要实现
     */
    void onEnterBackground();

    /**
     * app回到前台时调用 内置dokitview 不需要实现
     */
    void onEnterForeground();

    /**
     * 浮标控件是否可以拖动
     *
     * @return
     */
    boolean canDrag();

    /**
     * 是否需要自己处理返回键
     *
     * @return
     */
    boolean shouldDealBackKey();

    /**
     * shuldDealBackKey == true 时调用
     */
    boolean onBackPressed();

    /**
     * 悬浮窗主动销毁时调用 不能在当前生命周期回调函数中调用 detach自己 否则会出现死循环
     */
    void onDestroy();

}
