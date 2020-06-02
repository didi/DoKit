package com.didichuxing.doraemonkit.kit.timecounter.counter;

import com.didichuxing.doraemonkit.kit.timecounter.bean.CounterInfo;

/**
 * @desc: App启动耗时
 */
public class AppCounter {

    private long mStartTime;
    private long mStartCountTime;
    private long mAttachTime;
    private long mAttachCountTime;

    public long getStartCountTime() {
        return mStartCountTime;
    }


    public long getAttachCountTime() {
        return mAttachCountTime;
    }



    private CounterInfo mCounterInfo = new CounterInfo();

    public void start() {
        mStartTime = System.currentTimeMillis();
    }

    public void attachStart() {
        mAttachTime = System.currentTimeMillis();
    }

    public void attachEnd() {
        mAttachCountTime = System.currentTimeMillis() - mAttachTime;
    }

    public void end() {
        mStartCountTime = System.currentTimeMillis() - mStartTime;
        account();
    }

    public void account() {
        mCounterInfo.title = "App Setup Cost";
        mCounterInfo.totalCost = mAttachCountTime + mStartCountTime;
        mCounterInfo.type = CounterInfo.TYPE_APP;
        mCounterInfo.time = System.currentTimeMillis();
    }

    public CounterInfo getAppSetupInfo() {
        return mCounterInfo;
    }
}
