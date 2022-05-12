package com.didichuxing.doraemonkit.kit.timecounter.counter;

import android.app.Activity;
import android.os.SystemClock;

import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.util.ActivityUtils;
import com.didichuxing.doraemonkit.kit.core.DoKitManager;
import com.didichuxing.doraemonkit.kit.health.AppHealthInfoUtil;
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo;
import com.didichuxing.doraemonkit.kit.timecounter.TimeCounterDoKitView;
import com.didichuxing.doraemonkit.kit.timecounter.bean.CounterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 统计一个打开Activity操作的耗时分三个阶段，以A打开B为例，第一个阶段是A的pause操作，主要是onPause方法的耗时，第二个阶段是B的Launch操作，主要是
 * onCreate和onResume方法的耗时，第三个阶段是B的Window的渲染操作，调用DecorView的post方法进行统计
 * 这里忽略了和AMS、WMS进行通讯的耗时，主要是因为这部分耗时无法通过非侵入式方式统计，而且用户也无法对这部分耗时做优化（除非重写Activity调用AMS的逻辑），
 * 总耗时=pause+launch+render+other
 */
public class ActivityCounter {

    private static final String TAG = "ActivityCounter";
    private long mStartTime;
    private long mPauseCostTime;
    private long mLaunchStartTime;
    private long mLaunchCostTime;
    private long mRenderStartTime;
    private long mRenderCostTime;
    private long mTotalCostTime;
    private long mOtherCostTime;

    private String mPreviousActivity;
    private String mCurrentActivity;
    private List<CounterInfo> mCounterInfos = new ArrayList<>();

    public void pause() {
        mStartTime = SystemClock.elapsedRealtime();
        mPauseCostTime = 0;
        mRenderCostTime = 0;
        mOtherCostTime = 0;
        mLaunchCostTime = 0;
        mLaunchStartTime = 0;
        mTotalCostTime = 0;
        mPreviousActivity = null;
        Activity activity = ActivityUtils.getTopActivity();
        if (activity != null) {
            mPreviousActivity = activity.getClass().getCanonicalName();
        }
    }

    public void paused() {
        mPauseCostTime = SystemClock.elapsedRealtime() - mStartTime;
        //LogHelper.d(TAG, "pause cost：" + mPauseCostTime);
    }

    public void launch() {
        // 可能不走pause，直接打开新页面，比如从后台点击通知栏
        if (mStartTime == 0) {
            mStartTime = SystemClock.elapsedRealtime();
            mPauseCostTime = 0;
            mRenderCostTime = 0;
            mOtherCostTime = 0;
            mLaunchCostTime = 0;
            mLaunchStartTime = 0;
            mTotalCostTime = 0;
        }
        mLaunchStartTime = SystemClock.elapsedRealtime();
        mLaunchCostTime = 0;
    }

    public void launchEnd() {
        mLaunchCostTime = SystemClock.elapsedRealtime() - mLaunchStartTime;
        //LogHelper.d(TAG, "create cost：" + mLaunchCostTime);
        render();
    }

    public void render() {
        mRenderStartTime = SystemClock.elapsedRealtime();
        final Activity activity = ActivityUtils.getTopActivity();
        if (activity != null && activity.getWindow() != null) {
            mCurrentActivity = activity.getClass().getCanonicalName();
            activity.getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    renderEnd();
                }
            });
        } else {
            renderEnd();
        }
    }

    /**
     * 用户退到后台，点击通知栏打开新页面，这时候需要清空下上次pause记录的时间
     */
    public void enterBackground() {
        mStartTime = 0;
    }

    private void renderEnd() {
        mRenderCostTime = SystemClock.elapsedRealtime() - mRenderStartTime;
        //LogHelper.d(TAG, "render cost：" + mRenderCostTime);
        mTotalCostTime = SystemClock.elapsedRealtime() - mStartTime;
        //LogHelper.d(TAG, "total cost：" + mTotalCostTime);
        mOtherCostTime = mTotalCostTime - mRenderCostTime - mPauseCostTime - mLaunchCostTime;
        print();
    }

    private void print() {
        CounterInfo counterInfo = new CounterInfo();
        counterInfo.time = SystemClock.elapsedRealtime();
        counterInfo.type = CounterInfo.TYPE_ACTIVITY;
        counterInfo.title = mPreviousActivity + " -> " + mCurrentActivity;
        counterInfo.launchCost = mLaunchCostTime;
        counterInfo.pauseCost = mPauseCostTime;
        counterInfo.renderCost = mRenderCostTime;
        counterInfo.totalCost = mTotalCostTime;
        counterInfo.otherCost = mOtherCostTime;
        try {
            //将Activity 打开耗时 添加到AppHealth 中
            if (DoKitManager.APP_HEALTH_RUNNING) {
                if (!ActivityUtils.getTopActivity().getClass().getCanonicalName().equals("com.didichuxing.doraemonkit.kit.base.UniversalActivity")) {
                    AppHealthInfo.DataBean.PageLoadBean pageLoadBean = new AppHealthInfo.DataBean.PageLoadBean();
                    pageLoadBean.setPage(ActivityUtils.getTopActivity().getClass().getCanonicalName());
                    pageLoadBean.setTime("" + counterInfo.totalCost);
                    pageLoadBean.setTrace(counterInfo.title);
                    AppHealthInfoUtil.getInstance().addPageLoadInfo(pageLoadBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mCounterInfos.add(counterInfo);

        TimeCounterDoKitView dokitView = DoKit.getDoKitView(ActivityUtils.getTopActivity(), TimeCounterDoKitView.class);
        if (dokitView != null) {
            dokitView.showInfo(counterInfo);
        }


    }

    public List<CounterInfo> getHistory() {
        return mCounterInfos;
    }
}
