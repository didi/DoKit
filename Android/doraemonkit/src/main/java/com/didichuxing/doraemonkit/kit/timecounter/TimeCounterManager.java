package com.didichuxing.doraemonkit.kit.timecounter;

import android.app.Application;
import android.os.Looper;
import android.util.Log;

import com.blankj.utilcode.util.GsonUtils;
import com.didichuxing.doraemonkit.constant.DokitConstant;
import com.didichuxing.doraemonkit.kit.health.AppHealthInfoUtil;
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo;
import com.didichuxing.doraemonkit.kit.methodtrace.AppHealthMethodCostBean;
import com.didichuxing.doraemonkit.kit.methodtrace.MethodCostCallback;
import com.didichuxing.doraemonkit.kit.methodtrace.MethodCost;
import com.didichuxing.doraemonkit.kit.methodtrace.OrderBean;
import com.didichuxing.doraemonkit.kit.timecounter.bean.CounterInfo;
import com.didichuxing.doraemonkit.kit.timecounter.counter.ActivityCounter;
import com.didichuxing.doraemonkit.kit.timecounter.counter.AppCounter;
import com.didichuxing.doraemonkit.ui.base.DokitIntent;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @desc: App启动、Activity跳转的耗时统计类
 */
public class TimeCounterManager {
    private boolean mIsRunning;

    private static class Holder {
        private static TimeCounterManager INSTANCE = new TimeCounterManager();
    }

    public static TimeCounterManager get() {
        return TimeCounterManager.Holder.INSTANCE;
    }

    private AppCounter mAppCounter = new AppCounter();
    private ActivityCounter mActivityCounter = new ActivityCounter();

    /**
     * App 启动
     *
     * @param application
     */
    public void onAppCreateStart(Application application) {
//        Log.i("APP_STAR","=====onAppCreateStart======");
        mAppCounter.start();
        MethodCost.APPLICATION = application;
        MethodCost.startMethodTracing("appStart");
    }

    /**
     * App 启动结束
     *
     * @param application
     */
    public void onAppCreateEnd(Application application) {
//        Log.i("APP_STAR","=====onAppCreateEnd======");
        mAppCounter.end();
        MethodCost.stopMethodTracingAndPrintLog("appStart", new MethodCostCallback() {
            @Override
            public void onCall(ArrayList<OrderBean> orderBeans) {
                try {
                    CounterInfo counterInfo = getAppSetupInfo();
//                    List<AppHealthInfo.DataBean.AppStartBean.LoadFuncBean> loads = new ArrayList<>();
                    List<AppHealthMethodCostBean> appHealthMethodCostBeans = new ArrayList<>();
                    for (OrderBean orderBean : orderBeans) {
                        long costTime = Long.parseLong(orderBean.getCostTime());
                        if (costTime < 1000) {
                            continue;
                        }
                        //详细信息调用函数
                        AppHealthMethodCostBean appHealthMethodCostBean = new AppHealthMethodCostBean();
                        appHealthMethodCostBean.setCostTime(orderBean.getCostTime());
                        appHealthMethodCostBean.setFunctionName(orderBean.getFunctionName());
                        appHealthMethodCostBean.setThreadId(orderBean.getThreadId());
                        appHealthMethodCostBean.setThreadName(orderBean.getThreadName());
                        appHealthMethodCostBeans.add(appHealthMethodCostBean);
//                        AppHealthInfo.DataBean.AppStartBean.LoadFuncBean loadFuncBean = new AppHealthInfo.DataBean.AppStartBean.LoadFuncBean();
//                        loadFuncBean.setClassName(orderBean.getFunctionName());
//                        loadFuncBean.setCostTime(orderBean.getCostTime());
//                        loads.add(loadFuncBean);
                    }

                    if (appHealthMethodCostBeans.isEmpty()) {
                        AppHealthMethodCostBean appHealthMethodCostBean = new AppHealthMethodCostBean();
                        appHealthMethodCostBean.setCostTime("-1");
                        appHealthMethodCostBean.setFunctionName("has no method costTime greater than 1000");
                        appHealthMethodCostBean.setThreadId("-1");
                        appHealthMethodCostBean.setThreadName("-1");
                        appHealthMethodCostBeans.add(appHealthMethodCostBean);
                    }
                    AppHealthInfoUtil.getInstance().setAppStartInfo("" + counterInfo.totalCost, GsonUtils.toJson(appHealthMethodCostBeans), new ArrayList<AppHealthInfo.DataBean.AppStartBean.LoadFuncBean>());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String message) {

            }
        });

    }

    public void onActivityPause() {
        mActivityCounter.pause();
    }

    public void onActivityPaused() {
        mActivityCounter.paused();
    }

    public void onActivityLaunch() {
        mActivityCounter.launch();
    }

    public void onActivityLaunched() {
        mActivityCounter.launchEnd();
    }

    public void onEnterBackground() {
        mActivityCounter.enterBackground();
    }

    public void start() {
        if (mIsRunning) {
            return;
        }
        mIsRunning = true;
        DokitViewManager.getInstance().detachToolPanel();
        DokitIntent pageIntent = new DokitIntent(TimeCounterDokitView.class);
        pageIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManager.getInstance().attach(pageIntent);


    }

    public boolean isRunning() {
        return mIsRunning;
    }

    public void stop() {
        if (!mIsRunning) {
            return;
        }
        Looper.getMainLooper().setMessageLogging(null);
        mIsRunning = false;
        DokitViewManager.getInstance().detach(TimeCounterDokitView.class.getSimpleName());

    }

    public List<CounterInfo> getHistory() {
        return mActivityCounter.getHistory();
    }

    public CounterInfo getAppSetupInfo() {
        return mAppCounter.getAppSetupInfo();
    }
}
