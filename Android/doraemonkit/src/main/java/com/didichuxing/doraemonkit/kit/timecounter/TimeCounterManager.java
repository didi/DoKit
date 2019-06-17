package com.didichuxing.doraemonkit.kit.timecounter;

import android.os.Looper;

import com.didichuxing.doraemonkit.constant.PageTag;
import com.didichuxing.doraemonkit.kit.timecounter.bean.CounterInfo;
import com.didichuxing.doraemonkit.kit.timecounter.counter.ActivityCounter;
import com.didichuxing.doraemonkit.kit.timecounter.counter.AppCounter;
import com.didichuxing.doraemonkit.ui.base.FloatPageManager;
import com.didichuxing.doraemonkit.ui.base.PageIntent;

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

    public void onAppCreateStart() {
        mAppCounter.start();
    }

    public void onAppCreateEnd() {
        mAppCounter.end();
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
        PageIntent pageIntent = new PageIntent(TimeCounterFloatPage.class);
        pageIntent.tag = PageTag.PAGE_TIME_COUNTER;
        pageIntent.mode = PageIntent.MODE_SINGLE_INSTANCE;
        FloatPageManager.getInstance().add(pageIntent);
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
        FloatPageManager.getInstance().remove(PageTag.PAGE_TIME_COUNTER);
    }

    public List<CounterInfo> getHistory() {
        return mActivityCounter.getHistory();
    }

    public CounterInfo getAppSetupInfo() {
        return mAppCounter.getAppSetupInfo();
    }
}
