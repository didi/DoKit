package com.didichuxing.doraemonkit.kit.core;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.DoKitConstant;
import com.didichuxing.doraemonkit.constant.WSMode;
import com.didichuxing.doraemonkit.kit.health.CountDownDokitView;
import com.didichuxing.doraemonkit.kit.main.MainIconDokitView;
import com.didichuxing.doraemonkit.kit.performance.PerformanceDokitView;
import com.didichuxing.doraemonkit.model.ActivityLifecycleInfo;
import com.didichuxing.doraemonkit.util.ActivityUtils;
import com.didichuxing.doraemonkit.util.BarUtils;
import com.didichuxing.doraemonkit.util.DoKitSystemUtil;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jintai on 2018/10/23.
 * 每个activity悬浮窗管理类
 */

class NormalDokitViewManager implements DokitViewManagerInterface {
    private static int MC_DELAY = 100;
    private static final String TAG = "NormalDokitViewManager";
    /**
     * 每个Activity中dokitView的集合
     */
    private Map<Activity, Map<String, AbsDokitView>> mActivityDokitViews;
    /**
     * 全局的同步mActivityFloatDokitViews 应该在页面上显示的dokitView集合
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
        //创建key为Activity的dokitView
        mActivityDokitViews = new HashMap<>();
        mGlobalSingleDokitViews = new HashMap<>();
    }


    /**
     * 添加activity关联的所有dokitView activity resume的时候回调
     *
     * @param activity
     */
    @Override
    public void resumeAndAttachDokitViews(Activity activity) {
        if (mActivityDokitViews == null) {
            return;
        }

        //app启动
        if (DoKitSystemUtil.isOnlyFirstLaunchActivity(activity)) {
            onMainActivityCreate(activity);
            return;
        }


        ActivityLifecycleInfo activityLifecycleInfo = DoKitConstant.ACTIVITY_LIFECYCLE_INFOS.get(activity.getClass().getCanonicalName());
        if (activityLifecycleInfo == null) {
            return;
        }
        //新建Activity
        if (activityLifecycleInfo.getActivityLifeCycleCount() == ActivityLifecycleInfo.ACTIVITY_LIFECYCLE_CREATE2RESUME) {
            onActivityCreate(activity);
            return;
        }

        //activity resume
        if (activityLifecycleInfo.getActivityLifeCycleCount() > ActivityLifecycleInfo.ACTIVITY_LIFECYCLE_CREATE2RESUME) {
            onActivityResume(activity);
        }

        //判断悬浮窗是否可见

    }

    /**
     * 应用启动
     */
    @Override
    public void onMainActivityCreate(Activity activity) {
        if (activity instanceof UniversalActivity) {
            return;
        }
        //倒计时DokitView
        attachCountDownDokitView(activity);

        if (!DoKitConstant.AWAYS_SHOW_MAIN_ICON) {
            DoKitConstant.MAIN_ICON_HAS_SHOW = false;
            return;
        }
        DokitIntent dokitIntent = new DokitIntent(MainIconDokitView.class);
        dokitIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        attach(dokitIntent);
        DoKitConstant.MAIN_ICON_HAS_SHOW = true;
    }

    /**
     * 新建activity
     *
     * @param activity
     */
    @Override
    public void onActivityCreate(Activity activity) {
        if (mGlobalSingleDokitViews == null) {
            LogHelper.e(TAG, "resumeAndAttachDokitViews 方法执行异常");
            return;
        }

        //将所有的dokitView添加到新建的Activity中去
        for (GlobalSingleDokitViewInfo dokitViewInfo : mGlobalSingleDokitViews.values()) {
            //如果不是性能kitView 则不显示
            if (activity instanceof UniversalActivity && dokitViewInfo.getAbsDokitViewClass() != PerformanceDokitView.class) {
                continue;
            }
            //是否过滤掉 入口icon
            if (!DoKitConstant.AWAYS_SHOW_MAIN_ICON && dokitViewInfo.getAbsDokitViewClass() == MainIconDokitView.class) {
                DoKitConstant.MAIN_ICON_HAS_SHOW = false;
                continue;
            }
            if (dokitViewInfo.getAbsDokitViewClass() == MainIconDokitView.class) {
                DoKitConstant.MAIN_ICON_HAS_SHOW = true;
            }

            DokitIntent dokitIntent = new DokitIntent(dokitViewInfo.getAbsDokitViewClass());
            dokitIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
            dokitIntent.bundle = dokitViewInfo.getBundle();
            attach(dokitIntent);
        }
        //判断是否有MainIcon
        if (DoKitConstant.AWAYS_SHOW_MAIN_ICON && !DoKit.isMainIconShow()) {
            DoKit.show();
        }

        //倒计时DokitView
        attachCountDownDokitView(activity);
    }

