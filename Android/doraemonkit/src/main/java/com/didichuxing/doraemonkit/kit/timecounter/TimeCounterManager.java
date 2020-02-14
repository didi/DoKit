package com.didichuxing.doraemonkit.kit.timecounter;

import android.app.Application;
import android.os.Looper;
import android.util.Log;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.didichuxing.doraemonkit.constant.DokitConstant;
import com.didichuxing.doraemonkit.kit.health.AppHealthInfoUtil;
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo;
import com.didichuxing.doraemonkit.kit.methodtrace.AppHealthMethodCostBean;
import com.didichuxing.doraemonkit.kit.methodtrace.AppHealthMethodCostBeanWrap;
import com.didichuxing.doraemonkit.kit.methodtrace.MethodCostCallback;
import com.didichuxing.doraemonkit.kit.methodtrace.MethodCost;
import com.didichuxing.doraemonkit.kit.methodtrace.OrderBean;
import com.didichuxing.doraemonkit.kit.timecounter.bean.CounterInfo;
import com.didichuxing.doraemonkit.kit.timecounter.counter.ActivityCounter;
import com.didichuxing.doraemonkit.kit.timecounter.counter.AppCounter;
import com.didichuxing.doraemonkit.ui.base.DokitIntent;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;
import com.didichuxing.doraemonkit.util.FormatUtil;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @desc: App启动、Activity跳转的耗时统计类
 */
public class TimeCounterManager {
    //private static final String TAG = "TimeCounterManager";
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
        mAppCounter.end();
        MethodCost.stopMethodTracingAndPrintLog("appStart", new MethodCostCallback() {
            @Override
            public void onCall(String filePath, ArrayList<OrderBean> orderBeans) {
                try {
                    CounterInfo counterInfo = getAppSetupInfo();
                    List<AppHealthMethodCostBean> appHealthMethodCostBeans = new ArrayList<>();
                    for (OrderBean orderBean : orderBeans) {
                        long costTime = orderBean.getCostTime();
                        //过滤掉小于l ms的
                        if (costTime < 1000) {
                            continue;
                        }
                        //详细信息调用函数
                        AppHealthMethodCostBean appHealthMethodCostBean = new AppHealthMethodCostBean();
                        appHealthMethodCostBean.setCostTime(String.format("%.2f", orderBean.getCostTime() / 1000.00f) + "ms");
                        appHealthMethodCostBean.setFunctionName(orderBean.getFunctionName());
                        appHealthMethodCostBean.setThreadId(orderBean.getThreadId());
                        appHealthMethodCostBean.setThreadName(orderBean.getThreadName());
                        appHealthMethodCostBeans.add(appHealthMethodCostBean);

                    }

                    if (appHealthMethodCostBeans.isEmpty()) {
                        AppHealthMethodCostBean appHealthMethodCostBean = new AppHealthMethodCostBean();
                        appHealthMethodCostBean.setCostTime("-1");
                        appHealthMethodCostBean.setFunctionName("has no method costTime greater than 1000 ms");
                        appHealthMethodCostBean.setThreadId("-1");
                        appHealthMethodCostBean.setThreadName("-1");
                        appHealthMethodCostBeans.add(appHealthMethodCostBean);
                    }

                    AppHealthMethodCostBeanWrap appHealthMethodCostBeanWrap = new AppHealthMethodCostBeanWrap();
                    appHealthMethodCostBeanWrap.setTrace(filePath);
                    appHealthMethodCostBeanWrap.setData(appHealthMethodCostBeans);

                    AppHealthInfoUtil.getInstance().setAppStartInfo(counterInfo.totalCost, GsonUtils.toJson(appHealthMethodCostBeanWrap), new ArrayList<AppHealthInfo.DataBean.AppStartBean.LoadFuncBean>());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String message, String filePath) {
                CounterInfo counterInfo = getAppSetupInfo();
                List<AppHealthMethodCostBean> appHealthMethodCostBeans = new ArrayList<>();
                AppHealthMethodCostBean appHealthMethodCostBean = new AppHealthMethodCostBean();
                appHealthMethodCostBean.setCostTime("-1");
                appHealthMethodCostBean.setFunctionName("error===>" + message + " filePath===>" + filePath);
                appHealthMethodCostBean.setThreadId("-1");
                appHealthMethodCostBean.setThreadName("-1");
                appHealthMethodCostBeans.add(appHealthMethodCostBean);
                AppHealthMethodCostBeanWrap appHealthMethodCostBeanWrap = new AppHealthMethodCostBeanWrap();
                appHealthMethodCostBeanWrap.setTrace(filePath);
                appHealthMethodCostBeanWrap.setData(appHealthMethodCostBeans);
                AppHealthInfoUtil.getInstance().setAppStartInfo(counterInfo.totalCost, GsonUtils.toJson(appHealthMethodCostBeanWrap), new ArrayList<AppHealthInfo.DataBean.AppStartBean.LoadFuncBean>());
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
