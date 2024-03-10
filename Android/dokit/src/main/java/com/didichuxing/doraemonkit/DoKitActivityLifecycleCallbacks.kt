package com.didichuxing.doraemonkit

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.didichuxing.doraemonkit.datapick.DataPickManager
import com.didichuxing.doraemonkit.kit.core.ActivityLifecycleStatusInfo
import com.didichuxing.doraemonkit.kit.core.DoKitLifeCycleStatus
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.core.DoKitViewManager
import com.didichuxing.doraemonkit.kit.health.AppHealthInfoUtil
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo.DataBean.UiLevelBean
import com.didichuxing.doraemonkit.kit.uiperformance.UIPerformanceUtil
import com.didichuxing.doraemonkit.model.ViewInfo
import com.didichuxing.doraemonkit.util.DoKitPermissionUtil
import com.didichuxing.doraemonkit.util.LifecycleListenerUtil
import com.didichuxing.doraemonkit.util.UIUtils

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-31-10:04
 * 描    述：全局的activity生命周期回调
 * 修订历史：
 * ================================================
 */
class DoKitActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    private var startedActivityCounts = 0
    private var sHasRequestPermission = false
    //private Map<String, DoKitOrientationEventListener> mOrientationEventListeners = new HashMap<>();
    /**
     * fragment 生命周期回调
     */
    private val sFragmentLifecycleCallbacks: FragmentManager.FragmentLifecycleCallbacks

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        try {
            recordActivityLifeCycleStatus(activity, DoKitLifeCycleStatus.CREATED)
            if (ignoreCurrentActivityDokitView(activity)) {
                return
            }
            if (activity is FragmentActivity) {
                //注册fragment生命周期回调
                activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                    sFragmentLifecycleCallbacks,
                    true
                )
            }
            //暂时无法很好的解决屏幕旋转的问题
            //DoKitOrientationEventListener orientationEventListener = new DoKitOrientationEventListener(activity);
            //orientationEventListener.enable();
            //mOrientationEventListeners.put(activity.getClass().getSimpleName(), orientationEventListener);
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityStarted(activity: Activity) {
        try {
            if (ignoreCurrentActivityDokitView(activity)) {
                return
            }
            if (startedActivityCounts == 0) {
                DoKitViewManager.INSTANCE.notifyForeground()
            }
            startedActivityCounts++
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResumed(activity: Activity) {
        try {
            recordActivityLifeCycleStatus(activity, DoKitLifeCycleStatus.RESUME)
            //记录页面层级
            if (activity.javaClass.canonicalName != "com.didichuxing.doraemonkit.kit.base.UniversalActivity") {
                recordActivityUiLevel(activity)
            }
            //如果是leakCanary页面不进行添加
            if (ignoreCurrentActivityDokitView(activity)) {
                return
            }


            //设置app的直接子view的Id
            UIUtils.getDokitAppContentView(activity)
            dispatchOnActivityResumed(activity)
            for (listener in LifecycleListenerUtil.LIFECYCLE_LISTENERS) {
                listener.onActivityResumed(activity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityPaused(activity: Activity) {
        try {
            if (ignoreCurrentActivityDokitView(activity)) {
                return
            }
            for (listener in LifecycleListenerUtil.LIFECYCLE_LISTENERS) {
                listener.onActivityPaused(activity)
            }
            DoKitViewManager.INSTANCE.onActivityPaused(activity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityStopped(activity: Activity) {
        try {
            recordActivityLifeCycleStatus(activity, DoKitLifeCycleStatus.STOPPED)
            if (ignoreCurrentActivityDokitView(activity)) {
                return
            }
            startedActivityCounts--
            //通知app退出到后台
            if (startedActivityCounts == 0) {
                DoKitViewManager.INSTANCE.notifyBackground()
                //app 切换到后台 上传埋点数据
                DataPickManager.getInstance().postData()
            }
            DoKitViewManager.INSTANCE.onActivityStopped(activity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        try {
            if (ignoreCurrentActivityDokitView(activity)) {
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        try {
            recordActivityLifeCycleStatus(activity, DoKitLifeCycleStatus.DESTROYED)
            if (ignoreCurrentActivityDokitView(activity)) {
                return
            }
            //注销fragment的生命周期回调
            if (activity is FragmentActivity) {
                activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(
                    sFragmentLifecycleCallbacks
                )
            }
            DoKitViewManager.INSTANCE.onActivityDestroyed(activity)

            //暂时无法很好的解决屏幕旋转的问题
            //DoKitOrientationEventListener orientationEventListener = mOrientationEventListeners.get(activity.getClass().getSimpleName());

            //if (orientationEventListener != null) {
            //orientationEventListener.disable();
            //mOrientationEventListeners.remove(activity.getClass().getSimpleName());
            //}
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 显示所有应该显示的dokitView
     *
     * @param activity
     */
    private fun dispatchOnActivityResumed(activity: Activity) {
        activity.window.decorView.also {
            it.post { DoKitEnv.windowSize.set(it.width, it.height) }
        }
        if (DoKitManager.IS_NORMAL_FLOAT_MODE) {
            //显示内置dokitView icon
            DoKitViewManager.INSTANCE.dispatchOnActivityResumed(activity)
            return
        }
        // FIXME: consider handle permission down to activity-layer, just dispatch resumed-event here
        //悬浮窗权限 vivo 华为可以不需要动态权限 小米需要
        if (DoKitPermissionUtil.canDrawOverlays(activity)) {
            DoKitViewManager.INSTANCE.dispatchOnActivityResumed(activity)
        } else {
            //请求悬浮窗权限
            requestPermission(activity)
        }
    }

    /**
     * 请求悬浮窗权限
     *
     * @param context
     */
    private fun requestPermission(context: Context) {
        if (!DoKitPermissionUtil.canDrawOverlays(context) && !sHasRequestPermission) {
            Toast.makeText(
                context,
                context.getText(R.string.dk_float_permission_toast),
                Toast.LENGTH_SHORT
            ).show()
            //请求悬浮窗权限
            DoKitPermissionUtil.requestDrawOverlays(context)
            sHasRequestPermission = true
        }
    }

    /**
     * 记录当前activity的UILevel
     *
     * @param activity
     */
    private fun recordActivityUiLevel(activity: Activity) {
        try {
            if (!DoKitManager.APP_HEALTH_RUNNING) {
                return
            }
            val viewInfos = UIPerformanceUtil.getViewInfos(activity)
            var maxLevel = 0
            var maxTime = 0f
            var totalTime = 0f
            var maxLevelViewInfo: ViewInfo? = null
            var maxTimeViewInfo: ViewInfo? = null
            for (viewInfo in viewInfos) {
                if (viewInfo.layerNum > maxLevel) {
                    maxLevel = viewInfo.layerNum
                    maxLevelViewInfo = viewInfo
                }
                if (viewInfo.drawTime > maxTime) {
                    maxTime = viewInfo.drawTime
                    maxTimeViewInfo = viewInfo
                }
                totalTime += viewInfo.drawTime
            }
            val detail = """
                最大层级:$maxLevel
                控件id:${if (maxLevelViewInfo == null) "no id" else maxLevelViewInfo.id}
                总绘制耗时:${totalTime}ms
                绘制耗时最长控件:${maxTime}ms
                绘制耗时最长控件id:${if (maxTimeViewInfo == null) "no id" else maxTimeViewInfo.id}
                
                """.trimIndent()
            val uiLevelBean = UiLevelBean()
            uiLevelBean.page = activity.javaClass.canonicalName
            uiLevelBean.level = "" + maxLevel
            uiLevelBean.detail = detail
            AppHealthInfoUtil.getInstance().addUiLevelInfo(uiLevelBean)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 记录当前Activity的生命周期状态
     */
    private fun recordActivityLifeCycleStatus(
        activity: Activity,
        lifeCycleStatus: DoKitLifeCycleStatus
    ) {
        var activityLifeCycleStatusInfo: ActivityLifecycleStatusInfo? =
            DoKitManager.ACTIVITY_LIFECYCLE_INFOS[activity.javaClass.canonicalName]

        if (activityLifeCycleStatusInfo == null) {
            activityLifeCycleStatusInfo = ActivityLifecycleStatusInfo(
                isInvokeStopMethod = false,
                lifeCycleStatus = DoKitLifeCycleStatus.CREATED,
                activityName = activity.javaClass.canonicalName
            )

            DoKitManager.ACTIVITY_LIFECYCLE_INFOS[activity.javaClass.canonicalName!!] =
                activityLifeCycleStatusInfo
        }

        when (lifeCycleStatus) {
            DoKitLifeCycleStatus.CREATED -> {
                activityLifeCycleStatusInfo.lifeCycleStatus =
                    DoKitLifeCycleStatus.CREATED
            }
            DoKitLifeCycleStatus.RESUME -> {
                activityLifeCycleStatusInfo.lifeCycleStatus =
                    DoKitLifeCycleStatus.RESUME
            }
            DoKitLifeCycleStatus.STOPPED -> {
                activityLifeCycleStatusInfo.lifeCycleStatus =
                    DoKitLifeCycleStatus.STOPPED
                activityLifeCycleStatusInfo.isInvokeStopMethod = true
            }
            DoKitLifeCycleStatus.DESTROYED -> {
                DoKitManager.ACTIVITY_LIFECYCLE_INFOS.remove(activity.javaClass.canonicalName)
            }

        }
    }

    companion object {
        private const val TAG = "ActivityLifecycleCallback"

        /**
         * 是否忽略在当前的activity上显示浮标
         *
         * @param activity
         * @return
         */
        private fun ignoreCurrentActivityDokitView(activity: Activity): Boolean {
            val ignoreActivityClassNames = arrayOf("DisplayLeakActivity")
            for (activityClassName in ignoreActivityClassNames) {
                if (activity.javaClass.simpleName == activityClassName) {
                    return true
                }
            }
            return false
        }
    }

    init {
        sFragmentLifecycleCallbacks = DoKitFragmentLifecycleCallbacks()
    }
}
