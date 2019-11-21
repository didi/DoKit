package com.didichuxing.doraemonkit.ui.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.UniversalActivity;
import com.didichuxing.doraemonkit.ui.main.FloatIconDokitView;
import com.didichuxing.doraemonkit.ui.main.ToolPanelDokitView;
import com.didichuxing.doraemonkit.ui.realtime.PerformanceDokitView;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jintai on 2018/10/23.
 * 每个activity悬浮窗管理类
 */

class NormalDokitViewManager implements DokitViewManagerInterface {
    private static final String TAG = "NormalDokitViewManager";
    /**
     * 每个Activity中baseFloatPopView的集合
     */
    private Map<Activity, Map<String, AbsDokitView>> mActivityDokitViews;
    /**
     * 全局的同步mActivityFloatPopViews 应该在页面上显示的popView集合
     */
    private Map<String, GlobalSingleDokitViewInfo> mGlobalSingleDokitViews;

    private Context mContext;

    /**
     * 当app进入后台时调用
     */
    @Override
    public void notifyBackground() {
        if (mActivityDokitViews == null) {
            return;
        }
        //双层遍历
        for (Map<String, AbsDokitView> dokitViewMap : mActivityDokitViews.values()) {
            for (AbsDokitView dokitView : dokitViewMap.values()) {
                dokitView.onEnterBackground();
            }
        }
    }

    /**
     * 当app进入前台时调用
     */
    @Override
    public void notifyForeground() {
        if (mActivityDokitViews == null) {
            return;
        }
        //双层遍历
        for (Map<String, AbsDokitView> dokitViewMap : mActivityDokitViews.values()) {
            for (AbsDokitView dokitView : dokitViewMap.values()) {
                dokitView.onEnterForeground();
            }
        }
    }

    NormalDokitViewManager(Context context) {
        mContext = context.getApplicationContext();
        //创建key为Activity的baseFloatPopView
        mActivityDokitViews = new HashMap<>();
        mGlobalSingleDokitViews = new HashMap<>();
    }


    /**
     * 添加activity关联的所有popView activity resume的时候回调
     *
     * @param activity
     */
    @Override
    public void resumeAndAttachDokitViews(Activity activity) {
        if (mActivityDokitViews == null) {
            return;
        }
        //app启动
        if (mGlobalSingleDokitViews.size() == 0) {
            LogHelper.i(TAG, "app 启动==>" + activity.getClass().getSimpleName());
            if (activity instanceof UniversalActivity) {
                return;
            }
            DokitIntent dokitIntent = new DokitIntent(FloatIconDokitView.class);
            dokitIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
            attach(dokitIntent);
            return;
        }


        //新建Activity
        if (mActivityDokitViews.get(activity) == null) {
            if (mGlobalSingleDokitViews == null) {
                LogHelper.e(TAG, "resumeAndAttachDokitViews 方法执行异常");
                return;
            }
            //将所有的popView添加到新建的Activity中去
            for (GlobalSingleDokitViewInfo dokitViewInfo : mGlobalSingleDokitViews.values()) {
                LogHelper.i(TAG, " 新建activity==>" + activity.getClass().getSimpleName() + "  popView==>" + dokitViewInfo.getTag());
                if (activity instanceof UniversalActivity && dokitViewInfo.getAbsDokitViewClass() != PerformanceDokitView.class) {
                    return;
                }
                DokitIntent dokitIntent = new DokitIntent(dokitViewInfo.getAbsDokitViewClass());
                dokitIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
                dokitIntent.bundle = dokitViewInfo.getBundle();
                attach(dokitIntent);
            }
            return;
        }

        //activity resume
        //更新所有popView的位置
        Map<String, AbsDokitView> existDokitViews = mActivityDokitViews.get(activity);
        for (GlobalSingleDokitViewInfo traverseDokitViewInfo : mGlobalSingleDokitViews.values()) {
            if (activity instanceof UniversalActivity && traverseDokitViewInfo.getAbsDokitViewClass() != PerformanceDokitView.class) {
                return;
            }
            LogHelper.i(TAG, " activity  resume==>" + activity.getClass().getSimpleName() + "  dokitView==>" + traverseDokitViewInfo.getTag());
            //判断resume Activity 中时候存在指定的popview
            AbsDokitView existDokitView = existDokitViews.get(traverseDokitViewInfo.getTag());
            if (existDokitView != null && existDokitView.getRootView() != null) {
                existDokitView.getRootView().setVisibility(View.VISIBLE);
                existDokitView.updateViewLayout(existDokitView.getTag(), true);
                existDokitView.onResume();
            } else {
                //添加相应的
                DokitIntent dokitIntent = new DokitIntent(traverseDokitViewInfo.getAbsDokitViewClass());
                dokitIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
                dokitIntent.bundle = traverseDokitViewInfo.getBundle();
                attach(dokitIntent);
            }
        }

    }


