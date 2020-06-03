package com.didichuxing.doraemonkit;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.didichuxing.doraemonkit.constant.DokitConstant;
import com.didichuxing.doraemonkit.datapick.DataPickManager;
import com.didichuxing.doraemonkit.kit.health.AppHealthInfoUtil;
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo;
import com.didichuxing.doraemonkit.kit.uiperformance.UIPerformanceUtil;
import com.didichuxing.doraemonkit.model.ActivityLifecycleInfo;
import com.didichuxing.doraemonkit.model.ViewInfo;
import com.didichuxing.doraemonkit.kit.core.DokitViewManager;
import com.didichuxing.doraemonkit.util.LifecycleListenerUtil;
import com.didichuxing.doraemonkit.util.PermissionUtil;
import com.didichuxing.doraemonkit.util.UIUtils;

import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-31-10:04
 * 描    述：全局的activity生命周期回调
 * 修订历史：
 * ================================================
 */
class DokitActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    private int startedActivityCounts;
    private boolean sHasRequestPermission;
    /**
     * fragment 生命周期回调
     */
    private FragmentManager.FragmentLifecycleCallbacks sFragmentLifecycleCallbacks;

    DokitActivityLifecycleCallbacks() {
        sFragmentLifecycleCallbacks = new DokitFragmentLifecycleCallbacks();
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        recordActivityLifeCycleStatus(activity, LIFE_CYCLE_STATUS_CREATE);
        if (ignoreCurrentActivityDokitView(activity)) {
            return;
        }
        if (activity instanceof FragmentActivity) {
            //注册fragment生命周期回调
            ((FragmentActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(sFragmentLifecycleCallbacks, true);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (ignoreCurrentActivityDokitView(activity)) {
            return;
        }
        if (startedActivityCounts == 0) {
            DokitViewManager.getInstance().notifyForeground();

        }
        startedActivityCounts++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        recordActivityLifeCycleStatus(activity, LIFE_CYCLE_STATUS_RESUME);
        //记录页面层级
        if (!activity.getClass().getCanonicalName().equals("com.didichuxing.doraemonkit.kit.base.UniversalActivity")) {
            recordActivityUiLevel(activity);
        }
        //如果是leakCanary页面不进行添加
        if (ignoreCurrentActivityDokitView(activity)) {
            return;
        }


        //设置app的直接子view的Id
        UIUtils.getDokitAppContentView(activity);
        //添加DokitView
        resumeAndAttachDokitViews(activity);

        for (LifecycleListenerUtil.LifecycleListener listener : LifecycleListenerUtil.LIFECYCLE_LISTENERS) {
            listener.onActivityResumed(activity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (ignoreCurrentActivityDokitView(activity)) {
            return;
        }
        for (LifecycleListenerUtil.LifecycleListener listener : LifecycleListenerUtil.LIFECYCLE_LISTENERS) {
            listener.onActivityPaused(activity);
        }

        DokitViewManager.getInstance().onActivityPause(activity);
    }


    @Override
    public void onActivityStopped(Activity activity) {
        recordActivityLifeCycleStatus(activity, LIFE_CYCLE_STATUS_STOPPED);
        if (ignoreCurrentActivityDokitView(activity)) {
            return;
        }
        startedActivityCounts--;
        //通知app退出到后台
        if (startedActivityCounts == 0) {
            DokitViewManager.getInstance().notifyBackground();
            //app 切换到后台 上传埋点数据
            DataPickManager.getInstance().postData();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        if (ignoreCurrentActivityDokitView(activity)) {
            return;
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        recordActivityLifeCycleStatus(activity, LIFE_CYCLE_STATUS_DESTROY);
        if (ignoreCurrentActivityDokitView(activity)) {
            return;
        }
        //注销fragment的生命周期回调
        if (activity instanceof FragmentActivity) {
            ((FragmentActivity) activity).getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(sFragmentLifecycleCallbacks);
        }
        DokitViewManager.getInstance().onActivityDestroy(activity);
    }

    /**
     * 是否忽略在当前的activity上显示浮标
     *
     * @param activity
     * @return
     */
    private static boolean ignoreCurrentActivityDokitView(Activity activity) {
        String[] ignoreActivityClassNames = new String[]{"DisplayLeakActivity"};
        for (String activityClassName : ignoreActivityClassNames) {
            if (activity.getClass().getSimpleName().equals(activityClassName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 显示所有应该显示的dokitView
     *
     * @param activity
     */
    private void resumeAndAttachDokitViews(Activity activity) {
        if (DokitConstant.IS_NORMAL_FLOAT_MODE) {
            //显示内置dokitView icon
            DokitViewManager.getInstance().resumeAndAttachDokitViews(activity);
        }
        //系统模式
        else {
            //悬浮窗权限 vivo 华为可以不需要动态权限 小米需要
            if (PermissionUtil.canDrawOverlays(activity)) {
                DokitViewManager.getInstance().resumeAndAttachDokitViews(activity);
            } else {
                //请求悬浮窗权限
                requestPermission(activity);
            }
        }


    }


    /**
     * 请求悬浮窗权限
     *
     * @param context
     */
    private void requestPermission(Context context) {
        if (!PermissionUtil.canDrawOverlays(context) && !sHasRequestPermission) {
            Toast.makeText(context, context.getText(R.string.dk_float_permission_toast), Toast.LENGTH_SHORT).show();
            //请求悬浮窗权限
            PermissionUtil.requestDrawOverlays(context);
            sHasRequestPermission = true;
        }
    }

    /**
     * Activity 创建
     */
    private static int LIFE_CYCLE_STATUS_CREATE = 100;
    /**
     * Activity resume
     */
    private static int LIFE_CYCLE_STATUS_RESUME = 101;
    /**
     * Activity stop
     */
    private static int LIFE_CYCLE_STATUS_STOPPED = 102;
    /**
     * Activity destroy
     */
    private static int LIFE_CYCLE_STATUS_DESTROY = 103;

    /**
     * 记录当前activity的UILevel
     *
     * @param activity
     */
    private void recordActivityUiLevel(Activity activity) {
        try {
            if (!DokitConstant.APP_HEALTH_RUNNING) {
                return;
            }

            List<ViewInfo> viewInfos = UIPerformanceUtil.getViewInfos(activity);
            int maxLevel = 0;
            float maxTime = 0f;
            float totalTime = 0f;
            ViewInfo maxLevelViewInfo = null;
            ViewInfo maxTimeViewInfo = null;
            for (ViewInfo viewInfo : viewInfos) {
                if (viewInfo.layerNum > maxLevel) {
                    maxLevel = viewInfo.layerNum;
                    maxLevelViewInfo = viewInfo;
                }
                if (viewInfo.drawTime > maxTime) {
                    maxTime = viewInfo.drawTime;
                    maxTimeViewInfo = viewInfo;
                }
                totalTime += viewInfo.drawTime;
            }

            String detail = "最大层级:" + maxLevel + "\n"
                    + "控件id:" + (maxLevelViewInfo == null ? "no id" : maxLevelViewInfo.id) + "\n"
                    + "总绘制耗时:" + totalTime + "ms" + "\n"
                    + "绘制耗时最长控件:" + maxTime + "ms" + "\n"
                    + "绘制耗时最长控件id:" + (maxTimeViewInfo == null ? "no id" : maxTimeViewInfo.id) + "\n";
            AppHealthInfo.DataBean.UiLevelBean uiLevelBean = new AppHealthInfo.DataBean.UiLevelBean();
            uiLevelBean.setPage(activity.getClass().getCanonicalName());
            uiLevelBean.setLevel("" + maxLevel);
            uiLevelBean.setDetail(detail);
            AppHealthInfoUtil.getInstance().addUiLevelInfo(uiLevelBean);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 记录当前Activity的生命周期状态
     */
    private void recordActivityLifeCycleStatus(Activity activity, int lifeCycleStatus) {
        ActivityLifecycleInfo activityLifecaycleInfo = DokitConstant.ACTIVITY_LIFECYCLE_INFOS.get(activity.getClass().getCanonicalName());
        if (activityLifecaycleInfo == null) {
            activityLifecaycleInfo = new ActivityLifecycleInfo();
            activityLifecaycleInfo.setActivityName(activity.getClass().getCanonicalName());
            if (lifeCycleStatus == LIFE_CYCLE_STATUS_CREATE) {
                activityLifecaycleInfo.setActivityLifeCycleCount(0);
            } else if (lifeCycleStatus == LIFE_CYCLE_STATUS_RESUME) {
                activityLifecaycleInfo.setActivityLifeCycleCount(activityLifecaycleInfo.getActivityLifeCycleCount() + 1);
            } else if (lifeCycleStatus == LIFE_CYCLE_STATUS_STOPPED) {
                activityLifecaycleInfo.setInvokeStopMethod(true);
            }
            DokitConstant.ACTIVITY_LIFECYCLE_INFOS.put(activity.getClass().getCanonicalName(), activityLifecaycleInfo);
        } else {
            activityLifecaycleInfo.setActivityName(activity.getClass().getCanonicalName());
            if (lifeCycleStatus == LIFE_CYCLE_STATUS_CREATE) {
                activityLifecaycleInfo.setActivityLifeCycleCount(0);
            } else if (lifeCycleStatus == LIFE_CYCLE_STATUS_RESUME) {
                activityLifecaycleInfo.setActivityLifeCycleCount(activityLifecaycleInfo.getActivityLifeCycleCount() + 1);
            } else if (lifeCycleStatus == LIFE_CYCLE_STATUS_STOPPED) {
                activityLifecaycleInfo.setInvokeStopMethod(true);
            } else if (lifeCycleStatus == LIFE_CYCLE_STATUS_DESTROY) {
                DokitConstant.ACTIVITY_LIFECYCLE_INFOS.remove(activity.getClass().getCanonicalName());
            }
        }
    }
}
