package com.didichuxing.doraemonkit.kit.timecounter;

import android.os.Looper;

import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.util.GsonUtils;
import com.didichuxing.doraemonkit.aop.DokitPluginConfig;
import com.didichuxing.doraemonkit.aop.method_stack.MethodStackUtil;
import com.didichuxing.doraemonkit.kit.health.AppHealthInfoUtil;
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo;
import com.didichuxing.doraemonkit.kit.methodtrace.AppHealthMethodCostBean;
import com.didichuxing.doraemonkit.kit.methodtrace.AppHealthMethodCostBeanWrap;
import com.didichuxing.doraemonkit.kit.timecounter.bean.CounterInfo;
import com.didichuxing.doraemonkit.kit.timecounter.counter.ActivityCounter;
import com.didichuxing.doraemonkit.kit.timecounter.counter.AppCounter;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc: App启动、Activity跳转的耗时统计类
 */
public class TimeCounterManager {
    private static final String TAG = "TimeCounterManager";
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
     * App attachBaseContext
     */
    public void onAppAttachBaseContextStart() {
        mAppCounter.attachStart();
    }


    /**
     * App attachBaseContext
     */
    public void onAppAttachBaseContextEnd() {
        mAppCounter.attachEnd();
    }

    /**
     * App 启动
     */
    public void onAppCreateStart() {
        mAppCounter.start();

    }

    /**
     * App 启动结束
     */
    public void onAppCreateEnd() {
        mAppCounter.end();
        CounterInfo counterInfo = getAppSetupInfo();
        if (DokitPluginConfig.VALUE_METHOD_STRATEGY == DokitPluginConfig.STRATEGY_STACK) {
            StringBuilder startInfo = new StringBuilder();
            startInfo.append(MethodStackUtil.STR_APP_ATTACH_BASECONTEXT);
            startInfo.append("\n");
            startInfo.append(MethodStackUtil.STR_APP_ON_CREATE);
            AppHealthInfoUtil.getInstance().setAppStartInfo(counterInfo.totalCost, startInfo.toString(), new ArrayList<AppHealthInfo.DataBean.AppStartBean.LoadFuncBean>());
        } else {
            List<AppHealthMethodCostBean> appHealthMethodCostBeans = new ArrayList<>();
            AppHealthMethodCostBean onCreate = new AppHealthMethodCostBean();
            onCreate.setCostTime(mAppCounter.getStartCountTime() + "ms");
            onCreate.setFunctionName("Application onCreate");
            appHealthMethodCostBeans.add(onCreate);
            AppHealthMethodCostBean onAttach = new AppHealthMethodCostBean();
            onAttach.setCostTime(mAppCounter.getAttachCountTime() + "ms");
            onAttach.setFunctionName("Application attachBaseContext");
            appHealthMethodCostBeans.add(onAttach);

            AppHealthMethodCostBeanWrap appHealthMethodCostBeanWrap = new AppHealthMethodCostBeanWrap();
            appHealthMethodCostBeanWrap.setTitle("App启动耗时");
            appHealthMethodCostBeanWrap.setData(appHealthMethodCostBeans);
            AppHealthInfoUtil.getInstance().setAppStartInfo(counterInfo.totalCost, GsonUtils.toJson(appHealthMethodCostBeanWrap), new ArrayList<AppHealthInfo.DataBean.AppStartBean.LoadFuncBean>());

        }

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
        DoKit.hideToolPanel();

        DoKit.launchFloating(TimeCounterDoKitView.class);

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
        DoKit.removeFloating(TimeCounterDoKitView.class);

    }

    public List<CounterInfo> getHistory() {
        return mActivityCounter.getHistory();
    }

    public CounterInfo getAppSetupInfo() {
        return mAppCounter.getAppSetupInfo();
    }
}