    /**
     * 在当前Activity中添加指定悬浮窗
     *
     * @param dokitIntent
     */
    @Override
    public void attach(final DokitIntent dokitIntent) {
        try {
            if (dokitIntent.activity == null) {
                LogHelper.e(TAG, "activity = null");
                return;
            }
            if (dokitIntent.targetClass == null) {
                return;
            }


            //通过newInstance方式创建floatPage
            final AbsDokitView dokitView = dokitIntent.targetClass.newInstance();
            //判断当前Activity是否存在popView map
            Map<String, AbsDokitView> dokitViews;
            if (mActivityDokitViews.get(dokitIntent.activity) == null) {
                dokitViews = new HashMap<>();
                mActivityDokitViews.put(dokitIntent.activity, dokitViews);
            } else {
                dokitViews = mActivityDokitViews.get(dokitIntent.activity);
            }
            //判断该popview是否已经显示在页面上 同一个类型的popview 在页面上只显示一个
            if (dokitIntent.mode == DokitIntent.MODE_SINGLE_INSTANCE) {
                if (dokitViews.get(dokitIntent.getTag()) != null) {
                    LogHelper.i(TAG, dokitIntent.getTag() + "===>" + dokitViews.get(dokitIntent.getTag()).toString() + "  has attached");
                    //拿到指定的popView并更新位置
                    dokitViews.get(dokitIntent.getTag()).updateViewLayout(dokitIntent.getTag(), true);
                    return;
                }
            }

            //在当前Activity中保存floatPopView
            dokitViews.put(dokitView.getTag(), dokitView);
            dokitView.setBundle(dokitIntent.bundle);
            dokitView.setTag(dokitIntent.getTag());
            dokitView.setActivity(dokitIntent.activity);
            dokitView.performCreate(mContext);
            //在全局popviews中保存该类型的
            mGlobalSingleDokitViews.put(dokitView.getTag(), createGlobalSingleDokitViewInfo(dokitView));
            //得到activity window中的根布局
            final FrameLayout mDecorView = (FrameLayout) dokitIntent.activity.getWindow().getDecorView();


            //往DecorView的子RootView中添加floatPopView
            getDokitRootContentView(dokitIntent.activity, mDecorView)
                    .addView(dokitView.getRootView(),
                            dokitView.getNormalLayoutParams());
            //延迟100毫秒调用
            dokitView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dokitView.onResume();
                    //操作DecorRootView
                    dokitView.dealDecorRootView(getDokitRootContentView(dokitIntent.activity, mDecorView));
                }
            }, 100);


            LogHelper.i(TAG, "dokitView attach===>" + dokitIntent.activity.getClass().getSimpleName() + " ===>" + dokitView.toString());
        } catch (InstantiationException e) {
            LogHelper.e(TAG, e.toString());
        } catch (IllegalAccessException e) {
            LogHelper.e(TAG, e.toString());
        }
    }

