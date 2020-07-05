package com.didichuxing.doraemonkit

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.kit.health.AppHealthInfoUtil
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo
import com.didichuxing.doraemonkit.kit.uiperformance.UIPerformanceUtil
import com.didichuxing.doraemonkit.model.ActivityLifecycleInfo
import com.didichuxing.doraemonkit.model.ViewInfo
import com.didichuxing.doraemonkit.util.LifecycleListenerUtil
import com.didichuxing.doraemonkit.util.LogHelper
import com.didichuxing.doraemonkit.util.PermissionUtil
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
internal class DokitActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    private val TAG = "DokitActivityLifecycleCallbacks"
    private var startedActivityCounts = 0
    private var sHasRequestPermission = false

    /**
     * fragment 生命周期回调
     */
    private val sFragmentLifecycleCallbacks: FragmentManager.FragmentLifecycleCallbacks = DokitFragmentLifecycleCallbacks()

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        activity?.let {
            recordActivityLifeCycleStatus(it, LIFE_CYCLE_STATUS_CREATE)
            if (ignoreCurrentActivityDokitView(it)) {
                return
            }
            if (it is FragmentActivity) {
                //注册fragment生命周期回调
                it.supportFragmentManager.registerFragmentLifecycleCallbacks(sFragmentLifecycleCallbacks, true)
            }
        }

    }

    override fun onActivityStarted(activity: Activity?) {
        activity?.let {
            if (ignoreCurrentActivityDokitView(it)) {
                return
            }
            if (startedActivityCounts == 0) {
                DokitViewManager.instance.notifyForeground()
            }
            startedActivityCounts++
        }

    }

    override fun onActivityResumed(activity: Activity?) {
        activity?.let {
            recordActivityLifeCycleStatus(it, LIFE_CYCLE_STATUS_RESUME)
            //记录页面层级 健康体检需要
            if (it.javaClass.canonicalName != "com.didichuxing.doraemonkit.kit.base.UniversalActivity") {
                recordActivityUiLevel(activity)
            }
            //如果是leakCanary页面不进行添加
            if (ignoreCurrentActivityDokitView(it)) {
                return
            }


            //设置app的直接子view的Id
            UIUtils.getDokitAppContentView(it)
            //添加DokitView
            resumeAndAttachDokitViews(it)
            for (listener in LifecycleListenerUtil.LIFECYCLE_LISTENERS) {
                listener.onActivityResumed(it)
            }
        }

    }

    override fun onActivityPaused(activity: Activity?) {
        activity?.let {
            if (ignoreCurrentActivityDokitView(it)) {
                return
            }
            for (listener in LifecycleListenerUtil.LIFECYCLE_LISTENERS) {
                listener.onActivityPaused(it)
            }
            DokitViewManager.instance.onActivityPause(it)
        }

    }

    override fun onActivityStopped(activity: Activity?) {
        activity?.let {
            recordActivityLifeCycleStatus(it, LIFE_CYCLE_STATUS_STOPPED)
            if (ignoreCurrentActivityDokitView(it)) {
                return
            }
            startedActivityCounts--
            //通知app退出到后台
            if (startedActivityCounts == 0) {
                DokitViewManager.instance.notifyBackground()
                //app 切换到后台 上传埋点数据
                //TODO("功能需要实现")
                //DataPickManager.getInstance().postData()
            }
        }

    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        activity?.let {
            if (ignoreCurrentActivityDokitView(it)) {
                return
            }
        }

    }

    override fun onActivityDestroyed(activity: Activity?) {
        activity?.let {
            recordActivityLifeCycleStatus(it, LIFE_CYCLE_STATUS_DESTROY)
            if (ignoreCurrentActivityDokitView(it)) {
                return
            }
            //注销fragment的生命周期回调
            if (it is FragmentActivity) {
                it.supportFragmentManager.unregisterFragmentLifecycleCallbacks(sFragmentLifecycleCallbacks)
            }
            DokitViewManager.instance.onActivityDestroy(activity)
        }

    }

    /**
     * 显示所有应该显示的dokitView
     *
     * @param activity
     */
    private fun resumeAndAttachDokitViews(activity: Activity?) {
        activity?.let {
            if (DokitConstant.IS_NORMAL_FLOAT_MODE) {
                //显示内置dokitView icon
                DokitViewManager.instance.resumeAndAttachDokitViews(it)
            } else {
                //悬浮窗权限 vivo 华为可以不需要动态权限 小米需要
                if (PermissionUtil.canDrawOverlays(it)) {
                    DokitViewManager.instance.resumeAndAttachDokitViews(it)
                } else {
                    //请求悬浮窗权限
                    requestPermission(it)
                }
            }
        }


    }

    /**
     * 请求悬浮窗权限
     *
     * @param context
     */
    private fun requestPermission(context: Context) {
        if (!PermissionUtil.canDrawOverlays(context) && !sHasRequestPermission) {
            Toast.makeText(context, context.getText(R.string.dk_float_permission_toast), Toast.LENGTH_SHORT).show()
            //请求悬浮窗权限
            PermissionUtil.requestDrawOverlays(context)
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
            if (!DokitConstant.APP_HEALTH_RUNNING) {
                return
            }
            val viewInfos = UIPerformanceUtil.getActivityViewInfo(activity)
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
            LogHelper.d(TAG,detail)
            //todo 需要完成上传数据到健康数据
            val uiLevelBean = AppHealthInfo.DataBean.UiLevelBean()
            uiLevelBean.page = activity.javaClass.canonicalName
            uiLevelBean.level = "" + maxLevel
            uiLevelBean.detail = detail
            AppHealthInfoUtil.instance.addUiLevelInfo(uiLevelBean)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 记录当前Activity的生命周期状态
     */
    private fun recordActivityLifeCycleStatus(activity: Activity, lifeCycleStatus: Int) {
        var activityLifecaycleInfo = DokitConstant.ACTIVITY_LIFECYCLE_INFOS[activity.javaClass.canonicalName]
        if (activityLifecaycleInfo == null) {
            activityLifecaycleInfo = ActivityLifecycleInfo()
            activityLifecaycleInfo.activityName = activity.javaClass.canonicalName
            if (lifeCycleStatus == LIFE_CYCLE_STATUS_CREATE) {
                activityLifecaycleInfo.activityLifeCycleCount = 0
            } else if (lifeCycleStatus == LIFE_CYCLE_STATUS_RESUME) {
                activityLifecaycleInfo.activityLifeCycleCount = activityLifecaycleInfo.activityLifeCycleCount + 1
            } else if (lifeCycleStatus == LIFE_CYCLE_STATUS_STOPPED) {
                activityLifecaycleInfo.isInvokeStopMethod = true
            }
            DokitConstant.ACTIVITY_LIFECYCLE_INFOS[activity.javaClass.canonicalName!!] = activityLifecaycleInfo
        } else {
            activityLifecaycleInfo.activityName = activity.javaClass.canonicalName
            if (lifeCycleStatus == LIFE_CYCLE_STATUS_CREATE) {
                activityLifecaycleInfo.activityLifeCycleCount = 0
            } else if (lifeCycleStatus == LIFE_CYCLE_STATUS_RESUME) {
                activityLifecaycleInfo.activityLifeCycleCount = activityLifecaycleInfo.activityLifeCycleCount + 1
            } else if (lifeCycleStatus == LIFE_CYCLE_STATUS_STOPPED) {
                activityLifecaycleInfo.isInvokeStopMethod = true
            } else if (lifeCycleStatus == LIFE_CYCLE_STATUS_DESTROY) {
                DokitConstant.ACTIVITY_LIFECYCLE_INFOS.remove(activity.javaClass.canonicalName)
            }
        }
    }

    companion object {
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

        /**
         * Activity 创建
         */
        private const val LIFE_CYCLE_STATUS_CREATE = 100

        /**
         * Activity resume
         */
        private const val LIFE_CYCLE_STATUS_RESUME = 101

        /**
         * Activity stop
         */
        private const val LIFE_CYCLE_STATUS_STOPPED = 102

        /**
         * Activity destroy
         */
        private const val LIFE_CYCLE_STATUS_DESTROY = 103
    }

}