    /**
     * activity onResume
     *
     * @param activity
     */
    @Override
    public void onActivityResume(Activity activity) {
        if (mActivityDokitViews == null) {
            return;
        }
        Map<String, AbsDokitView> existDokitViews = mActivityDokitViews.get(activity);
        //先清除页面上启动模式为DokitIntent.MODE_ONCE 的dokitView
        if (existDokitViews != null) {
            //千万注意不要使用for循环去移除对象 下面注释的这段代码存在bug
//            for (AbsDokitView existDokitView : existDokitViews.values()) {
//                if (existDokitView.getMode() == DokitIntent.MODE_ONCE) {
//                    detach(existDokitView.getClass());
//                }
//            }
            List<String> modeOnceDokitViews = new ArrayList<>();
            for (AbsDokitView existDokitView : existDokitViews.values()) {
                if (existDokitView.getMode() == DokitIntent.MODE_ONCE) {
                    modeOnceDokitViews.add(existDokitView.getClass().getSimpleName());
                }
            }

            for (String tag : modeOnceDokitViews) {
                detach(tag);
            }

        }


        //更新所有全局DokitView的位置
        if (mGlobalSingleDokitViews != null && mGlobalSingleDokitViews.size() > 0) {
            for (GlobalSingleDokitViewInfo globalSingleDokitViewInfo : mGlobalSingleDokitViews.values()) {
                //如果不是性能kitView 则需要重新更新位置
                if (activity instanceof UniversalActivity && globalSingleDokitViewInfo.getAbsDokitViewClass() != PerformanceDokitView.class) {
                    continue;
                }
                //是否过滤掉 入口icon
                if (!DoKitConstant.AWAYS_SHOW_MAIN_ICON && globalSingleDokitViewInfo.getAbsDokitViewClass() == MainIconDokitView.class) {
                    DoKitConstant.MAIN_ICON_HAS_SHOW = false;
                    continue;
                }

                if (globalSingleDokitViewInfo.getAbsDokitViewClass() == MainIconDokitView.class) {
                    DoKitConstant.MAIN_ICON_HAS_SHOW = true;
                }

                //LogHelper.i(TAG, " activity  resume==>" + activity.getClass().getSimpleName() + "  dokitView==>" + globalSingleDokitViewInfo.getTag());
                //判断resume Activity 中时候存在指定的dokitview
                AbsDokitView existDokitView = null;
                if (existDokitViews != null && !existDokitViews.isEmpty()) {
                    existDokitView = existDokitViews.get(globalSingleDokitViewInfo.getTag());
                }

                //当前页面已存在dokitview
                if (existDokitView != null && existDokitView.getDoKitView() != null) {
                    existDokitView.getDoKitView().setVisibility(View.VISIBLE);
                    //更新位置
                    existDokitView.updateViewLayout(existDokitView.getTag(), true);
                    existDokitView.onResume();
                } else {
                    //添加相应的
                    DokitIntent dokitIntent = new DokitIntent(globalSingleDokitViewInfo.getAbsDokitViewClass());
                    dokitIntent.mode = globalSingleDokitViewInfo.getMode();
                    dokitIntent.bundle = globalSingleDokitViewInfo.getBundle();
                    attach(dokitIntent);
                }
            }

            if (!mGlobalSingleDokitViews.containsKey(MainIconDokitView.class.getSimpleName())) {
                attachMainIconDokitView(activity);
            }

        } else {
            //假如不存在全局的icon这需要全局显示主icon
            attachMainIconDokitView(activity);
        }

        attachCountDownDokitView(activity);
    }

    private void attachMainIconDokitView(Activity activity) {
        //假如不存在全局的icon这需要全局显示主icon
        if (DoKitConstant.AWAYS_SHOW_MAIN_ICON && !(activity instanceof UniversalActivity)) {
            DokitIntent dokitIntent = new DokitIntent(MainIconDokitView.class);
            dokitIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
            attach(dokitIntent);
        }
    }

    @Override
    public void onActivityPause(Activity activity) {
        Map<String, AbsDokitView> dokitViews = getDokitViews(activity);
        for (AbsDokitView absDokitView : dokitViews.values()) {
            absDokitView.onPause();
        }
    }

