package com.didichuxing.doraemonkit.ui.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-20-16:22
 * 描    述：dokit 页面浮标抽象类 一般的悬浮窗都需要继承该抽象接口
 * 修订历史：
 * ================================================
 */
public abstract class AbsDokitView implements DokitView,TouchProxy.OnTouchEventListener, DokitViewManager.DokitViewAttachedListener {



    protected WindowManager mWindowManager = DokitViewManager.getInstance().getWindowManager();
    /**
     * 创建FrameLayout#LayoutParams 内置悬浮窗调用
     */
    private FrameLayout.LayoutParams mFrameLayoutParams;
    /**
     * 创建FrameLayout#LayoutParams 系统悬浮窗调用
     */
    private WindowManager.LayoutParams mWindowLayoutParams;
    private Handler mHandler;

    /**
     * 当前dokitViewName 用来当做map的key 和dokitViewIntent的tag一致
     */

    private Bundle mBundle;
    /**
     * weakActivity attach activity
     */
    private WeakReference<Activity> mAttachActivity;

    private FrameLayout mRootView;
    /**
     * rootView的直接子View 一般是用户的xml布局 被添加到mRootView中
     */
    private View mChildView;


    /**
     * 执行floatPage create
     *
     * @param context
     */
    @SuppressLint("ClickableViewAccessibility")
    protected void performCreate(Context context) {


    }

    void performDestroy() {

    }

    /**
     * 确定普通浮标的初始位置
     * LayoutParams创建完以后调用
     * 调用时建议放在实现下方
     *
     * @param params
     */
    private void onNormalLayoutParamsCreated(FrameLayout.LayoutParams params) {

    }

    /**
     * 确定系统浮标的初始位置
     * LayoutParams创建完以后调用
     * 调用时建议放在实现下方
     *
     * @param params
     */
    private void onSystemLayoutParamsCreated(WindowManager.LayoutParams params) {

    }

    @Override
    public void initDokitViewLayoutParams(DokitViewLayoutParams params) {

    }

    @Override
    public void onDestroy() {

    }

    /**
     * 默认实现为true
     *
     * @return
     */
    @Override
    public boolean canDrag() {
        return true;
    }

    /**
     * 搭配shouldDealBackKey使用
     */
    @Override
    public boolean onBackPressed() {
        return false;
    }

    /**
     * 默认不自己处理返回按键
     *
     * @return
     */
    @Override
    public boolean shouldDealBackKey() {
        return false;
    }

    @Override
    public void onEnterBackground() {

    }

    @Override
    public void onEnterForeground() {

    }

    @Override
    public void onMove(int x, int y, int dx, int dy) {


    }


    /**
     * 手指弹起时保存当前浮标位置
     *
     * @param x
     * @param y
     */
    @Override
    public void onUp(int x, int y) {


    }

    /**
     * 手指按下时的操作
     *
     * @param x
     * @param y
     */
    @Override
    public void onDown(int x, int y) {

    }



    /**
     * home键被点击 只有系统悬浮窗控件才会被调用
     */
    public void onHomeKeyPress() {

    }


    /**
     * 菜单键被点击 只有系统悬浮窗控件才会被调用
     */
    public void onRecentAppKeyPress() {

    }


    /**
     * 当前的dokitView添加到根布局里时调用 只有普通模式下会被调用
     */
    @Override
    public void onResume() {

    }

    /**
     * 系统悬浮窗需要调用
     *
     * @return
     */
    public Context getContext() {
       return  null;
    }


    public Resources getResources() {
        return  null;
    }

    public String getString(@StringRes int resId) {

        return null;
    }

    public boolean isShow() {
        return false;
    }

    protected <T extends View> T findViewById(@IdRes int id) {
        return null;
    }

    public View getRootView() {
        return null;
    }

    public FrameLayout.LayoutParams getNormalLayoutParams() {
        return null;
    }

    public WindowManager.LayoutParams getSystemLayoutParams() {
        return null;
    }

    /**
     * 将当前dokitview于activity解绑
     */
    public void detach() {

    }

    /**
     * 操作DecorView的直接子布局
     * 测试专用
     */
    public void dealDecorRootView(FrameLayout decorRootView) {

    }

    /**
     * 更新view的位置
     *
     * @param isActivityResume 是否是从其他页面返回时更新的位置
     */
    public void updateViewLayout(String tag, boolean isActivityResume) {

    }

    /**
     * 限制边界
     */
    private void resetBorderline(FrameLayout.LayoutParams normalFrameLayoutParams) {


    }


    /**
     * 是否限制布局边界
     *
     * @return
     */
    public boolean restrictBorderline() {
        return true;
    }

    public String getTag() {
        return null;
    }

    public void setTag(String mTag) {

    }

    public Bundle getBundle() {
        return null;
    }

    public void setBundle(Bundle mBundle) {

    }

    public Activity getActivity() {

        return null;
    }

    public void setActivity(Activity activity) {

    }

    public void post(Runnable r) {
    }

    public void postDelayed(Runnable r, long delayMillis) {
    }

    /**
     * 设置当前kitView不响应触摸事件
     * 控件默认响应触摸事件
     * 需要在子view的onViewCreated中调用
     */
    public void setDokitViewNotResponseTouchEvent(View view) {


    }


    /**
     * 获取屏幕短边的长度
     *
     * @return
     */
    public int getScreenShortSideLength() {
        return 0;
    }

    /**
     * 获取屏幕长边的长度
     *
     * @return
     */
    public int getScreenLongSideLength() {
        return  0;
    }


    /**
     * 是否是普通的浮标模式
     *
     * @return
     */
    public boolean isNormalMode() {
        return true;
    }

    @Override
    public void onDokitViewAdd(AbsDokitView dokitView) {

    }
}
