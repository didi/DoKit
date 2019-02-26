package com.didichuxing.doraemonkit.kit.timecounter.counter;

import com.didichuxing.doraemonkit.kit.timecounter.bean.CounterInfo;

/**
 * @desc: App启动耗时
 */
public class AppCounter {

    private long mStartTime;
    private long mCost;
    private CounterInfo mCounterInfo = new CounterInfo();

    public void start() {
        mStartTime = System.currentTimeMillis();
    }

    public void end() {
        mCost = System.currentTimeMillis() - mStartTime;
        mCounterInfo.title = "App Setup Cost";
        mCounterInfo.totalCost = mCost;
        mCounterInfo.type = CounterInfo.TYPE_APP;
        mCounterInfo.time = System.currentTimeMillis();
    }

    public long getTime() {
        return mCost;
    }

    public CounterInfo getAppSetupInfo() {
        return mCounterInfo;
    }
}