    /**
     * 添加倒计时DokitView
     */
    private void attachCountDownDokitView(Activity activity) {
        if (!DoKitConstant.APP_HEALTH_RUNNING) {
            return;
        }
        if (activity instanceof UniversalActivity) {
            return;
        }
        DokitIntent dokitIntent = new DokitIntent(CountDownDokitView.class);
        dokitIntent.mode = DokitIntent.MODE_ONCE;
        attach(dokitIntent);
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
            //判断当前Activity是否存在dokitView map
            Map<String, AbsDokitView> dokitViews;
            if (mActivityDokitViews.get(dokitIntent.activity) == null) {
                dokitViews = new HashMap<>();
                mActivityDokitViews.put(dokitIntent.activity, dokitViews);
            } else {
                dokitViews = mActivityDokitViews.get(dokitIntent.activity);
            }
            //判断该dokitview是否已经显示在页面上 同一个类型的dokitview 在页面上只显示一个
            if (dokitIntent.mode == DokitIntent.MODE_SINGLE_INSTANCE) {
                if (dokitViews.get(dokitIntent.getTag()) != null) {
                    //拿到指定的dokitView并更新位置
                    dokitViews.get(dokitIntent.getTag()).updateViewLayout(dokitIntent.getTag(), true);
                    return;
                }
            }

            //在当前Activity中保存dokitView
            dokitViews.put(dokitView.getTag(), dokitView);
            //设置dokitview的属性
            dokitView.setMode(dokitIntent.mode);
            dokitView.setBundle(dokitIntent.bundle);
            dokitView.setTag(dokitIntent.getTag());
            dokitView.setActivity(dokitIntent.activity);
            dokitView.performCreate(mContext);
            //在全局dokitviews中保存该类型的
            if (dokitIntent.mode == DokitIntent.MODE_SINGLE_INSTANCE) {
                if (mGlobalSingleDokitViews != null) {
                    mGlobalSingleDokitViews.put(dokitView.getTag(), createGlobalSingleDokitViewInfo(dokitView));
                }
            }
            //得到activity window中的根布局
            //final ViewGroup mDecorView = getDecorView(dokitIntent.activity);

            //往DecorView的子RootView中添加dokitView
            if (dokitView.getNormalLayoutParams() != null && dokitView.getDoKitView() != null) {
                getDoKitRootContentView(dokitIntent.activity)
                        .addView(dokitView.getDoKitView(),
                                dokitView.getNormalLayoutParams());
                //延迟100毫秒调用
                dokitView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dokitView.onResume();
                        //操作DecorRootView
                        dokitView.dealDecorRootView(getDoKitRootContentView(dokitIntent.activity));
                    }
                }, MC_DELAY);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private static final String DOKIT_ROOT_VIEW_TAG = "DokitRootView";

    /**
     * @return rootView
     */
    private FrameLayout getDoKitRootContentView(final Activity activity) {
        ViewGroup decorView = getDecorView(activity);
        FrameLayout dokitRootView = decorView.findViewById(R.id.dokit_contentview_id);
        if (dokitRootView != null) {
            return dokitRootView;
        }

        dokitRootView = new DokitFrameLayout(mContext, DokitFrameLayout.DoKitFrameLayoutFlag_ROOT);
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
        dokitRootView.setClipToPadding(false);

        //解决无法获取返回按键的问题
        dokitRootView.setFocusable(true);
        dokitRootView.setFocusableInTouchMode(true);
        dokitRootView.requestFocus();
        dokitRootView.setId(R.id.dokit_contentview_id);
        FrameLayout.LayoutParams dokitParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        try {
            //解决由于项目集成SwipeBackLayout而出现的dokit入口不显示
            if (BarUtils.isStatusBarVisible(activity)) {
                dokitParams.topMargin = BarUtils.getStatusBarHeight();
            }
            if (BarUtils.isSupportNavBar()) {
                if (BarUtils.isNavBarVisible(activity)) {
                    dokitParams.bottomMargin = BarUtils.getNavBarHeight();
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        dokitParams.gravity = Gravity.BOTTOM;
        dokitRootView.setLayoutParams(dokitParams);
        //添加到DecorView中 为了不和用户自己往根布局中添加view干扰
        decorView.addView(dokitRootView);

        return dokitRootView;
    }


    /**
     * 移除每个activity指定的dokitView
     */
    @Override
    public void detach(AbsDokitView dokitView) {
        if (mActivityDokitViews == null) {
            return;
        }

        //调用当前Activity的指定dokitView的Destroy方法
        //dokitView.performDestroy();

        detach(dokitView.getTag());

    }

    @Override
    public void detach(Activity activity, AbsDokitView dokitView) {
        detach(activity, dokitView.getTag());
    }


    /**
     * 根据tag 移除ui和列表中的数据
     *
     * @param tag
     */
    @Override
    public void detach(final String tag) {
        if (mActivityDokitViews == null) {
            return;
        }

        if (DoKitConstant.INSTANCE.getWS_MODE() == WSMode.HOST) {
            getDecorView(ActivityUtils.getTopActivity()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    realDetach(tag);
                }
            }, MC_DELAY);
        } else {
            realDetach(tag);
        }
    }


    private void realDetach(String tag) {
        //移除每个activity中指定的dokitView
        for (Activity activityKey : mActivityDokitViews.keySet()) {
            Map<String, AbsDokitView> dokitViews = mActivityDokitViews.get(activityKey);
            //定位到指定dokitView
            AbsDokitView dokitView = dokitViews.get(tag);
            if (dokitView == null) {
                continue;
            }


            if (dokitView.getDoKitView() != null) {
                dokitView.getDoKitView().setVisibility(View.GONE);
                getDoKitRootContentView(dokitView.getActivity()).removeView(dokitView.getDoKitView());
            }

            //移除指定UI
            //请求重新绘制
            getDecorView(activityKey).requestLayout();
            //执行dokitView的销毁
            dokitView.performDestroy();
            //移除map中的数据
            dokitViews.remove(tag);

        }
        //同步移除全局指定类型的dokitView
        if (mGlobalSingleDokitViews != null && mGlobalSingleDokitViews.containsKey(tag)) {
            mGlobalSingleDokitViews.remove(tag);
        }
    }

    @Override
    public void detach(Activity activity, String tag) {
        if (activity == null) {
            return;
        }
        detach(tag);
//        Map<String, AbsDokitView> dokitViews = mActivityDokitViews.get(activity);
//        if (dokitViews == null) {
//            return;
//        }
//        //定位到指定dokitView
//        AbsDokitView dokitView = dokitViews.get(tag);
//        if (dokitView == null) {
//            return;
//        }
//        if (dokitView.getDoKitView() != null) {
//            dokitView.getDoKitView().setVisibility(View.GONE);
//            getDokitRootContentView(dokitView.getActivity(), (FrameLayout) activity.getWindow().getDecorView()).removeView(dokitView.getDoKitView());
//        }
//
//        //移除指定UI
//        //请求重新绘制
//        activity.getWindow().getDecorView().requestLayout();
//        //执行dokitView的销毁
//        dokitView.performDestroy();
//        //移除map中的数据
//        dokitViews.remove(tag);
//
//        if (mGlobalSingleDokitViews != null && mGlobalSingleDokitViews.containsKey(tag)) {
//            mGlobalSingleDokitViews.remove(tag);
//        }
    }

    @Override
    public void detach(Class<? extends AbsDokitView> dokitViewClass) {
        detach(dokitViewClass.getSimpleName());
    }

    @Override
    public void detach(Activity activity, Class<? extends AbsDokitView> dokitViewClass) {
        detach(activity, dokitViewClass.getSimpleName());
    }


    /**
     * 移除所有activity的所有dokitView
     */
    @Override
    public void detachAll() {
        if (mActivityDokitViews == null) {
            return;
        }

        //移除每个activity中所有的dokitView
        for (Activity activityKey : mActivityDokitViews.keySet()) {
            Map<String, AbsDokitView> dokitViews = mActivityDokitViews.get(activityKey);
            //移除指定UI
            getDoKitRootContentView(activityKey).removeAllViews();
            //移除map中的数据
            dokitViews.clear();
        }
        if (mGlobalSingleDokitViews != null) {

            mGlobalSingleDokitViews.clear();
        }
    }

    /**
     * Activity销毁时调用
     */
    @Override
    public void onActivityDestroy(Activity activity) {
        if (mActivityDokitViews == null) {
            return;
        }
        //移除dokit根布局
        View dokitRootView = activity.findViewById(R.id.dokit_contentview_id);
        if (dokitRootView != null) {
            getDecorView(activity).removeView(dokitRootView);
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
     * 获取页面根布局
     *
     * @param activity
     * @return
     */
    private ViewGroup getDecorView(Activity activity) {
        return (ViewGroup) activity.getWindow().getDecorView();
    }

    /**
     * 获取当前页面指定的dokitView
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
     * 获取当前页面所有的dokitView
     *
     * @param activity
     * @return
     */
    @NonNull
    @Override
    public Map<String, AbsDokitView> getDokitViews(Activity activity) {
        if (activity == null) {
            return Collections.emptyMap();
        }
        if (mActivityDokitViews == null) {
            return Collections.emptyMap();
        }

        return mActivityDokitViews.get(activity) == null ? Collections.<String, AbsDokitView>emptyMap() : mActivityDokitViews.get(activity);
    }


    private GlobalSingleDokitViewInfo createGlobalSingleDokitViewInfo(AbsDokitView dokitView) {
        return new GlobalSingleDokitViewInfo(dokitView.getClass(), dokitView.getTag(), DokitIntent.MODE_SINGLE_INSTANCE, dokitView.getBundle());
    }

}
