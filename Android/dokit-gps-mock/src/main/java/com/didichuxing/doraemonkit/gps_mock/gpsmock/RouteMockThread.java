package com.didichuxing.doraemonkit.gps_mock.gpsmock;

import com.baidu.mapapi.model.LatLng;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.util.ThreadUtils;
import com.didichuxing.doraemonkit.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class RouteMockThread extends Thread {
    private static final String TAG = RouteMockThread.class.getSimpleName();
    private volatile boolean mIsMocking = false;
    private final Object mWaitMonitor = new Object();
    // 线程暂停标识
    private volatile boolean mSuspend = false;

    private int mIndex = 0;
    List<com.baidu.mapapi.model.LatLng> mPoints = new ArrayList<>();
    // 坐标点移动间隔时间
    private long mIntervalTime;
    private RouteMockStatusCallback mRouteMockStatusCallback;

    @Override
    public void run() {
        while (mPoints != null && mPoints.size() > 0 && mIndex < mPoints.size()) {
            mIsMocking = true;
            com.baidu.mapapi.model.LatLng latLng = mPoints.get(mIndex);
            LogHelper.d(TAG, "模拟导航=== step index: " + mIndex + " total steps " + mPoints.size() + " latitude " + latLng.latitude + " longitude " + latLng.longitude + " mIntervalTime " + mIntervalTime);
            ThreadUtils.runOnUiThread(() -> GpsMockManager.getInstance().performMock(new com.didichuxing.doraemonkit.model.LatLng(latLng.latitude, latLng.longitude)));

            mIndex++;
            synchronized (mWaitMonitor) {
                try {
                    if (mSuspend){
                        ToastUtils.showShort("暂停模拟");
                        // 暂停
                        mWaitMonitor.wait();
                        ToastUtils.showShort("继续模拟");
                    }else {
                        // 延时
                        mWaitMonitor.wait(mIntervalTime);
                    }
                }catch (Exception e){
                    reset();
                    LogHelper.d(TAG, "route mock thread wait error " + mSuspend + " " + e.getMessage());
                    break;
                }
            }
        }

        LogHelper.d(TAG, "模拟线程===");
        reset();
    }

    private void reset() {
        mIndex = 0;
        mIsMocking = false;
        ToastUtils.showShort("轨迹模拟已结束");
        if (mRouteMockStatusCallback != null) {
            ThreadUtils.runOnUiThread(() -> mRouteMockStatusCallback.onRouteMockFinish());
        }
    }

    /**
     *
     * @param suspend true: 暂停模拟; false:继续模拟.
     */
    public void notifyThread(boolean suspend) {
        if (!suspend) {
            synchronized (mWaitMonitor) {
                mWaitMonitor.notifyAll();
            }
        }
        this.mSuspend = suspend;
    }

    public boolean isSuspend() {
        return this.mSuspend;
    }

    public boolean isMocking() {
        return mIsMocking;
    }

    public void setMocking(boolean mocking) {
        mIsMocking = mocking;
    }

    public List<LatLng> getPoints() {
        return mPoints;
    }

    public void setPoints(List<LatLng> points) {
        mPoints.clear();
        mPoints.addAll(points);
    }

    public void setIntervalTime(long intervalTime) {
        mIntervalTime = intervalTime;
    }

    public long getIntervalTime() {
        return mIntervalTime;
    }

    public void setRouteMockStatusCallback(RouteMockStatusCallback routeMockStatusCallback) {
        mRouteMockStatusCallback = routeMockStatusCallback;
    }

    public void clearRouteMockStatusCallback(){
        mRouteMockStatusCallback = null;
    }

    public interface RouteMockStatusCallback{
        void onRouteMockFinish();
    }
}