//    private static final String DOKIT_ROOT_VIEW_TAG = "DokitRootView";

    /**
     * @return rootView
     */
    private FrameLayout getDokitRootContentView(final Activity activity, FrameLayout decorView) {
        FrameLayout dokitRootView = decorView.findViewById(R.id.dokit_contentview_id);
        if (dokitRootView != null) {
            return dokitRootView;
        }

        dokitRootView = new DokitFrameLayout(mContext);
        //普通模式的返回按键监听
        dokitRootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //LogHelper.i(TAG, "keyCode===>" + keyCode + " " + v.getClass().getSimpleName());
                //监听返回键
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Map<String, AbsDokitView> dokitViews = getDokitViews(activity);
                    if (dokitViews == null || dokitViews.size() == 0) {
                        return false;
                    }
                    for (AbsDokitView dokitView : dokitViews.values()) {
                        if (dokitView.shouldDealBackKey()) {
                            return dokitView.onBackPressed();
                        }
                    }
                    return false;
                }
                return false;
            }
        });
        dokitRootView.setClipChildren(false);
        //解决无法获取返回按键的问题
        dokitRootView.setFocusable(true);
        dokitRootView.setFocusableInTouchMode(true);
        dokitRootView.requestFocus();
        dokitRootView.setId(R.id.dokit_contentview_id);
        FrameLayout.LayoutParams dokitParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (BarUtils.isStatusBarVisible(activity)) {
            dokitParams.topMargin = BarUtils.getStatusBarHeight();
        }
        if (BarUtils.isSupportNavBar()) {
            if (BarUtils.isNavBarVisible(activity)) {
                dokitParams.bottomMargin = BarUtils.getNavBarHeight();
            }
        }
        dokitRootView.setLayoutParams(dokitParams);
        //添加到DecorView中 为了不和用户自己往根布局中添加view干扰
        decorView.addView(dokitRootView);

        return dokitRootView;
    }


    /**
     * 隐藏工具列表popView
     */
    public void detachKitDokitView() {
        detach(ToolPanelDokitView.class.getSimpleName());
    }

    /**
     * 移除每个activity指定的popView
     */
    @Override
    public void detach(AbsDokitView popView) {
        if (mActivityDokitViews == null) {
            return;
        }

        //调用当前Activity的指定PopView的Destroy方法
        //popView.performDestroy();

        detach(popView.getTag());

    }


    /**
     * 根据tag 移除ui和列表中的数据
     *
     * @param tag
     */
    @Override
    public void detach(String tag) {
        if (mActivityDokitViews == null) {
            return;
        }
        //移除每个activity中指定的popView
        for (Activity activityKey : mActivityDokitViews.keySet()) {
            Map<String, AbsDokitView> dokitViews = mActivityDokitViews.get(activityKey);
            //定位到指定popView
            AbsDokitView dokitView = dokitViews.get(tag);
            if (dokitView == null) {
                continue;
            }
            if (dokitView.getRootView() != null) {
                dokitView.getRootView().setVisibility(View.GONE);
                getDokitRootContentView(dokitView.getActivity(), (FrameLayout) activityKey.getWindow().getDecorView()).removeView(dokitView.getRootView());
            }

            //移除指定UI
            //请求重新绘制
            activityKey.getWindow().getDecorView().requestLayout();
            //执行popView的销毁
            dokitView.performDestroy();
            //移除map中的数据
            dokitViews.remove(tag);

        }
        //同步移除全局指定类型的popview
        if (mGlobalSingleDokitViews.containsKey(tag)) {
            mGlobalSingleDokitViews.remove(tag);
        }
        LogHelper.i(TAG, "popView detach====>" + tag);

    }

    @Override
    public void detach(Class<? extends AbsDokitView> dokitViewClass) {
        detach(dokitViewClass.getSimpleName());
    }


    /**
     * 移除所有activity的所有popView
     */
    @Override
    public void detachAll() {
        if (mActivityDokitViews == null) {
            return;
        }

        //移除每个activity中所有的popView
        for (Activity activityKey : mActivityDokitViews.keySet()) {
            Map<String, AbsDokitView> dokitViews = mActivityDokitViews.get(activityKey);
            //移除指定UI
            getDokitRootContentView(activityKey, (FrameLayout) activityKey.getWindow().getDecorView()).removeAllViews();
            //移除map中的数据
            dokitViews.clear();
        }
        mGlobalSingleDokitViews.clear();
        LogHelper.i(TAG, "popView detachAll====>");
    }

    /**
     * Activity销毁时调用
     */
    @Override
    public void onActivityDestroy(Activity activity) {
        if (mActivityDokitViews == null) {
            return;
        }
        Map<String, AbsDokitView> dokitViewMap = getDokitViews(activity);
        if (dokitViewMap == null) {
            return;
        }
        for (AbsDokitView dokitView : dokitViewMap.values()) {
            dokitView.performDestroy();
        }
        mActivityDokitViews.remove(activity);
    }


    /**
     * 获取当前页面指定的popView
     *
     * @param activity
     * @param tag
     * @return
     */
    @Override
    public AbsDokitView getDokitView(Activity activity, String tag) {
        if (TextUtils.isEmpty(tag) || activity == null) {
            return null;
        }
        if (mActivityDokitViews == null) {
            return null;
        }
        if (mActivityDokitViews.get(activity) == null) {
            return null;
        }
        return mActivityDokitViews.get(activity).get(tag);
    }


    /**
     * 获取当前页面所有的popView
     *
     * @param activity
     * @return
     */
    @Override
    public Map<String, AbsDokitView> getDokitViews(Activity activity) {
        if (activity == null) {
            return null;
        }
        if (mActivityDokitViews == null) {
            return null;
        }

        return mActivityDokitViews.get(activity);
    }


    private GlobalSingleDokitViewInfo createGlobalSingleDokitViewInfo(AbsDokitView dokitView) {
        return new GlobalSingleDokitViewInfo(dokitView.getClass(), dokitView.getTag(), DokitIntent.MODE_SINGLE_INSTANCE, dokitView.getBundle());
    }

}
